package net.ccc_ai_age;

import net.ccc_ai_age.api.AITier;
import net.ccc_ai_age.block.KineticAICoreBlock;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

/**
 * Central registry for all blocks and their corresponding BlockItems in CC:C AI Age.
 *
 * <p>Each block constant should follow the pattern:
 * <ol>
 *   <li>Create a static final {@link Block} field.</li>
 *   <li>Register it and its {@link BlockItem} inside {@link #register()}.</li>
 *   <li>Add the item to the appropriate creative-mode tab.</li>
 * </ol>
 */
public final class ModBlocks {

	// -------------------------------------------------------------------------
	// Block declarations
	// -------------------------------------------------------------------------

	/**
	 * The Kinetic AI Core — a brass-tier machine block that serves as the
	 * bridge between Create's kinetic network and CC: Tweaked computers.
	 *
	 * <p>Properties mirror Create's BrassCasingBlock:
	 * <ul>
	 *   <li>Requires a pickaxe to mine (hardness 3.0, blast resistance 6.0)</li>
	 *   <li>Metal sound group</li>
	 *   <li>Gold/brass map colour</li>
	 * </ul>
	 */
	public static final Block BASIC_KINETIC_AI_CORE = new KineticAICoreBlock(
			AbstractBlock.Settings.create()
					.mapColor(MapColor.GOLD)
					.requiresTool()                   // pickaxe tag enforced via data tag JSON
					.strength(3.0f, 6.0f)
					.sounds(BlockSoundGroup.METAL)
					.pistonBehavior(PistonBehavior.BLOCK),
			AITier.BASIC
	);

	public static final Block ADVANCED_KINETIC_AI_CORE = new KineticAICoreBlock(
			AbstractBlock.Settings.create()
					.mapColor(MapColor.DIAMOND_BLUE)
					.requiresTool()
					.strength(3.0f, 6.0f)
					.sounds(BlockSoundGroup.METAL)
					.pistonBehavior(PistonBehavior.BLOCK),
			AITier.ADVANCED
	);

	public static final Block QUANTUM_KINETIC_AI_CORE = new KineticAICoreBlock(
			AbstractBlock.Settings.create()
					.mapColor(MapColor.PURPLE)
					.requiresTool()
					.strength(3.0f, 6.0f)
					.sounds(BlockSoundGroup.METAL)
					.pistonBehavior(PistonBehavior.BLOCK),
			AITier.QUANTUM
	);

	// Modems (Items only)
	public static final Item ADVANCED_KINETIC_AI_CORE_MODEM = Registry.register(Registries.ITEM, 
			new net.minecraft.util.Identifier(CCCAIAge.MOD_ID, "advanced_kinetic_ai_core_modem"), 
			new Item(new FabricItemSettings()));
	public static final Item QUANTUM_KINETIC_AI_CORE_MODEM = Registry.register(Registries.ITEM, 
			new net.minecraft.util.Identifier(CCCAIAge.MOD_ID, "quantum_kinetic_ai_core_modem"), 
			new Item(new FabricItemSettings()));

	// -------------------------------------------------------------------------
	// Registration
	// -------------------------------------------------------------------------

	/**
	 * Registers every block and its BlockItem. Called once from
	 * {@link CCCAIAge#onInitialize()}.
	 */
	public static void register() {
		// --- Basic Kinetic AI Core ---
		Registry.register(
				Registries.BLOCK,
				CCCAIAge.id("basic_kinetic_ai_core"),
				BASIC_KINETIC_AI_CORE
		);
		BlockItem basicKineticAICoreItem = new BlockItem(BASIC_KINETIC_AI_CORE, new FabricItemSettings());
		Registry.register(
				Registries.ITEM,
				CCCAIAge.id("basic_kinetic_ai_core"),
				basicKineticAICoreItem
		);

		// --- Advanced Kinetic AI Core ---
		Registry.register(
				Registries.BLOCK,
				CCCAIAge.id("advanced_kinetic_ai_core"),
				ADVANCED_KINETIC_AI_CORE
		);
		BlockItem advancedKineticAICoreItem = new BlockItem(ADVANCED_KINETIC_AI_CORE, new FabricItemSettings());
		Registry.register(
				Registries.ITEM,
				CCCAIAge.id("advanced_kinetic_ai_core"),
				advancedKineticAICoreItem
		);

		// --- Quantum Kinetic AI Core ---
		Registry.register(
				Registries.BLOCK,
				CCCAIAge.id("quantum_kinetic_ai_core"),
				QUANTUM_KINETIC_AI_CORE
		);
		BlockItem quantumKineticAICoreItem = new BlockItem(QUANTUM_KINETIC_AI_CORE, new FabricItemSettings());
		Registry.register(
				Registries.ITEM,
				CCCAIAge.id("quantum_kinetic_ai_core"),
				quantumKineticAICoreItem
		);



		CCCAIAge.LOGGER.info("[CC:C AI Age] Blocks registered.");
	}

	// Utility class — do not instantiate.
	private ModBlocks() {}
}
