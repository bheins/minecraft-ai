package com.example.aimodgen.item;

import com.example.aimodgen.AiModGenerator;
import com.example.aimodgen.generation.GeneratedContent;
import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class DynamicItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AiModGenerator.MOD_ID);
    private static final Map<String, RegistryObject<Item>> registeredItems = new HashMap<>();

    public static RegistryObject<Item> registerItem(GeneratedContent content) {
        if (registeredItems.containsKey(content.getId())) {
            return registeredItems.get(content.getId());
        }

        RegistryObject<Item> itemReg = ITEMS.register(content.getId(), () -> new AIGeneratedItem(new Item.Properties(), content));
        registeredItems.put(content.getId(), itemReg);
        return itemReg;
    }

    public static Item createAndRegisterItem(String itemId, JsonObject data) {
        // Since we can't register items at runtime after initialization,
        // we'll return null and log an error for now
        // This will be handled by creating a fallback item system
        
        System.err.println("WARNING: Cannot register items at runtime. Item '" + itemId + "' will not be created.");
        return null;
    }

    public static Map<String, RegistryObject<Item>> getRegisteredItems() {
        return registeredItems;
    }
}
