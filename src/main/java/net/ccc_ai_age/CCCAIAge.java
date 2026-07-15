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

	public static final dan200.computercraft.api.turtle.TurtleUpgradeSerialiser<net.ccc_ai_age.integration.AITurtleUpgrade> BASIC_UPGRADE_SERIALISER =
			dan200.computercraft.api.turtle.TurtleUpgradeSerialiser.simpleWithCustomItem(
					(id, stack) -> new net.ccc_ai_age.integration.AITurtleUpgrade(id, net.ccc_ai_age.api.AITier.BASIC, stack, false)
			);
	public static final dan200.computercraft.api.turtle.TurtleUpgradeSerialiser<net.ccc_ai_age.integration.AITurtleUpgrade> ADVANCED_UPGRADE_SERIALISER =
			dan200.computercraft.api.turtle.TurtleUpgradeSerialiser.simpleWithCustomItem(
					(id, stack) -> new net.ccc_ai_age.integration.AITurtleUpgrade(id, net.ccc_ai_age.api.AITier.ADVANCED, stack, false)
			);
	public static final dan200.computercraft.api.turtle.TurtleUpgradeSerialiser<net.ccc_ai_age.integration.AITurtleUpgrade> QUANTUM_UPGRADE_SERIALISER =
			dan200.computercraft.api.turtle.TurtleUpgradeSerialiser.simpleWithCustomItem(
					(id, stack) -> new net.ccc_ai_age.integration.AITurtleUpgrade(id, net.ccc_ai_age.api.AITier.QUANTUM, stack, false)
			);
	public static final dan200.computercraft.api.turtle.TurtleUpgradeSerialiser<net.ccc_ai_age.integration.AITurtleUpgrade> ADVANCED_MODEM_UPGRADE_SERIALISER =
			dan200.computercraft.api.turtle.TurtleUpgradeSerialiser.simpleWithCustomItem(
					(id, stack) -> new net.ccc_ai_age.integration.AITurtleUpgrade(id, net.ccc_ai_age.api.AITier.ADVANCED, stack, true)
			);
	public static final dan200.computercraft.api.turtle.TurtleUpgradeSerialiser<net.ccc_ai_age.integration.AITurtleUpgrade> QUANTUM_MODEM_UPGRADE_SERIALISER =
			dan200.computercraft.api.turtle.TurtleUpgradeSerialiser.simpleWithCustomItem(
					(id, stack) -> new net.ccc_ai_age.integration.AITurtleUpgrade(id, net.ccc_ai_age.api.AITier.QUANTUM, stack, true)
			);

	public static final net.minecraft.recipe.RecipeSerializer<net.ccc_ai_age.recipe.NeuralTurtleRecipe> NEURAL_TURTLE_RECIPE_SERIALIZER =
			new net.minecraft.recipe.SpecialRecipeSerializer<>(net.ccc_ai_age.recipe.NeuralTurtleRecipe::new);

	@Override
	public void onInitialize() {
		LOGGER.info("[CC:C AI Age] Initializing — Phase 1 & 2 (Kinetic AI Core + CC:T peripheral)");

		// Load configuration FIRST — OllamaSetupHandler and other systems read it (v0.38)
		ModConfig.load();

		// Register blocks (and their BlockItems)
		ModBlocks.register();

		// Register block entity types (must come after blocks are registered)
		ModBlockEntities.register();

		// Register custom recipes (v0.41)
		net.minecraft.registry.Registry.register(
				net.minecraft.registry.Registries.RECIPE_SERIALIZER,
				CCCAIAge.id("neural_turtle_crafting"),
				NEURAL_TURTLE_RECIPE_SERIALIZER
		);

		// Register turtle upgrades (v0.39)
		net.minecraft.registry.Registry<dan200.computercraft.api.turtle.TurtleUpgradeSerialiser<?>> registry =
				(net.minecraft.registry.Registry) net.minecraft.registry.Registries.REGISTRIES.get(
						dan200.computercraft.api.turtle.TurtleUpgradeSerialiser.registryId().getValue()
				);
		if (registry != null) {
			net.minecraft.registry.Registry.register(
					registry,
					CCCAIAge.id("basic_kinetic_ai_core_upgrade"),
					BASIC_UPGRADE_SERIALISER
			);
			net.minecraft.registry.Registry.register(
					registry,
					CCCAIAge.id("advanced_kinetic_ai_core_upgrade"),
					ADVANCED_UPGRADE_SERIALISER
			);
			net.minecraft.registry.Registry.register(
					registry,
					CCCAIAge.id("quantum_kinetic_ai_core_upgrade"),
					QUANTUM_UPGRADE_SERIALISER
			);
			net.minecraft.registry.Registry.register(
					registry,
					CCCAIAge.id("advanced_kinetic_ai_core_modem_upgrade"),
					ADVANCED_MODEM_UPGRADE_SERIALISER
			);
			net.minecraft.registry.Registry.register(
					registry,
					CCCAIAge.id("quantum_kinetic_ai_core_modem_upgrade"),
					QUANTUM_MODEM_UPGRADE_SERIALISER
			);
		} else {
			// CC:T was not loaded or its registry is unavailable — turtle upgrades will not be registered
			LOGGER.warn("[CC:C AI Age] CC:T TurtleUpgradeSerialiser registry not found — turtle upgrades will not be registered. Is CC: Tweaked installed?");
		}

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
