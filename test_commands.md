# AI Mod Generator - Test Commands

## Testing the AI Content Generation

Once the mod is loaded in Minecraft, you can test the following commands:

### Generate a Block
```
/aimod generate block diamond-like crystal that glows blue hardness=3.0 lightLevel=10
```

### Generate an Item
```
/aimod generate item magical sword that deals extra damage maxDurability=1000
```

### List Generated Content
```
/aimod list
```

### Delete Generated Content
```
/aimod delete <content_name>
```

## Expected Behavior

1. **Block Generation**: Creates a new block with AI-generated properties and texture
2. **Item Generation**: Creates a new item with AI-generated properties and texture
3. **Texture Generation**: Automatically generates 16x16 textures using AI
4. **Recipe Generation**: Creates crafting recipes for the generated content
5. **Persistence**: Saves all generated content to JSON files
6. **Re-registration**: Loads previously generated content on mod startup

## Configuration

Make sure your AI service is configured in:
- `run/config/aimodgenerator-common.toml`

The mod supports:
- LM Studio (local AI models)
- Ollama (local AI models)
- Custom AI services

## File Locations

- Generated textures: `src/main/resources/assets/aimodgenerator/textures/`
- Saved content: JSON files in the mod's data directory
- Logs: Check the Minecraft logs for generation status
