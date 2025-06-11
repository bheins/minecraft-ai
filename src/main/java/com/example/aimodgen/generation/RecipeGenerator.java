package com.example.aimodgen.generation;

import com.example.aimodgen.AiModGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RecipeGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();    public static void generateRecipe(GeneratedContent content) {
        try {
            // Get recipe from AI
            String recipePrompt = String.format("Generate a balanced Minecraft crafting recipe for %s: %s. " +
                "Return a JSON object with 'type' (shaped or shapeless), 'pattern' (for shaped), " +
                "'ingredients', and 'result' fields.", 
                content.getName(), content.getDescription());
            String response = AiModGenerator.getInstance().getLlmService().generateModContent(recipePrompt);
            
            JsonObject recipe = parseRecipeResponse(response, content);
            if (recipe == null) {
                // Generate default recipe if AI fails
                recipe = generateDefaultRecipe(content);
            }

            // Ensure recipe data directory exists
            Path dataDir = Paths.get("src/main/resources/data/" + AiModGenerator.MOD_ID + "/recipes");
            Files.createDirectories(dataDir);

            // Save recipe
            Path recipePath = dataDir.resolve(content.getId() + ".json");
            Files.writeString(recipePath, GSON.toJson(recipe));
            
            LOGGER.info("Generated recipe for {}", content.getId());
        } catch (IOException e) {
            LOGGER.error("Failed to save recipe: " + e.getMessage());
        }
    }

    private static JsonObject parseRecipeResponse(String response, GeneratedContent content) {
        try {
            // Try to extract JSON from response
            String jsonString = response;
            int startIndex = response.indexOf("{");
            int endIndex = response.lastIndexOf("}");
            
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                jsonString = response.substring(startIndex, endIndex + 1);
            }
            
            JsonObject parsed = GSON.fromJson(jsonString, JsonObject.class);
            
            // Validate the recipe has required fields
            if (parsed != null && parsed.has("type") && parsed.has("result")) {
                // Set the correct result item/block
                JsonObject result = new JsonObject();
                result.addProperty("item", AiModGenerator.MOD_ID + ":" + content.getId());
                result.addProperty("count", 1);
                parsed.add("result", result);
                
                return parsed;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse recipe response: " + e.getMessage());
        }
        return null;
    }

    private static JsonObject generateDefaultRecipe(GeneratedContent content) {
        JsonObject recipe = new JsonObject();
        
        if (content.getType() == ContentType.BLOCK) {
            // Default shaped recipe for blocks
            recipe.addProperty("type", "minecraft:crafting_shaped");
            
            JsonObject pattern = new JsonObject();
            pattern.addProperty("0", "SSS");
            pattern.addProperty("1", "SSS"); 
            pattern.addProperty("2", "SSS");
            recipe.add("pattern", pattern);
            
            JsonObject key = new JsonObject();
            JsonObject stone = new JsonObject();
            stone.addProperty("item", "minecraft:stone");
            key.add("S", stone);
            recipe.add("key", key);
            
        } else {
            // Default shapeless recipe for items
            recipe.addProperty("type", "minecraft:crafting_shapeless");
            
            JsonObject ingredients = new JsonObject();
            JsonObject iron = new JsonObject();
            iron.addProperty("item", "minecraft:iron_ingot");
            JsonObject redstone = new JsonObject();
            redstone.addProperty("item", "minecraft:redstone");
            
            ingredients.add("0", iron);
            ingredients.add("1", redstone);
            recipe.add("ingredients", ingredients);
        }
        
        // Set result
        JsonObject result = new JsonObject();
        result.addProperty("item", AiModGenerator.MOD_ID + ":" + content.getId());
        result.addProperty("count", 1);
        recipe.add("result", result);
        
        return recipe;
    }
}
