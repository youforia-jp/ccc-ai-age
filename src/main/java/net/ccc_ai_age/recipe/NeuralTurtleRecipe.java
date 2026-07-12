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
 * Custom recipe allowing players to combine any ComputerCraft computer or turtle
 * with any of our three AI Cores (Basic, Advanced, Quantum) to neural-upgrade it.
 */
public class NeuralTurtleRecipe extends SpecialCraftingRecipe {

	public NeuralTurtleRecipe(Identifier id, CraftingRecipeCategory category) {
		super(id, category);
	}

	@Override
	public boolean matches(RecipeInputInventory inventory, World world) {
		boolean foundBase = false;
		boolean foundCore = false;

		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty()) continue;

			Identifier itemId = Registries.ITEM.getId(stack.getItem());
			if (itemId.getNamespace().equals("computercraft") && 
				(itemId.getPath().equals("turtle_advanced") || 
				 itemId.getPath().equals("turtle_normal") || 
				 itemId.getPath().equals("computer_advanced") || 
				 itemId.getPath().equals("computer_normal"))) {
				if (foundBase) return false;
				foundBase = true;
			} else if (stack.isOf(ModBlocks.BASIC_KINETIC_AI_CORE.asItem()) || 
					   stack.isOf(ModBlocks.ADVANCED_KINETIC_AI_CORE.asItem()) || 
					   stack.isOf(ModBlocks.QUANTUM_KINETIC_AI_CORE.asItem())) {
				if (foundCore) return false;
				foundCore = true;
			} else {
				return false;
			}
		}

		return foundBase && foundCore;
	}

	@Override
	public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
		ItemStack baseStack = ItemStack.EMPTY;
		ItemStack coreStack = ItemStack.EMPTY;

		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty()) continue;

			Identifier itemId = Registries.ITEM.getId(stack.getItem());
			if (itemId.getNamespace().equals("computercraft") && 
				(itemId.getPath().equals("turtle_advanced") || 
				 itemId.getPath().equals("turtle_normal") || 
				 itemId.getPath().equals("computer_advanced") || 
				 itemId.getPath().equals("computer_normal"))) {
				baseStack = stack;
			} else if (stack.isOf(ModBlocks.BASIC_KINETIC_AI_CORE.asItem()) || 
					   stack.isOf(ModBlocks.ADVANCED_KINETIC_AI_CORE.asItem()) || 
					   stack.isOf(ModBlocks.QUANTUM_KINETIC_AI_CORE.asItem())) {
				coreStack = stack;
			}
		}

		if (baseStack.isEmpty() || coreStack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		String tierName = "basic";
		if (coreStack.isOf(ModBlocks.ADVANCED_KINETIC_AI_CORE.asItem())) {
			tierName = "advanced";
		} else if (coreStack.isOf(ModBlocks.QUANTUM_KINETIC_AI_CORE.asItem())) {
			tierName = "quantum";
		}

		ItemStack result = baseStack.copy();
		result.setCount(1);
		result.getOrCreateNbt().putBoolean("NeuralAI", true);
		result.getOrCreateNbt().putString("NeuralTier", tierName);
		result.getOrCreateSubNbt("BlockEntityTag").putBoolean("NeuralAI", true);
		result.getOrCreateSubNbt("BlockEntityTag").putString("NeuralTier", tierName);
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
