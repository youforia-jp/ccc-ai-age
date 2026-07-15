package net.ccc_ai_age.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Inject(method = "getName", at = @At("RETURN"), cancellable = true)
	private void onGetName(CallbackInfoReturnable<Text> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (stack.hasNbt() && stack.getNbt().getBoolean("NeuralAI")) {
			cir.setReturnValue(Text.translatable("item.ccc-ai-age.neural_computer.name"));
		}
	}
}
