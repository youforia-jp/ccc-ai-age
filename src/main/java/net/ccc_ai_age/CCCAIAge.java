package net.ccc_ai_age;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod initializer for CC:C AI Age.
 *
 * Bootstraps all registries in a safe, deterministic order:
 *   1. Blocks (and their BlockItems)
 *   2. BlockEntityTypes
 *
 * The CC: Tweaked peripheral plugin is loaded separately via the "computercraft"
 * entrypoint in fabric.mod.json, which CC:T calls after it has finished its own
 * initialization — so we do NOT call anything CC:T-related here.
 */
public class CCCAIAge implements ModInitializer {

	/** The mod ID must match the "id" field in fabric.mod.json. */
	public static final String MOD_ID = "ccc-ai-age";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[CC:C AI Age] Initializing — Phase 1 & 2 (Kinetic AI Core + CC:T peripheral)");

		// Register blocks (and their BlockItems)
		ModBlocks.register();

		// Register block entity types (must come after blocks are registered)
		ModBlockEntities.register();

		// Register the KineticAICoreBlockEntity's peripheral wrapper as an IPeripheral provider (v0.34)
		dan200.computercraft.api.peripheral.PeripheralLookup.get().registerForBlockEntity(
				(blockEntity, direction) -> blockEntity.getPeripheral(),
				ModBlockEntities.KINETIC_AI_CORE
		);
		
		// Register creative mode tabs
		ModItemGroups.register();

		// Run local Ollama background check and model pre-pull (v0.33)
		OllamaSetupHandler.initializeAsync();

		LOGGER.info("[CC:C AI Age] Initialization complete.");
	}

	/**
	 * Convenience helper to build an {@link Identifier} namespaced to this mod.
	 *
	 * @param path the path component (e.g. "kinetic_ai_core")
	 * @return a namespaced {@code ccc-ai-age:<path>} identifier
	 */
	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
