package net.ccc_ai_age;

import net.ccc_ai_age.blockentity.KineticAICoreBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/**
 * Central registry for all {@link net.minecraft.block.entity.BlockEntity} types
 * in CC:C AI Age.
 *
 * <p>Must be called <em>after</em> {@link ModBlocks#register()} because block
 * entity types reference the block instances they are valid for.
 */
public final class ModBlockEntities {

	// -------------------------------------------------------------------------
	// BlockEntityType declarations
	// -------------------------------------------------------------------------

	/**
	 * The block entity type for {@link net.ccc_ai_age.block.KineticAICoreBlock}.
	 *
	 * <p>Registered as {@code ccc-ai-age:kinetic_ai_core}.
	 */
	public static final BlockEntityType<KineticAICoreBlockEntity> KINETIC_AI_CORE =
			FabricBlockEntityTypeBuilder
					.create(KineticAICoreBlockEntity::new, ModBlocks.KINETIC_AI_CORE)
					.build();

	// -------------------------------------------------------------------------
	// Registration
	// -------------------------------------------------------------------------

	/**
	 * Registers every block entity type. Called once from
	 * {@link CCCAIAge#onInitialize()}, after {@link ModBlocks#register()}.
	 */
	public static void register() {
		Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				CCCAIAge.id("kinetic_ai_core"),
				KINETIC_AI_CORE
		);

		CCCAIAge.LOGGER.info("[CC:C AI Age] Block entity types registered.");
	}

	// Utility class — do not instantiate.
	private ModBlockEntities() {}
}
