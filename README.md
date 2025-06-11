# AI Mod Generator for Minecraft

A Minecraft Forge mod that uses AI to generate new game content! This mod integrates with OpenAI's API or local LLMs (via LM Studio or Ollama) to dynamically generate new blocks and items in Minecraft.

## Requirements

- Minecraft 1.19.2
- Forge 43.2.0
- Java Development Kit (JDK) 17
- One of the following:
  - OpenAI API Key
  - LM Studio (running locally)
  - Ollama (running locally)

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

   For OpenAI:
   ```toml
   ["AI Mod Generator Configuration"]
      llm_type = "openai"
      openai_api_key = "your-api-key-here"
   ```   For LM Studio:
   ```toml
   ["AI Mod Generator Configuration"]
      llm_type = "lmstudio"
      local_llm_url = "http://localhost:1234"
   ```

   **✅ CURRENTLY WORKING WITH OLLAMA:**
   For Ollama (Recommended - Currently Working):
   ```toml
   ["AI Mod Generator Configuration"]
      llm_type = "ollama"
      local_llm_url = "http://localhost:11434"
      local_llm_model = "llama3"
   ```

4. **Setup Local LLM (if not using OpenAI)**
     For LM Studio:
   - Download and install [LM Studio](https://lmstudio.ai/)
   - Load your preferred model (see recommendations below)
   - Start the local server on port 1234

   **Top 3 Recommended Models for LM Studio:**
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

   **Installation Tips:**
   - Choose Q4_K_M quantization for best quality/performance balance
   - Ensure you have at least 8GB RAM for smooth operation
   - Enable GPU acceleration if you have a compatible graphics card

   For Ollama:
   - Install [Ollama](https://ollama.ai/)
   - Pull your preferred model: `ollama pull mistral`
   - Ollama will automatically start its server on port 11434

5. **Setup Development Environment**
   ```powershell
   ./gradlew.bat clean build
   ```

6. **Run the Client**
   ```powershell
   ./gradlew.bat runClient
   ```

## Features

- AI-generated blocks with custom textures
- Creative mode tab for mod items
- Integration with OpenAI's API for content generation
- Configurable through the mod's configuration file

## Development

### Project Structure
```
src/main/
├── java/com/example/aimodgen/    # Main mod code
│   ├── ai/                       # AI integration
│   ├── block/                    # Block definitions
│   ├── config/                   # Configuration
│   └── init/                     # Initialization classes
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

## Usage Guide

### Generating Content
1. **Launch Minecraft** with the mod installed and configured
2. **Open Chat** (press T key by default)
3. Use the following commands to generate content:

```
/aimod generate block <description>
Example: /aimod generate block a glowing crystal block that pulses with energy

/aimod generate item <description>
Example: /aimod generate item a magical staff that controls the weather
```

### Examples

1. **Creating a Custom Block**
```
/aimod generate block ancient runic stone that glows in the dark
```
This will:
- Generate a block texture using AI
- Create a new block with appropriate properties
- Add it to the "AI Mod Generator" creative tab
- Create necessary crafting recipes

The block will appear in your creative inventory under the "AI Mod Generator" tab.

2. **Creating a Custom Item**
```
/aimod generate item enchanted compass that points to rare ores
```
This will:
- Generate an item texture
- Create a new item with custom behaviors
- Add it to the creative tab
- Set up any necessary crafting recipes

3. **Customizing Properties**
You can add specific properties to your generation request:
```
/aimod generate block volcanic rock that damages players when touched hardness=50 light_level=15
/aimod generate item magical sword damage=8 durability=1000
```

### Tips
- Be specific in your descriptions
- Include key properties you want in the generation
- You can find generated items/blocks in the "AI Mod Generator" creative tab
- Use `/aimod list` to see all generated content
- Use `/aimod delete <name>` to remove generated content

### Troubleshooting

If you see "Failed to generate content":
1. Check your AI service configuration in `aimodgenerator-common.toml`
2. Ensure your chosen AI service is running (for local LLMs)
3. Verify your network connection
4. Check the logs in `run/logs/latest.log` for detailed error messages

**LM Studio Specific Issues:**
- If LM Studio won't start: Make sure you have enough RAM (8GB+ recommended)
- If connection fails: Verify the server is running on http://localhost:1234
- If responses are slow: Try a smaller model or enable GPU acceleration
- If JSON parsing fails: Switch to Llama 3.1 or CodeLlama models for better structured output

**Model Performance Tips:**
- **For fastest generation**: Use Mistral 7B with Q4_K_S quantization
- **For best quality**: Use Llama 3.1 8B with Q4_K_M quantization  
- **For most reliable JSON**: Use CodeLlama 7B Instruct

## Configuration

The mod can be configured through the following files:
- `run/config/aimodgenerator-common.toml` - Main mod configuration
- `run/config/forge-client.toml` - Forge client settings
- `run/config/forge-common.toml` - Forge common settings

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
