package net.ccc_ai_age.mixin;

import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import dan200.computercraft.shared.turtle.blocks.TurtleBlock;
import net.ccc_ai_age.integration.NeuralComputerAccess;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TurtleBlock.class)
public class TurtleBlockMixin {

	@Inject(method = "getItem", at = @At("RETURN"), remap = false, cancellable = true)
	private void onGetItem(AbstractComputerBlockEntity entity, CallbackInfoReturnable<ItemStack> cir) {
		ItemStack stack = cir.getReturnValue();
		if (entity instanceof NeuralComputerAccess && ((NeuralComputerAccess) entity).isNeuralAI() && !stack.isEmpty()) {
			stack.getOrCreateNbt().putBoolean("NeuralAI", true);
			stack.getOrCreateNbt().putString("NeuralTier", ((NeuralComputerAccess) entity).getNeuralTier());
			stack.getOrCreateSubNbt("BlockEntityTag").putBoolean("NeuralAI", true);
			stack.getOrCreateSubNbt("BlockEntityTag").putString("NeuralTier", ((NeuralComputerAccess) entity).getNeuralTier());
			cir.setReturnValue(stack);
		}
	}
}
