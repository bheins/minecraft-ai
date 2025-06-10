package com.example.aimodgen.init;

import com.example.aimodgen.AiModGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AiModGenerator.MOD_ID);
    
    // Block Items
    public static final RegistryObject<Item> AI_GENERATED_BLOCK_ITEM = ITEMS.register("ai_generated_block",
            () -> new BlockItem(ModBlocks.AI_GENERATED_BLOCK.get(), new Item.Properties()));
}
