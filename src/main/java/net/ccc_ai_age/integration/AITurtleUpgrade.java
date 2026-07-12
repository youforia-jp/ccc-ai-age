package net.ccc_ai_age.integration;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.ccc_ai_age.api.AITier;
import net.ccc_ai_age.blockentity.KineticAICoreBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom ITurtleUpgrade implementation allowing turtles to use the Kinetic AI Core
 * as a mobile peripheral.
 */
public class AITurtleUpgrade implements ITurtleUpgrade {

	private final Identifier id;
	private final AITier tier;
	private final ItemStack craftItem;

	public AITurtleUpgrade(Identifier id, AITier tier, ItemStack craftItem) {
		this.id = id;
		this.tier = tier;
		this.craftItem = craftItem;
	}

	@Override
	public @NotNull ItemStack getCraftingItem() {
		return this.craftItem.copy();
	}

	@Override
	public @NotNull String getUnlocalisedAdjective() {
		return "turtle_upgrade." + id.getNamespace() + "." + id.getPath() + ".adjective";
	}

	@Override
	public @NotNull Identifier getUpgradeID() {
		return this.id;
	}

	@Override
	public @NotNull ItemStack getUpgradeItem(@Nullable NbtCompound nbt) {
		return this.craftItem.copy();
	}

	@Override
	public @NotNull TurtleUpgradeType getType() {
		return TurtleUpgradeType.PERIPHERAL;
	}

	@Override
	public @Nullable IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
		return new KineticAICoreBlockEntity.KineticAICorePeripheral(this.tier);
	}
}
