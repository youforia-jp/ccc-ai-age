package net.ccc_ai_age;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Background startup helper to check if Ollama is running, start it if missing,
 * and pre-pull the required basic model.
 */
public class OllamaSetupHandler {

	private static final HttpClient CLIENT = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(3))
			.build();

	/**
	 * Runs the startup check asynchronously on a background thread.
	 */
	public static void initializeAsync() {
		CompletableFuture.runAsync(() -> {
			try {
				CCCAIAge.LOGGER.info("[CC:C AI Age] Checking local Ollama instance on http://localhost:11434/...");
				boolean online = isOllamaOnline();
				if (!online) {
					CCCAIAge.LOGGER.info("[CC:C AI Age] Ollama not detected. Attempting to start local instance via 'ollama serve'...");
					try {
						new ProcessBuilder("ollama", "serve").start();
						// Give it a brief moment to spin up
						Thread.sleep(3000);
					} catch (Exception e) {
						CCCAIAge.LOGGER.warn("[CC:C AI Age] Failed to start local Ollama process. Make sure Ollama is installed and on your system PATH: {}", e.getMessage());
						return;
					}

					// Re-check status
					online = isOllamaOnline();
					if (!online) {
						CCCAIAge.LOGGER.warn("[CC:C AI Age] Ollama serve was launched but is not responding on http://localhost:11434/");
						return;
					}
				}

				CCCAIAge.LOGGER.info("[CC:C AI Age] Ollama is online. Ensuring all 5 model tiers are pulled...");
				String[] targetModels = {"qwen:0.5b", "qwen3.5:4b", "qwen3:8b", "qwen3.5:9b", "qwen2.5:14b"};
				for (String modelName : targetModels) {
					CompletableFuture.runAsync(() -> {
						pullModel(modelName);
					});
				}
				CCCAIAge.LOGGER.info("[CC:C AI Age] Ollama model check tasks scheduled.");
			} catch (Exception e) {
				CCCAIAge.LOGGER.warn("[CC:C AI Age] Error during background Ollama setup: {}", e.getMessage());
			}
		});
	}

	private static boolean isOllamaOnline() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("http://localhost:11434/"))
					.GET()
					.build();
			HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() == 200;
		} catch (Exception e) {
			return false;
		}
	}

	private static void pullModel(String modelName) {
		try {
			String jsonPayload = "{\"model\": \"" + modelName + "\"}";
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("http://localhost:11434/api/pull"))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
					.build();
			HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				CCCAIAge.LOGGER.info("[CC:C AI Age] Successfully ensured model '{}' is pulled.", modelName);
			} else {
				CCCAIAge.LOGGER.warn("[CC:C AI Age] Ollama returned status {} when pulling model: {}", response.statusCode(), response.body());
			}
		} catch (Exception e) {
			CCCAIAge.LOGGER.warn("[CC:C AI Age] Failed to pull Ollama model '{}': {}", modelName, e.getMessage());
		}
	}
}
