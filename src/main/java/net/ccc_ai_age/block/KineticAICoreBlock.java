package net.ccc_ai_age.block;

import net.ccc_ai_age.ModBlockEntities;
import net.ccc_ai_age.blockentity.KineticAICoreBlockEntity;

import net.ccc_ai_age.api.AITier;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * The Kinetic AI Core block.
 *
 * <p>Extends {@link BlockWithEntity} so that Minecraft associates a
 * {@link KineticAICoreBlockEntity} with every placed instance of this block.
 * The block entity is what CC: Tweaked attaches its peripheral wrapper to.
 */
public class KineticAICoreBlock extends BlockWithEntity {

	private final AITier tier;

	public KineticAICoreBlock(Settings settings, AITier tier) {
		super(settings);
		this.tier = tier;
	}

	public AITier getTier() {
		return tier;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new KineticAICoreBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> net.minecraft.block.entity.BlockEntityTicker<T> getTicker(net.minecraft.world.World world, BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
		// Only register the ticker on the server side — chunk loading is a server-side operation.
		// Returning null on the client avoids a redundant client tick.
		if (world.isClient()) return null;
		return checkType(type, ModBlockEntities.KINETIC_AI_CORE, (world1, pos, state1, blockEntity) -> {
			if (blockEntity instanceof KineticAICoreBlockEntity) {
				((KineticAICoreBlockEntity) blockEntity).tick(world1, pos, state1);
			}
		});
	}
}
