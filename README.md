# AI Mod Generator for Minecraft

A Minecraft Forge mod that uses AI to generate new game content! This mod integrates with OpenAI's API or local LLMs (via LM Studio or Ollama) to dynamically generate new blocks and items with custom textures and properties based on natural language descriptions.

## ✨ Current Status: **FULLY FUNCTIONAL WITH WORKING ABILITIES**

- ✅ **AI Integration**: Working with Ollama + llama3 model
- ✅ **Content Generation**: Items and blocks generated from descriptions
- ✅ **Functional Behaviors**: AI-generated items now have working abilities (teleport, heal, fire, ice)
- ✅ **Smart Item Detection**: Automatically detects and enables abilities from AI properties
- ✅ **Texture System**: AI-generated or fallback textures created
- ✅ **Persistence**: Content saved/loaded across sessions
- ✅ **Item Access**: New `/aimod give` command to access generated items
- ✅ **Recipe Generation**: AI creates crafting recipes
- ✅ **Error Handling**: Comprehensive fallbacks for AI failures

## Features

- 🤖 **AI-Powered Content**: Generate items and blocks using natural language
- 🎨 **Dynamic Textures**: AI generates custom 16x16 pixel art textures
- 📜 **Recipe Creation**: Automatically generates balanced crafting recipes
- 💾 **Persistence**: Generated content survives server restarts
- 🎯 **Instant Access**: Get generated items immediately with `/aimod give`
- 🔧 **Multiple AI Services**: OpenAI, LM Studio, or Ollama support

## Requirements

- Minecraft 1.19.2
- Forge 43.2.0
- Java Development Kit (JDK) 17
- One of the following AI services:
  - **Ollama** (Recommended - Currently tested and working)
  - OpenAI API Key
  - LM Studio (running locally)

## Setup Instructions

1. **Install Java Development Kit 17**
   - Download and install JDK 17 from [Microsoft's OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-17)
   - Set JAVA_HOME environment variable to point to your JDK 17 installation

2. **Clone the Repository**
   ```powershell
   git clone <repository-url>
   cd "Minecraft AI"
   ```

3. **Configure AI Service**
   - Copy `run/config/aimodgenerator-common.toml` to `run/defaultconfigs/` if it doesn't exist
   - Edit the config file and choose your AI service:

   **For Ollama (Recommended - Currently Working):**
   ```toml
   ["AI Mod Generator Configuration"]
      llm_type = "ollama"
      local_llm_url = "http://localhost:11434"
      local_llm_model = "llama3"
   ```

   For OpenAI:
   ```toml
   ["AI Mod Generator Configuration"]
      llm_type = "openai"
      openai_api_key = "your-api-key-here"
   ```

   For LM Studio:
   ```toml
   ["AI Mod Generator Configuration"]
      llm_type = "lmstudio"
      local_llm_url = "http://localhost:1234"
   ```

4. **Setup Local LLM (if not using OpenAI)**
   
   **For Ollama (Recommended - Currently Working):**
   - Install [Ollama](https://ollama.ai/)
   - Pull the llama3 model: `ollama pull llama3`
   - Start Ollama (it automatically runs on port 11434)
   - Verify with: `ollama list` (should show llama3:latest)

   **For LM Studio:**
   - Download and install [LM Studio](https://lmstudio.ai/)
   - Load your preferred model (see recommendations below)
   - Start the local server on port 1234

   **For OpenAI:**
   - Get an API key from [OpenAI](https://platform.openai.com/)
   - Add your API key to the config file

5. **Setup Development Environment**
   ```powershell
   ./gradlew.bat clean build
   ```

6. **Run the Client**
   ```powershell
   ./gradlew.bat runClient
   ```

## Usage Guide

### Available Commands

The mod provides several commands to generate and manage AI-created content:

#### **Generate Content**
```bash
# Generate an item (most common)
/aimod generate "magical sword that heals the player"
/aimod generate "fire staff that shoots fireballs"
/aimod generate "healing potion that restores full health"

# Generate specific item types (legacy syntax)
/aimod generate item "description here"
/aimod generate block "description here"
```

#### **Access Generated Items**
```bash
# List all generated content
/aimod list

# Get a generated item in your inventory
/aimod give <item_name>
/aimod give teleport_wand
/aimod give healing_sword
```

#### **Manage Content**
```bash
# Delete generated content
/aimod delete <item_name>
/aimod delete unwanted_item
```

### Step-by-Step Workflow

1. **Generate Content**
   ```
   /aimod generate "ice sword that freezes enemies"
   ```
   - AI creates item properties and texture
   - Content is saved to persistence file
   - Generation takes ~5 seconds

2. **Check What Was Created**
   ```
   /aimod list
   ```
   - Shows all generated items with descriptions
   - Each item has a unique ID (e.g., "ice_sword")

3. **Get the Item**
   ```
   /aimod give ice_sword
   ```
   - Item appears in your inventory immediately
   - Has custom name, description, and properties
   - Shows as a special item with AI-generated tooltip

4. **Use the Item**
   - Items appear as enchanted sticks with custom NBT data
   - **NEW: Right-click to activate special abilities!**
   - Hover to see AI-generated properties and usage instructions
   - **Teleport Wand**: Right-click to teleport forward (range and cooldown configurable)
   - **Healing Items**: Right-click to restore health when damaged
   - **Fire Staff**: Right-click to create fire blocks around you
   - **Ice Wand**: Right-click to freeze nearby water into ice
   - Properties include damage, durability, special effects

### Example Session

```bash
# Generate a few different items
/aimod generate "golden pickaxe with fortune enchantment"
/aimod generate "magic boots that let you walk on water"
/aimod generate "healing wand that restores health"

# See what was created
/aimod list

# Get the items you want
/aimod give golden_pickaxe
/aimod give magic_boots
/aimod give healing_wand

# Clean up unwanted items
/aimod delete magic_boots
```

### Features in Action

- 🤖 **AI Generation**: Natural language descriptions become structured game content
- 🎨 **Texture Creation**: AI generates 16x16 pixel art or creates colored fallbacks
- 📊 **Smart Properties**: AI determines balanced damage, durability, and special effects
- 💾 **Persistence**: All content survives server restarts
- 🎯 **Instant Access**: No need to restart - items available immediately
- 🔧 **Recipe Generation**: AI creates balanced crafting recipes automatically

### Important Notes

- **Registry Limitation**: Due to Minecraft Forge restrictions, items appear as "enhanced sticks" with custom data rather than entirely new item types
- **Persistence**: Generated content is saved to `run/run/generated_content.json`
- **Textures**: Saved to `run/src/main/resources/assets/aimodgenerator/textures/item/`
- **Performance**: Each generation takes ~5 seconds with Ollama + llama3

## Development

### Project Structure
```
src/main/
├── java/com/example/aimodgen/    # Main mod code
│   ├── ai/                       # AI integration (LLMService, providers)
│   ├── block/                    # Block definitions and registry
│   ├── commands/                 # Chat commands (/aimod)
│   ├── config/                   # Configuration handling
│   ├── generation/               # Content generation logic
│   ├── item/                     # Item definitions and registry
│   └── persistence/              # Save/load system
└── resources/                    # Assets and resources
    ├── assets/aimodgenerator/    # Textures, models, etc.
    └── META-INF/                 # Mod metadata
```

### Building
```powershell
./gradlew.bat clean build
```

The built mod JAR will be in `build/libs/`.

### Running in Development
- Client: `./gradlew.bat runClient`
- Server: `./gradlew.bat runServer`

## Troubleshooting

### Common Issues

**"No generated content found" after creation:**
- Fixed in latest version - persistence system now working correctly
- Content loads automatically on startup

**"Registry is already frozen" errors:**
- This is expected behavior due to Minecraft Forge limitations
- Use `/aimod give <item_name>` to access generated items instead

**AI connection failures:**
- Check your AI service configuration in `aimodgenerator-common.toml`
- Ensure your chosen AI service is running (for local LLMs)
- Verify network connection and service URLs

### LM Studio Specific Issues
- **Won't start**: Make sure you have enough RAM (8GB+ recommended)
- **Connection fails**: Verify server is running on http://localhost:1234
- **Slow responses**: Try a smaller model or enable GPU acceleration
- **JSON parsing fails**: Switch to Llama 3.1 or CodeLlama models for better structured output

### Recommended Models

**Top 3 Models for LM Studio:**
1. **Llama 3.1 8B Instruct** (bartowski/Meta-Llama-3.1-8B-Instruct-GGUF)
   - Best overall performance for creative content generation
   - Excellent at understanding Minecraft context and generating JSON
   - ~8GB RAM required

2. **Mistral 7B Instruct v0.3** (microsoft/Mistral-7B-Instruct-v0.3-GGUF)
   - Great balance of speed and quality
   - Good at following structured prompts
   - ~6GB RAM required

3. **CodeLlama 7B Instruct** (codellama/CodeLlama-7b-Instruct-hf-GGUF)
   - Excellent for generating structured data and properties
   - Very reliable for JSON output format
   - ~6GB RAM required

**Performance Tips:**
- **Fastest generation**: Use Mistral 7B with Q4_K_S quantization
- **Best quality**: Use Llama 3.1 8B with Q4_K_M quantization  
- **Most reliable JSON**: Use CodeLlama 7B Instruct

## Configuration

The mod can be configured through the following files:
- `run/config/aimodgenerator-common.toml` - Main mod configuration
- `run/config/forge-client.toml` - Forge client settings
- `run/config/forge-common.toml` - Forge common settings

## Recent Updates

### Version 1.0.1 - June 2025
- 🎯 **NEW: Functional Item Behaviors** - AI-generated items now have working abilities!
  - **Teleport Wand**: Right-click to teleport forward with customizable range and cooldown
  - **Healing Items**: Right-click to restore health with configurable heal amounts
  - **Fire Staff**: Right-click to create fire in a radius around the player
  - **Ice Wand**: Right-click to freeze water blocks into ice
  - **Smart Detection**: Items automatically detect abilities from AI-generated properties
  - **Usage Instructions**: Items now show "Right-click to teleport" etc. in tooltips
  - **Cooldown System**: Prevents spam-clicking with NBT-based cooldown tracking
  - **Visual/Audio Feedback**: Sound effects and particles for all abilities

### Version 1.0.0 - June 2025
- ✅ **Fixed persistence system** - Generated content now loads correctly
- ✅ **Added `/aimod give` command** - Instant access to generated items
- ✅ **Improved texture generation** - Better AI response parsing and fallbacks
- ✅ **Enhanced error handling** - Comprehensive fallback systems
- ✅ **Ollama integration** - Tested and working with llama3 model
- ✅ **Recipe generation** - AI creates balanced crafting recipes

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.