package net.ccc_ai_age.block;

import net.ccc_ai_age.ModBlockEntities;
import net.ccc_ai_age.blockentity.KineticAICoreBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * The Kinetic AI Core block.
 *
 * <p>Extends {@link Block} and implements {@link BlockEntityProvider} so that
 * Minecraft associates a {@link KineticAICoreBlockEntity} with every placed
 * instance of this block. The block entity is what CC: Tweaked attaches its
 * peripheral wrapper to.
 *
 * <p>In a later phase this class will also override
 * {@code appendProperties()} to add a {@code FACING} state property
 * (for directional placement), and override {@code getPlacementState()} to
 * set the initial facing direction from the player's look vector.
 */
public class KineticAICoreBlock extends Block implements BlockEntityProvider {

	public KineticAICoreBlock(Settings settings) {
		super(settings);
	}

	// -------------------------------------------------------------------------
	// BlockEntityProvider
	// -------------------------------------------------------------------------

	/**
	 * Creates a new {@link KineticAICoreBlockEntity} for each placed block.
	 *
	 * @param pos   position of the block in the world
	 * @param state the current block state
	 * @return a fresh block entity instance, never {@code null}
	 */
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new KineticAICoreBlockEntity(pos, state);
	}
}
