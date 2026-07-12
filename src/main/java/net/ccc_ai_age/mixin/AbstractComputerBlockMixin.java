package net.ccc_ai_age.mixin;

import dan200.computercraft.shared.computer.blocks.AbstractComputerBlock;
import net.ccc_ai_age.integration.NeuralComputerAccess;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into AbstractComputerBlock.onPlaced to immediately copy the NeuralAI
 * and NeuralTier NBT flags from the placed item stack onto the new block entity.
 *
 * CC:T uses its own onPlaced routine that only reads known computer keys (ComputerId,
 * Label, Fuel, Upgrades, etc.) and does NOT propagate custom root-level NBT through
 * loadServer. Without this hook, the NeuralAI flag is never set on first placement.
 */
@Mixin(AbstractComputerBlock.class)
public class AbstractComputerBlockMixin {

	@Inject(method = "onPlaced", at = @At("TAIL"))
	private void onPlaced(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
		if (blockEntity instanceof NeuralComputerAccess && stack.hasNbt()) {
			NbtCompound nbt = stack.getNbt();

			// Priority 1: Check root-level NBT (set by NeuralTurtleRecipe.craft)
			if (nbt.getBoolean("NeuralAI")) {
				((NeuralComputerAccess) blockEntity).setNeuralAI(true);
				if (nbt.contains("NeuralTier")) {
					((NeuralComputerAccess) blockEntity).setNeuralTier(nbt.getString("NeuralTier"));
				}
				blockEntity.markDirty();
				return;
			}

			// Priority 2: Check BlockEntityTag sub-NBT (also set by NeuralTurtleRecipe.craft)
			if (nbt.contains("BlockEntityTag")) {
				NbtCompound bet = nbt.getCompound("BlockEntityTag");
				if (bet.getBoolean("NeuralAI")) {
					((NeuralComputerAccess) blockEntity).setNeuralAI(true);
					if (bet.contains("NeuralTier")) {
						((NeuralComputerAccess) blockEntity).setNeuralTier(bet.getString("NeuralTier"));
					}
					blockEntity.markDirty();
				}
			}
		}
	}
}
