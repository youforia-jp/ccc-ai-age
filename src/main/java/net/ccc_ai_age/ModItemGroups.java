package net.ccc_ai_age;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class ModItemGroups {

    public static final ItemGroup MAIN = Registry.register(
            Registries.ITEM_GROUP,
            CCCAIAge.id("main"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.ccc-ai-age.main"))
                    .icon(() -> new ItemStack(ModBlocks.ADVANCED_KINETIC_AI_CORE))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.BASIC_KINETIC_AI_CORE);
                        entries.add(ModBlocks.ADVANCED_KINETIC_AI_CORE);
                        entries.add(ModBlocks.QUANTUM_KINETIC_AI_CORE);
                    })
                    .build()
    );

    public static void register() {
        CCCAIAge.LOGGER.info("[CC:C AI Age] ItemGroups registered.");
    }
}
