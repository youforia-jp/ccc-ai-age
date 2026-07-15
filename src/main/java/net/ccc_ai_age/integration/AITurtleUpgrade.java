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

	private final boolean hasModem;

	public AITurtleUpgrade(Identifier id, AITier tier, ItemStack craftItem, boolean hasModem) {
		this.id = id;
		this.tier = tier;
		this.craftItem = craftItem;
		this.hasModem = hasModem;
	}

	public AITier getTier() {
		return tier;
	}

	public boolean hasModem() {
		return hasModem;
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

	public static final net.minecraft.server.world.ChunkTicketType<net.minecraft.util.math.ChunkPos> KINETIC_AI_TICKET =
			net.minecraft.server.world.ChunkTicketType.create("kinetic_ai", java.util.Comparator.comparingLong(net.minecraft.util.math.ChunkPos::toLong), 100);

	@Override
	public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
		net.minecraft.world.World world = turtle.getLevel();
		if (world != null && !world.isClient() && world instanceof net.minecraft.server.world.ServerWorld) {
			net.minecraft.server.world.ServerWorld serverWorld = (net.minecraft.server.world.ServerWorld) world;
			net.minecraft.util.math.BlockPos pos = turtle.getPosition();
			net.minecraft.util.math.ChunkPos chunkPos = new net.minecraft.util.math.ChunkPos(pos);
			serverWorld.getChunkManager().addTicket(KINETIC_AI_TICKET, chunkPos, 31, chunkPos);
		}
	}
}
