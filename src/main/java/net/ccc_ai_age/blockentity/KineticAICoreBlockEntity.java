package net.ccc_ai_age.blockentity;

import net.ccc_ai_age.ModBlockEntities;
import net.ccc_ai_age.api.AITier;
import net.ccc_ai_age.block.KineticAICoreBlock;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Block entity for the Kinetic AI Core.
 *
 * <p>Manages the peripheral lifecycle, tracks connected computers, and integrates
 * with adjacent Create mod networks using reflection to extract speed and stress data.
 */
public class KineticAICoreBlockEntity extends BlockEntity {

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(5))
			.build();

	private final IPeripheral peripheral;

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

	/**
	 * Extracts the tier of the AI core from its block state.
	 * 
	 * @return the AITier associated with this block entity
	 */
	public AITier getTier() {
		if (this.getCachedState().getBlock() instanceof KineticAICoreBlock) {
			return ((KineticAICoreBlock) this.getCachedState().getBlock()).getTier();
		}
		return AITier.BASIC; // fallback
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		// Cancel all in-flight requests when the block entity is broken or unloaded
		if (peripheral instanceof KineticAICorePeripheral) {
			((KineticAICorePeripheral) peripheral).cancelAllRequests();
		}
	}

	// =========================================================================
	// Create Mod Kinetic Reflection Helpers
	// =========================================================================

	/**
	 * Helper class to hold resolved kinetic data.
	 */
	public static class KineticData {
		public float speed = 0.0f;
		public float stress = 0.0f;
		public float capacity = 0.0f;
		public boolean hasNetwork = false;
	}

	/**
	 * Scans adjacent blocks for Create's KineticBlockEntity and extracts speed and network stress metrics.
	 * Using reflection avoids direct compile-time class dependencies on Create/Porting-Lib.
	 *
	 * @return a {@link KineticData} structure containing the highest speed and network capacity found
	 */
	public KineticData getAdjacentKineticData() {
		KineticData data = new KineticData();
		if (this.world == null) return data;

		for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.values()) {
			net.minecraft.util.math.BlockPos adjacentPos = this.pos.offset(dir);
			net.minecraft.block.entity.BlockEntity adjacentBe = this.world.getBlockEntity(adjacentPos);
			if (adjacentBe == null) continue;

			if (isKineticBlockEntity(adjacentBe)) {
				float speed = Math.abs(getSpeed(adjacentBe));
				// Track the fastest rotating adjacent source
				if (speed > data.speed) {
					data.speed = speed;
				}

				Object network = getNetwork(adjacentBe);
				if (network != null) {
					data.hasNetwork = true;
					float capacity = getNetworkCapacity(network);
					float stress = getNetworkStress(network);
					// Track the network with the largest capacity
					if (capacity > data.capacity) {
						data.capacity = capacity;
						data.stress = stress;
					}
				}
			}
		}
		return data;
	}

	private static boolean isKineticBlockEntity(net.minecraft.block.entity.BlockEntity be) {
		Class<?> clazz = be.getClass();
		while (clazz != null && clazz != Object.class) {
			if (clazz.getName().equals("com.simibubi.create.content.kinetics.base.KineticBlockEntity")) {
				return true;
			}
			clazz = clazz.getSuperclass();
		}
		return false;
	}

	private static float getSpeed(net.minecraft.block.entity.BlockEntity be) {
		try {
			Method method = be.getClass().getMethod("getSpeed");
			return ((Number) method.invoke(be)).floatValue();
		} catch (Exception e) {
			return 0.0f;
		}
	}

	private static Object getNetwork(net.minecraft.block.entity.BlockEntity be) {
		try {
			Method method = be.getClass().getMethod("getOrCreateNetwork");
			return method.invoke(be);
		} catch (Exception e) {
			try {
				Method method = be.getClass().getMethod("getNetwork");
				return method.invoke(be);
			} catch (Exception ex) {
				return null;
			}
		}
	}

	private static float getNetworkCapacity(Object network) {
		if (network == null) return 0.0f;
		// Try getCapacity() getter method first, fall back to public capacity field
		try {
			Method method = network.getClass().getMethod("getCapacity");
			return ((Number) method.invoke(network)).floatValue();
		} catch (Exception e) {
			try {
				Field field = network.getClass().getField("capacity");
				return ((Number) field.get(network)).floatValue();
			} catch (Exception ex) {
				return 0.0f;
			}
		}
	}

	private static float getNetworkStress(Object network) {
		if (network == null) return 0.0f;
		// Try getStress() getter method first, fall back to public stress field
		try {
			Method method = network.getClass().getMethod("getStress");
			return ((Number) method.invoke(network)).floatValue();
		} catch (Exception e) {
			try {
				Field field = network.getClass().getField("stress");
				return ((Number) field.get(network)).floatValue();
			} catch (Exception ex) {
				return 0.0f;
			}
		}
	}

	// =========================================================================
	// CC: Tweaked Peripheral Implementation
	// =========================================================================

	/**
	 * Implementation of {@link IPeripheral} for the Kinetic AI Core.
	 *
	 * <p>Bridges the Lua computer context with the asynchronous Java HTTP client and kinetic networks.
	 */
	public static class KineticAICorePeripheral implements IPeripheral {

		private final @Nullable KineticAICoreBlockEntity blockEntity;
		private final AITier tier;
		private final Set<IComputerAccess> attachedComputers = ConcurrentHashMap.newKeySet();
		private final Map<String, RequestContext> activeRequests = new ConcurrentHashMap<>();

		public KineticAICorePeripheral(KineticAICoreBlockEntity blockEntity) {
			this.blockEntity = blockEntity;
			this.tier = blockEntity.getTier();
		}

		public KineticAICorePeripheral(AITier tier) {
			this.blockEntity = null;
			this.tier = tier;
		}

		public void cancelAllRequests() {
			activeRequests.values().forEach(RequestContext::cancel);
			activeRequests.clear();
			attachedComputers.clear();
		}

		@Override
		public @NotNull String getType() {
			return "ai_core";
		}

		@Override
		public void attach(@NotNull IComputerAccess computer) {
			attachedComputers.add(computer);
		}

		@Override
		public void detach(@NotNull IComputerAccess computer) {
			attachedComputers.remove(computer);
			// Cancel all requests initiated by this specific computer
			activeRequests.values().forEach(context -> {
				if (context.getComputer().equals(computer)) {
					context.cancel();
				}
			});
		}

		@Override
		public boolean equals(@Nullable IPeripheral other) {
			if (this == other) return true;
			if (!(other instanceof KineticAICorePeripheral)) return false;
			KineticAICorePeripheral o = (KineticAICorePeripheral) other;
			if (this.blockEntity != null && o.blockEntity != null) {
				return this.blockEntity == o.blockEntity;
			}
			return this.blockEntity == null && o.blockEntity == null && this.tier == o.tier;
		}

		/**
		 * Reads kinetic network telemetry from adjacent blocks.
		 *
		 * @return a map containing speed, capacity, stress, load %, and status flags.
		 */
		@LuaFunction
		public final Map<String, Object> getKineticData() {
			float speed, stress, capacity;
			boolean isPowered;
			if (blockEntity != null) {
				KineticData data = blockEntity.getAdjacentKineticData();
				speed = data.speed;
				stress = data.stress;
				capacity = data.capacity;
				if (tier == AITier.QUANTUM) {
					isPowered = true;
				} else if (tier == AITier.ADVANCED) {
					isPowered = speed >= 16.0f;
				} else {
					isPowered = speed >= 32.0f;
				}
			} else {
				// Turtle upgrade fallback - always powered and fully functional!
				speed = 64.0f;
				stress = 0.0f;
				capacity = 256.0f;
				isPowered = true;
			}

			Map<String, Object> map = new HashMap<>();
			map.put("speed", speed);
			map.put("stress", stress);
			map.put("capacity", capacity);
			map.put("stressPercent", capacity > 0.0f ? (stress / capacity) * 100.0f : 0.0f);
			map.put("isPowered", isPowered);
			map.put("isOverstressed", capacity > 0.0f && stress >= capacity);
			return map;
		}

		/**
		 * Triggers an asynchronous generation request to a local Ollama server.
		 * Requires adjacent rotational force to run (minimum 16 RPM).
		 *
		 * @param computer injected computer access
		 * @param prompt the prompt text to generate from
		 * @param modelOpt the model to use
		 * @return a unique 8-character request ID
		 * @throws LuaException if unpowered or the network is overstressed
		 */
		@LuaFunction
		public final String streamTelemetry(IComputerAccess computer, String prompt, Optional<String> modelOpt) throws LuaException {
			String model = modelOpt.orElse(null);
			AITier tier = this.tier;
			
			// Enforce tier-based kinetic power requirements
			if (blockEntity != null && tier != AITier.QUANTUM) {
				KineticData data = blockEntity.getAdjacentKineticData();
				float requiredSpeed = (tier == AITier.ADVANCED) ? 16.0f : 32.0f;
				if (data.speed < requiredSpeed) {
					throw new LuaException(String.format("Kinetic AI Core (%s) is unpowered. Rotational force (minimum %.0f RPM) required on an adjacent block.", tier.name(), requiredSpeed));
				}
				if (data.capacity > 0.0f && data.stress >= data.capacity) {
					throw new LuaException("Kinetic AI Core has stalled due to adjacent kinetic network overstress.");
				}
			}

			// 1. DYNAMIC VRAM LOOKUP (v0.35, v0.36 override logic)
			long vramBytes = 0;
			try {
				oshi.SystemInfo si = new oshi.SystemInfo();
				for (oshi.hardware.GraphicsCard card : si.getHardware().getGraphicsCards()) {
					long cardVRam = card.getVRam();
					String cardName = card.getName() == null ? "" : card.getName().toLowerCase();
					
					// Fix the Windows 4GB VRAM Truncation Reporting (v0.36)
					if (cardVRam == 4294967295L || 
					    ((cardName.contains("super") || cardName.contains("ti") || cardName.contains("rtx") || 
					      cardName.contains("rx") || cardName.contains("2060") || cardName.contains("3060") || 
					      cardName.contains("4060")) && cardVRam < 8589934592L)) {
						cardVRam = Math.max(cardVRam, 8589934592L);
					}
					
					vramBytes += cardVRam;
				}
			} catch (Throwable t) {
				// Fallback if oshi fails
			}
			long vramGb = Math.round(vramBytes / (1024.0 * 1024.0 * 1024.0));

			// 2. THE UPDATED MODEL SELECTION MATRIX (v0.35)
			String normModel = (model == null) ? "" : model.trim().toLowerCase();
			String selectedModel;
			long modelVramGb = 0;
			String recommendation = null;

			if (tier == AITier.BASIC) {
				selectedModel = "qwen:0.5b";
				modelVramGb = 1;
			} else if (tier == AITier.ADVANCED) {
				if (normModel.isEmpty()) {
					selectedModel = "qwen3.5:4b";
				} else if (normModel.equals("qwen3.5:4b") || normModel.equals("qwen:0.5b")) {
					selectedModel = normModel;
				} else {
					throw new LuaException("Model '" + model + "' is not authorized for Advanced Tier. Authorized models: 'qwen3.5:4b', 'qwen:0.5b'.");
				}
				
				if (selectedModel.equals("qwen:0.5b")) {
					modelVramGb = 1;
				} else {
					modelVramGb = 3;
					recommendation = "qwen:0.5b";
				}
			} else { // QUANTUM TIER
				if (normModel.isEmpty()) {
					selectedModel = "qwen3.5:9b";
				} else if (normModel.equals("qwen3.5:9b") || normModel.equals("qwen2.5:14b") || normModel.equals("qwen3:8b") || normModel.equals("qwen3.5:4b") || normModel.equals("qwen:0.5b")) {
					selectedModel = normModel;
				} else {
					throw new LuaException("Model '" + model + "' is not authorized for Quantum Tier. Authorized models: 'qwen3.5:9b', 'qwen2.5:14b', 'qwen3:8b', 'qwen3.5:4b', 'qwen:0.5b'.");
				}

				if (selectedModel.equals("qwen:0.5b")) {
					modelVramGb = 1;
				} else if (selectedModel.equals("qwen3.5:4b")) {
					modelVramGb = 3;
					recommendation = "qwen:0.5b";
				} else if (selectedModel.equals("qwen3:8b")) {
					modelVramGb = 5;
					recommendation = "qwen3.5:4b";
				} else if (selectedModel.equals("qwen3.5:9b")) {
					modelVramGb = 7;
					recommendation = "qwen3:8b";
				} else { // qwen2.5:14b
					modelVramGb = 12;
					recommendation = "qwen3.5:9b";
				}
			}

			// 4. SYSTEM PROMPTS (v0.35)
			String finalPrompt;
			if (selectedModel.equals("qwen:0.5b")) {
				finalPrompt = "You are a primitive, low-powered computational matrix. Keep responses incredibly simple and short: " + prompt;
			} else {
				finalPrompt = prompt;
			}

			String requestId = UUID.randomUUID().toString().substring(0, 8);

			// 3. SYSTEM RECOMMENDATION INJECTION (v0.35)
			if (vramGb > 0 && (vramGb < modelVramGb || (vramGb - modelVramGb) <= 2)) {
				String recText = (recommendation != null) ? "'" + recommendation + "'" : "a smaller model";
				String warningMessage = String.format(
						"[Hardware Note: Detected %dGB VRAM. Running '%s' requires ~%dGB. If you experience low frame rates with shaders active, consider switching to %s.]\n" +
						"[System Note: Loading model into memory. This may take a while if running on CPU/system memory...]\n\n",
						vramGb, selectedModel, modelVramGb, recText
				);
				computer.queueEvent("ai_token", requestId, warningMessage, false);
			}

			// Build Ollama JSON POST payload
			JsonObject payload = new JsonObject();
			payload.addProperty("model", selectedModel);
			payload.addProperty("prompt", finalPrompt);
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
			activeRequests.put(requestId, context);

			future.thenAcceptAsync(response -> {
				if (response.statusCode() != 200) {
					computer.queueEvent("ai_token", requestId, "Error: Ollama returned status " + response.statusCode(), true);
					activeRequests.remove(requestId);
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
					activeRequests.remove(requestId);
				}
			}).exceptionally(ex -> {
				if (!context.isCancelled()) {
					computer.queueEvent("ai_token", requestId, "Connection failed: " + ex.getMessage(), true);
				}
				activeRequests.remove(requestId);
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
