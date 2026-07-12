package net.ccc_ai_age.mixin;

import net.ccc_ai_age.integration.NeuralComputerAccess;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

	@Inject(method = "postPlacement", at = @At("RETURN"))
	private void onPostPlacement(BlockPos pos, World world, PlayerEntity player, ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (!world.isClient() && stack.hasNbt() && stack.getNbt().getBoolean("NeuralAI")) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof NeuralComputerAccess) {
				((NeuralComputerAccess) be).setNeuralAI(true);
				if (stack.getNbt().contains("NeuralTier")) {
					((NeuralComputerAccess) be).setNeuralTier(stack.getNbt().getString("NeuralTier"));
				}
				be.markDirty();
			}
		}
	}
}
