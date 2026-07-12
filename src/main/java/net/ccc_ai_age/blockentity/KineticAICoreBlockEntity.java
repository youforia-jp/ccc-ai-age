package net.ccc_ai_age.blockentity;

import net.ccc_ai_age.ModBlockEntities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Block entity for the Kinetic AI Core.
 *
 * <p>Manages the peripheral lifecycle and runs asynchronous streaming requests
 * to a local Ollama LLM endpoint.
 */
public class KineticAICoreBlockEntity extends BlockEntity {

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(5))
			.build();

	private final IPeripheral peripheral;
	private final Set<IComputerAccess> attachedComputers = ConcurrentHashMap.newKeySet();
	private final Map<String, RequestContext> activeRequests = new ConcurrentHashMap<>();

	public KineticAICoreBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.KINETIC_AI_CORE, pos, state);
		this.peripheral = new KineticAICorePeripheral(this);
	}

	/**
	 * Gets the CC: Tweaked peripheral instance associated with this block entity.
	 *
	 * @return the peripheral instance
	 */
	public IPeripheral getPeripheral() {
		return this.peripheral;
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		// Cancel all in-flight requests when the block entity is broken or unloaded
		activeRequests.values().forEach(RequestContext::cancel);
		activeRequests.clear();
		attachedComputers.clear();
	}

	/**
	 * Implementation of {@link IPeripheral} for the Kinetic AI Core.
	 *
	 * <p>Bridges the Lua computer context with the asynchronous Java HTTP client.
	 */
	public static class KineticAICorePeripheral implements IPeripheral {

		private final KineticAICoreBlockEntity blockEntity;

		public KineticAICorePeripheral(KineticAICoreBlockEntity blockEntity) {
			this.blockEntity = blockEntity;
		}

		@Override
		public @NotNull String getType() {
			return "ai_core";
		}

		@Override
		public void attach(@NotNull IComputerAccess computer) {
			blockEntity.attachedComputers.add(computer);
		}

		@Override
		public void detach(@NotNull IComputerAccess computer) {
			blockEntity.attachedComputers.remove(computer);
			// Cancel all requests initiated by this specific computer
			blockEntity.activeRequests.values().forEach(context -> {
				if (context.getComputer().equals(computer)) {
					context.cancel();
				}
			});
		}

		@Override
		public boolean equals(@Nullable IPeripheral other) {
			if (this == other) return true;
			if (!(other instanceof KineticAICorePeripheral)) return false;
			return this.blockEntity == ((KineticAICorePeripheral) other).blockEntity;
		}

		/**
		 * Triggers an asynchronous generation request to a local Ollama server.
		 * Returns a unique request ID immediately, then streams tokens back to the
		 * computer as `ai_token` events.
		 *
		 * @param computer injected computer access
		 * @param prompt the prompt text to generate from
		 * @param model the model to use (defaults to "llama3" if null/empty)
		 * @return a unique 8-character request ID
		 */
		@LuaFunction
		public final String streamTelemetry(IComputerAccess computer, String prompt, @Nullable String model) {
			String selectedModel = (model != null && !model.trim().isEmpty()) ? model.trim() : "llama3";
			String requestId = UUID.randomUUID().toString().substring(0, 8);

			// Build Ollama JSON POST payload
			JsonObject payload = new JsonObject();
			payload.addProperty("model", selectedModel);
			payload.addProperty("prompt", prompt);
			payload.addProperty("stream", true);
			String jsonBody = new Gson().toJson(payload);

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("http://localhost:11434/api/generate"))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
					.build();

			CompletableFuture<HttpResponse<InputStream>> future = HTTP_CLIENT.sendAsync(
					request,
					HttpResponse.BodyHandlers.ofInputStream()
			);

			RequestContext context = new RequestContext(computer, future);
			blockEntity.activeRequests.put(requestId, context);

			future.thenAcceptAsync(response -> {
				if (response.statusCode() != 200) {
					computer.queueEvent("ai_token", requestId, "Error: Ollama returned status " + response.statusCode(), true);
					blockEntity.activeRequests.remove(requestId);
					return;
				}

				InputStream is = response.body();
				context.setInputStream(is);

				try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
					String line;
					while ((line = reader.readLine()) != null) {
						if (Thread.currentThread().isInterrupted() || context.isCancelled()) {
							break;
						}
						try {
							JsonObject json = new Gson().fromJson(line, JsonObject.class);
							if (json.has("response")) {
								String token = json.get("response").getAsString();
								boolean done = json.has("done") && json.get("done").getAsBoolean();
								computer.queueEvent("ai_token", requestId, token, done);
							}
						} catch (Exception e) {
							// Skip parsing error on corrupt/malformed line and continue
						}
					}
				} catch (IOException e) {
					if (!context.isCancelled()) {
						computer.queueEvent("ai_token", requestId, "Error: " + e.getMessage(), true);
					}
				} finally {
					blockEntity.activeRequests.remove(requestId);
				}
			}).exceptionally(ex -> {
				if (!context.isCancelled()) {
					computer.queueEvent("ai_token", requestId, "Connection failed: " + ex.getMessage(), true);
				}
				blockEntity.activeRequests.remove(requestId);
				return null;
			});

			return requestId;
		}
	}

	/**
	 * Thread-safe wrapper containing active request context to allow safe cancellation.
	 */
	private static class RequestContext {
		private final IComputerAccess computer;
		private final CompletableFuture<?> future;
		private volatile InputStream inputStream;
		private volatile boolean cancelled = false;

		public RequestContext(IComputerAccess computer, CompletableFuture<?> future) {
			this.computer = computer;
			this.future = future;
		}

		public IComputerAccess getComputer() {
			return computer;
		}

		public synchronized void setInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
			if (cancelled) {
				closeStream();
			}
		}

		public synchronized void cancel() {
			cancelled = true;
			future.cancel(true);
			closeStream();
		}

		public boolean isCancelled() {
			return cancelled;
		}

		private void closeStream() {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// Ignore stream close exception
				}
			}
		}
	}
}
