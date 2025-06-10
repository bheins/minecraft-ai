package com.example.aimodgen.init;

import com.example.aimodgen.AiModGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final CreativeModeTab AI_MOD_TAB = new CreativeModeTab(AiModGenerator.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.AI_GENERATED_BLOCK_ITEM.get());
        }

        @Override
        public Component getDisplayName() {
            return Component.translatable("itemGroup.aimodgenerator");
        }
    };
}
