package net.ccc_ai_age;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup MAIN = Registry.register(
            Registries.ITEM_GROUP,
            CCCAIAge.id("main"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.ccc-ai-age.main"))
                    .icon(() -> new ItemStack(ModBlocks.ADVANCED_KINETIC_AI_CORE))
                    .entries((displayContext, entries) -> {
                        // AI Core blocks
                        entries.add(ModBlocks.BASIC_KINETIC_AI_CORE);
                        entries.add(ModBlocks.ADVANCED_KINETIC_AI_CORE);
                        entries.add(ModBlocks.QUANTUM_KINETIC_AI_CORE);
                        
                        // Combo Items
                        entries.add(ModBlocks.ADVANCED_KINETIC_AI_CORE_MODEM);
                        entries.add(ModBlocks.QUANTUM_KINETIC_AI_CORE_MODEM);

                        // Neural Turtles — constructed using standard CC:T NBT
                        net.minecraft.item.Item turtleAdvanced = Registries.ITEM.get(new Identifier("computercraft", "turtle_advanced"));
                        net.minecraft.item.Item turtleNormal = Registries.ITEM.get(new Identifier("computercraft", "turtle_normal"));
                        String[] tiers = {"basic", "advanced", "quantum"};
                        for (String tier : tiers) {
                            for (net.minecraft.item.Item base : new net.minecraft.item.Item[]{turtleAdvanced, turtleNormal}) {
                                if (base != null && base != net.minecraft.item.Items.AIR) {
                                    ItemStack neural = new ItemStack(base);
                                    NbtCompound nbt = neural.getOrCreateNbt();
                                    nbt.putString("RightUpgrade", "ccc-ai-age:" + tier + "_kinetic_ai_core_upgrade");
                                    entries.add(neural);
                                }
                            }
                        }
                    })
                    .build()
    );

    public static void register() {
        CCCAIAge.LOGGER.info("[CC:C AI Age] ItemGroups registered.");
    }
}

