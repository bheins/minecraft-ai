package com.example.aimodgen.generation;

import com.google.gson.JsonObject;

public class GeneratedContent {
    private final ContentType type;
    private final String id;
    private final String description;
    private final JsonObject properties;
    private final byte[] textureData;

    public GeneratedContent(ContentType type, String id, String description, JsonObject properties, byte[] textureData) {
        this.type = type;
        this.id = id;
        this.description = description;
        this.properties = properties;
        this.textureData = textureData;
    }

    public ContentType getType() { return type; }
    public String getId() { return id; }
    public String getName() { 
        // Extract name from properties or use id as fallback
        if (properties != null && properties.has("name")) {
            return properties.get("name").getAsString();
        }
        return id; 
    }
    public String getDescription() { return description; }
    public JsonObject getProperties() { return properties; }
    public byte[] getTextureData() { return textureData; }
}
