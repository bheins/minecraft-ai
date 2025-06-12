package com.example.aimodgen.util;

import com.example.aimodgen.generation.GeneratedContent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the visual appearance of AI-generated items by selecting appropriate base items
 * based on AI-generated content descriptions and properties.
 */
public class ItemAppearanceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Weapon mappings
    private static final Map<String, Item> WEAPON_KEYWORDS = new HashMap<>();
    // Tool mappings  
    private static final Map<String, Item> TOOL_KEYWORDS = new HashMap<>();
    // Magic/Special mappings
    private static final Map<String, Item> MAGIC_KEYWORDS = new HashMap<>();
    // Material/Type mappings
    private static final Map<String, Item> MATERIAL_KEYWORDS = new HashMap<>();
    
    static {
        initializeWeaponMappings();
        initializeToolMappings();
        initializeMagicMappings();
        initializeMaterialMappings();
    }
    
    private static void initializeWeaponMappings() {
        // Swords
        WEAPON_KEYWORDS.put("sword", Items.IRON_SWORD);
        WEAPON_KEYWORDS.put("blade", Items.IRON_SWORD);
        WEAPON_KEYWORDS.put("katana", Items.IRON_SWORD);
        WEAPON_KEYWORDS.put("saber", Items.IRON_SWORD);
        WEAPON_KEYWORDS.put("rapier", Items.IRON_SWORD);
        
        // Axes
        WEAPON_KEYWORDS.put("axe", Items.IRON_AXE);
        WEAPON_KEYWORDS.put("hatchet", Items.IRON_AXE);
        WEAPON_KEYWORDS.put("battleaxe", Items.IRON_AXE);
        
        // Ranged
        WEAPON_KEYWORDS.put("bow", Items.BOW);
        WEAPON_KEYWORDS.put("crossbow", Items.CROSSBOW);
        WEAPON_KEYWORDS.put("arrow", Items.ARROW);
        
        // Misc weapons
        WEAPON_KEYWORDS.put("trident", Items.TRIDENT);
        WEAPON_KEYWORDS.put("spear", Items.TRIDENT);
    }
    
    private static void initializeToolMappings() {
        // Pickaxes
        TOOL_KEYWORDS.put("pickaxe", Items.IRON_PICKAXE);
        TOOL_KEYWORDS.put("pick", Items.IRON_PICKAXE);
        
        // Shovels
        TOOL_KEYWORDS.put("shovel", Items.IRON_SHOVEL);
        TOOL_KEYWORDS.put("spade", Items.IRON_SHOVEL);
        
        // Hoes
        TOOL_KEYWORDS.put("hoe", Items.IRON_HOE);
        
        // Shears
        TOOL_KEYWORDS.put("shears", Items.SHEARS);
        TOOL_KEYWORDS.put("scissors", Items.SHEARS);
        
        // Fishing
        TOOL_KEYWORDS.put("fishing", Items.FISHING_ROD);
        TOOL_KEYWORDS.put("rod", Items.FISHING_ROD);
    }
    
    private static void initializeMagicMappings() {
        // Wands and Staves
        MAGIC_KEYWORDS.put("wand", Items.BLAZE_ROD);
        MAGIC_KEYWORDS.put("staff", Items.STICK);
        MAGIC_KEYWORDS.put("rod", Items.BLAZE_ROD);
        MAGIC_KEYWORDS.put("scepter", Items.BLAZE_ROD);
        
        // Teleport specific
        MAGIC_KEYWORDS.put("teleport", Items.ENDER_PEARL);
        MAGIC_KEYWORDS.put("portal", Items.ENDER_PEARL);
        
        // Healing specific
        MAGIC_KEYWORDS.put("healing", Items.GOLDEN_APPLE);
        MAGIC_KEYWORDS.put("health", Items.GOLDEN_APPLE);
        MAGIC_KEYWORDS.put("cure", Items.GOLDEN_APPLE);
        MAGIC_KEYWORDS.put("potion", Items.POTION);
        
        // Fire specific
        MAGIC_KEYWORDS.put("fire", Items.FIRE_CHARGE);
        MAGIC_KEYWORDS.put("flame", Items.FIRE_CHARGE);
        MAGIC_KEYWORDS.put("burn", Items.FIRE_CHARGE);
        MAGIC_KEYWORDS.put("blaze", Items.BLAZE_ROD);
        
        // Ice specific
        MAGIC_KEYWORDS.put("ice", Items.BLUE_ICE);
        MAGIC_KEYWORDS.put("frost", Items.BLUE_ICE);
        MAGIC_KEYWORDS.put("freeze", Items.BLUE_ICE);
        MAGIC_KEYWORDS.put("snow", Items.SNOWBALL);
        
        // Crystal/Gem
        MAGIC_KEYWORDS.put("crystal", Items.QUARTZ);
        MAGIC_KEYWORDS.put("gem", Items.EMERALD);
        MAGIC_KEYWORDS.put("jewel", Items.DIAMOND);
        
        // Orb/Sphere
        MAGIC_KEYWORDS.put("orb", Items.ENDER_EYE);
        MAGIC_KEYWORDS.put("sphere", Items.ENDER_EYE);
        MAGIC_KEYWORDS.put("eye", Items.ENDER_EYE);
    }
    
    private static void initializeMaterialMappings() {
        // Metals
        MATERIAL_KEYWORDS.put("iron", Items.IRON_INGOT);
        MATERIAL_KEYWORDS.put("gold", Items.GOLD_INGOT);
        MATERIAL_KEYWORDS.put("golden", Items.GOLD_INGOT);
        MATERIAL_KEYWORDS.put("diamond", Items.DIAMOND);
        MATERIAL_KEYWORDS.put("emerald", Items.EMERALD);
        MATERIAL_KEYWORDS.put("netherite", Items.NETHERITE_INGOT);
        
        // Organic
        MATERIAL_KEYWORDS.put("wood", Items.OAK_LOG);
        MATERIAL_KEYWORDS.put("wooden", Items.STICK);
        MATERIAL_KEYWORDS.put("bone", Items.BONE);
        MATERIAL_KEYWORDS.put("leather", Items.LEATHER);
        
        // Magical materials
        MATERIAL_KEYWORDS.put("ender", Items.ENDER_PEARL);
        MATERIAL_KEYWORDS.put("nether", Items.NETHER_STAR);
        MATERIAL_KEYWORDS.put("obsidian", Items.OBSIDIAN);
        MATERIAL_KEYWORDS.put("quartz", Items.QUARTZ);
    }
    
    /**
     * Determines the best base item to use for an AI-generated item based on its content
     */
    public static Item selectBaseItem(GeneratedContent content) {
        String description = content.getDescription().toLowerCase();
        String itemId = content.getId().toLowerCase();
        
        LOGGER.debug("Selecting base item for '{}' with description '{}'", itemId, description);
        
        // Check for ability-specific items first (highest priority)
        Item abilityItem = selectByAbilities(content, description, itemId);
        if (abilityItem != null) {
            LOGGER.info("Selected {} based on abilities for '{}'", abilityItem, itemId);
            return abilityItem;
        }
        
        // Check for weapon keywords
        Item weaponItem = selectFromKeywords(description + " " + itemId, WEAPON_KEYWORDS);
        if (weaponItem != null) {
            LOGGER.info("Selected {} as weapon for '{}'", weaponItem, itemId);
            return weaponItem;
        }
        
        // Check for tool keywords
        Item toolItem = selectFromKeywords(description + " " + itemId, TOOL_KEYWORDS);
        if (toolItem != null) {
            LOGGER.info("Selected {} as tool for '{}'", toolItem, itemId);
            return toolItem;
        }
        
        // Check for magic keywords
        Item magicItem = selectFromKeywords(description + " " + itemId, MAGIC_KEYWORDS);
        if (magicItem != null) {
            LOGGER.info("Selected {} as magic item for '{}'", magicItem, itemId);
            return magicItem;
        }
        
        // Check for material keywords
        Item materialItem = selectFromKeywords(description + " " + itemId, MATERIAL_KEYWORDS);
        if (materialItem != null) {
            LOGGER.info("Selected {} based on material for '{}'", materialItem, itemId);
            return materialItem;
        }
        
        // Default fallback
        LOGGER.info("Using default stick for '{}'", itemId);
        return Items.STICK;
    }
    
    /**
     * Select item based on detected abilities from AI properties
     */
    private static Item selectByAbilities(GeneratedContent content, String description, String itemId) {
        // Check AI properties first
        if (content.getProperties().has("customProperties")) {
            var customProps = content.getProperties().getAsJsonObject("customProperties");
            
            if (customProps.has("teleportRange")) {
                return Items.ENDER_PEARL;
            }
            if (customProps.has("healAmount")) {
                return Items.GOLDEN_APPLE;
            }
            if (customProps.has("fireRadius")) {
                return Items.FIRE_CHARGE;
            }
            if (customProps.has("freezeRadius")) {
                return Items.BLUE_ICE;
            }
        }
        
        // Check for ability keywords in description/ID
        if (containsKeyword(description + " " + itemId, "teleport", "portal", "warp")) {
            return Items.ENDER_PEARL;
        }
        if (containsKeyword(description + " " + itemId, "heal", "health", "cure", "restore")) {
            return Items.GOLDEN_APPLE;
        }
        if (containsKeyword(description + " " + itemId, "fire", "flame", "burn", "ignite")) {
            return Items.FIRE_CHARGE;
        }
        if (containsKeyword(description + " " + itemId, "ice", "freeze", "frost", "snow")) {
            return Items.BLUE_ICE;
        }
        
        return null;
    }
    
    /**
     * Select item from a keyword map
     */
    private static Item selectFromKeywords(String text, Map<String, Item> keywordMap) {
        for (Map.Entry<String, Item> entry : keywordMap.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    /**
     * Check if text contains any of the given keywords
     */
    private static boolean containsKeyword(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get a human-readable description of why an item was selected
     */
    public static String getSelectionReason(GeneratedContent content) {
        String description = content.getDescription().toLowerCase();
        String itemId = content.getId().toLowerCase();
        
        // Check abilities first
        if (content.getProperties().has("customProperties")) {
            var customProps = content.getProperties().getAsJsonObject("customProperties");
            if (customProps.has("teleportRange")) return "Teleportation ability detected";
            if (customProps.has("healAmount")) return "Healing ability detected";
            if (customProps.has("fireRadius")) return "Fire ability detected";
            if (customProps.has("freezeRadius")) return "Ice ability detected";
        }
        
        // Check keyword categories
        if (selectFromKeywords(description + " " + itemId, WEAPON_KEYWORDS) != null) {
            return "Weapon keywords detected";
        }
        if (selectFromKeywords(description + " " + itemId, TOOL_KEYWORDS) != null) {
            return "Tool keywords detected";
        }
        if (selectFromKeywords(description + " " + itemId, MAGIC_KEYWORDS) != null) {
            return "Magic keywords detected";
        }
        if (selectFromKeywords(description + " " + itemId, MATERIAL_KEYWORDS) != null) {
            return "Material keywords detected";
        }
        
        return "Default fallback";
    }
}
