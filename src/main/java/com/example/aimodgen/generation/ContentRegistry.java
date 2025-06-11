package com.example.aimodgen.generation;

import com.example.aimodgen.AiModGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ContentRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String REGISTRY_FILE = "config/aimodgenerator/generated_content.json";
    private static final Map<String, GeneratedContent> contentMap = new HashMap<>();

    public static void init() {
        loadRegistry();
    }

    public static void register(GeneratedContent content) {
        contentMap.put(content.getId(), content);
        saveRegistry();
    }

    public static void unregister(String id) {
        contentMap.remove(id);
        saveRegistry();
    }

    public static GeneratedContent getContent(String id) {
        return contentMap.get(id);
    }

    public static Collection<GeneratedContent> getAllContent() {
        return contentMap.values();
    }

    private static void loadRegistry() {
        try {
            Path registryPath = Paths.get(REGISTRY_FILE);
            if (!Files.exists(registryPath)) {
                Files.createDirectories(registryPath.getParent());
                return;
            }

            String json = Files.readString(registryPath);
            Type type = new TypeToken<HashMap<String, GeneratedContent>>(){}.getType();
            Map<String, GeneratedContent> loaded = GSON.fromJson(json, type);
            if (loaded != null) {
                contentMap.clear();
                contentMap.putAll(loaded);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load content registry: " + e.getMessage());
        }
    }

    private static void saveRegistry() {
        try {
            Path registryPath = Paths.get(REGISTRY_FILE);
            Files.createDirectories(registryPath.getParent());
            String json = GSON.toJson(contentMap);
            Files.writeString(registryPath, json);
        } catch (IOException e) {
            LOGGER.error("Failed to save content registry: " + e.getMessage());
        }
    }
}
