package net.ccc_ai_age.recipe;

import net.ccc_ai_age.CCCAIAge;
import net.ccc_ai_age.ModBlocks;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Custom recipe allowing players to combine an Advanced Turtle with a Quantum AI Core.
 * Retains all original turtle ID, fuel, and upgrade tags while appending the NeuralAI marker.
 */
public class NeuralTurtleRecipe extends SpecialCraftingRecipe {

	public NeuralTurtleRecipe(Identifier id, CraftingRecipeCategory category) {
		super(id, category);
	}

	@Override
	public boolean matches(RecipeInputInventory inventory, World world) {
		boolean foundTurtle = false;
		boolean foundQuantumCore = false;

		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty()) continue;

			Identifier itemId = Registries.ITEM.getId(stack.getItem());
			if (itemId.getNamespace().equals("computercraft") && itemId.getPath().equals("turtle_advanced")) {
				if (foundTurtle) return false;
				foundTurtle = true;
			} else if (stack.isOf(ModBlocks.QUANTUM_KINETIC_AI_CORE.asItem())) {
				if (foundQuantumCore) return false;
				foundQuantumCore = true;
			} else {
				return false;
			}
		}

		return foundTurtle && foundQuantumCore;
	}

	@Override
	public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
		ItemStack turtleStack = ItemStack.EMPTY;

		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty()) continue;

			Identifier itemId = Registries.ITEM.getId(stack.getItem());
			if (itemId.getNamespace().equals("computercraft") && itemId.getPath().equals("turtle_advanced")) {
				turtleStack = stack;
				break;
			}
		}

		if (turtleStack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack result = turtleStack.copy();
		result.setCount(1);
		result.getOrCreateNbt().putBoolean("NeuralAI", true);
		result.getOrCreateSubNbt("BlockEntityTag").putBoolean("NeuralAI", true);
		return result;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
		return CCCAIAge.NEURAL_TURTLE_RECIPE_SERIALIZER;
	}
}
