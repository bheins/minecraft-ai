package com.example.aimodgen.persistence;

import com.example.aimodgen.generation.ContentType;
import com.example.aimodgen.generation.GeneratedContent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ContentPersistence {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SAVE_FILE = "run/generated_content.json";
    private static final String PENDING_FILE = "run/pending_content.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, GeneratedContent> pendingContent = new HashMap<>();

    public static void saveGeneratedContent(Map<String, GeneratedContent> content) {
        try {
            // Ensure directory exists
            Path savePath = Paths.get(SAVE_FILE);
            Files.createDirectories(savePath.getParent());

            // Convert to JSON-serializable format
            Map<String, JsonObject> serializable = new HashMap<>();
            for (Map.Entry<String, GeneratedContent> entry : content.entrySet()) {
                JsonObject contentObj = new JsonObject();
                contentObj.addProperty("type", entry.getValue().getType().name());
                contentObj.addProperty("id", entry.getValue().getId());
                contentObj.addProperty("description", entry.getValue().getDescription());
                contentObj.add("properties", entry.getValue().getProperties());
                
                serializable.put(entry.getKey(), contentObj);
            }

            // Write to file
            try (FileWriter writer = new FileWriter(SAVE_FILE)) {
                GSON.toJson(serializable, writer);
            }

            LOGGER.info("Saved {} generated content items", content.size());
        } catch (IOException e) {
            LOGGER.error("Failed to save generated content: " + e.getMessage());
        }
    }    public static Map<String, GeneratedContent> loadGeneratedContent() {
        Map<String, GeneratedContent> content = new HashMap<>();
        
        try {
            Path savePath = Paths.get(SAVE_FILE);
            if (!Files.exists(savePath)) {
                LOGGER.info("No saved content found, starting fresh");
                return content;
            }

            try (FileReader reader = new FileReader(SAVE_FILE)) {
                // Parse as JsonObject first, then iterate through elements
                JsonObject rootObj = GSON.fromJson(reader, JsonObject.class);
                
                if (rootObj != null) {
                    for (Map.Entry<String, com.google.gson.JsonElement> entry : rootObj.entrySet()) {
                        JsonObject obj = entry.getValue().getAsJsonObject();
                        
                        ContentType type = ContentType.valueOf(obj.get("type").getAsString());
                        String id = obj.get("id").getAsString();
                        String description = obj.get("description").getAsString();
                        JsonObject properties = obj.getAsJsonObject("properties");
                        
                        GeneratedContent generatedContent = new GeneratedContent(
                            type, id, description, properties, new byte[0]
                        );
                        
                        content.put(entry.getKey(), generatedContent);
                    }
                }
            }

            LOGGER.info("Loaded {} generated content items", content.size());
        } catch (Exception e) {
            LOGGER.error("Failed to load generated content: " + e.getMessage());
        }

        return content;
    }

    public static void addPendingContent(GeneratedContent content) {
        pendingContent.put(content.getId(), content);
        savePendingContent();
    }
    
    public static void savePendingContent() {
        try {
            // Ensure directory exists
            Path savePath = Paths.get(PENDING_FILE);
            Files.createDirectories(savePath.getParent());

            // Convert to JSON-serializable format
            Map<String, JsonObject> serializable = new HashMap<>();
            for (Map.Entry<String, GeneratedContent> entry : pendingContent.entrySet()) {
                JsonObject contentObj = new JsonObject();
                contentObj.addProperty("type", entry.getValue().getType().name());
                contentObj.addProperty("id", entry.getValue().getId());
                contentObj.addProperty("description", entry.getValue().getDescription());
                contentObj.add("properties", entry.getValue().getProperties());
                
                serializable.put(entry.getKey(), contentObj);
            }

            // Write to file
            try (FileWriter writer = new FileWriter(PENDING_FILE)) {
                GSON.toJson(serializable, writer);
            }

            LOGGER.info("Saved {} pending content items", pendingContent.size());
        } catch (IOException e) {
            LOGGER.error("Failed to save pending content: " + e.getMessage());
        }
    }
    
    public static Map<String, GeneratedContent> loadPendingContent() {
        Map<String, GeneratedContent> content = new HashMap<>();
        
        try {
            Path savePath = Paths.get(PENDING_FILE);
            if (!Files.exists(savePath)) {
                return content;
            }

            try (FileReader reader = new FileReader(PENDING_FILE)) {
                JsonObject rootObj = GSON.fromJson(reader, JsonObject.class);
                
                if (rootObj != null) {
                    for (Map.Entry<String, com.google.gson.JsonElement> entry : rootObj.entrySet()) {
                        JsonObject obj = entry.getValue().getAsJsonObject();
                        
                        ContentType type = ContentType.valueOf(obj.get("type").getAsString());
                        String id = obj.get("id").getAsString();
                        String description = obj.get("description").getAsString();
                        JsonObject properties = obj.getAsJsonObject("properties");
                        
                        GeneratedContent generatedContent = new GeneratedContent(
                            type, id, description, properties, new byte[0]
                        );
                        
                        content.put(entry.getKey(), generatedContent);
                    }
                }
                
                // Clear the pending file after loading
                Files.deleteIfExists(savePath);
            }

            LOGGER.info("Loaded {} pending content items", content.size());
        } catch (Exception e) {
            LOGGER.error("Failed to load pending content: " + e.getMessage());
        }

        return content;
    }
}
