# LM Studio Setup Guide for AI Mod Generator

## Quick Start

### 1. Download LM Studio
- Go to [LM Studio](https://lmstudio.ai/)
- Download and install for your operating system
- Launch the application

### 2. Recommended Models (Top 3)

#### 🥇 #1: Llama 3.1 8B Instruct ⭐ BEST OVERALL
```
Model: bartowski/Meta-Llama-3.1-8B-Instruct-GGUF
File: Meta-Llama-3.1-8B-Instruct-Q4_K_M.gguf
Size: ~4.9GB
RAM Required: ~8GB
```
**Why this model:**
- Excellent creative content generation
- Best understanding of Minecraft concepts
- Most reliable JSON output
- Great at following complex prompts

#### 🥈 #2: Mistral 7B Instruct v0.3 ⚡ FASTEST
```
Model: microsoft/Mistral-7B-Instruct-v0.3-GGUF
File: Mistral-7B-Instruct-v0.3-Q4_K_M.gguf
Size: ~4.1GB
RAM Required: ~6GB
```
**Why this model:**
- Fastest generation speed
- Good quality output
- Lower RAM requirements
- Reliable structured responses

#### 🥉 #3: CodeLlama 7B Instruct 🎯 MOST RELIABLE
```
Model: codellama/CodeLlama-7b-Instruct-hf-GGUF
File: CodeLlama-7b-Instruct-hf-Q4_K_M.gguf
Size: ~4.1GB
RAM Required: ~6GB
```
**Why this model:**
- Best for structured data generation
- Most consistent JSON formatting
- Excellent property generation
- Very stable outputs

### 3. Installation Steps

1. **In LM Studio:**
   - Click on "🔍 Discover" tab
   - Search for one of the recommended models above
   - Click "Download" next to the Q4_K_M version
   - Wait for download to complete

2. **Load the Model:**
   - Go to "💬 Chat" tab
   - Click "Select a model to load"
   - Choose your downloaded model
   - Click "Load Model"

3. **Start the Server:**
   - Go to "↔️ Local Server" tab
   - Click "Start Server"
   - Verify it shows "Server running on port 1234"
   - Keep LM Studio open while using the mod

### 4. Performance Optimization

#### For Better Speed:
- Use Q4_K_S quantization (smaller files)
- Enable GPU acceleration in LM Studio settings
- Close other memory-intensive applications

#### For Better Quality:
- Use Q4_K_M quantization (recommended)
- Increase context length to 4096 tokens
- Use temperature setting around 0.7

#### System Requirements:
- **Minimum**: 8GB RAM, 5GB free disk space
- **Recommended**: 16GB RAM, NVIDIA/AMD GPU
- **Optimal**: 32GB RAM, RTX 3060 or better

### 5. Testing Your Setup

1. **Test LM Studio Server:**
   - Open browser to http://localhost:1234
   - You should see LM Studio API documentation

2. **Test with Minecraft Mod:**
   - Launch Minecraft with the mod
   - Run: `/aimod generate block test crystal`
   - Check logs for successful generation

### 6. Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Model won't load | Check available RAM, close other apps |
| Server won't start | Restart LM Studio, check port 1234 isn't in use |
| Slow generation | Try smaller model or enable GPU acceleration |
| Poor JSON output | Switch to CodeLlama or Llama 3.1 models |
| Connection timeout | Increase timeout in mod config, check firewall |

### 7. Alternative Models (If Top 3 Don't Work)

- **Phi-3 Mini 4K Instruct** (lightweight, 2GB RAM)
- **Zephyr 7B Beta** (good creativity, 6GB RAM)
- **Vicuna 7B v1.5** (balanced performance, 6GB RAM)

## Pro Tips

✅ **Use Q4_K_M quantization** - Best quality/size balance  
✅ **Keep LM Studio open** - Server stops when app closes  
✅ **Monitor RAM usage** - Models need headroom to run smoothly  
✅ **Test with simple prompts first** - Verify connection before complex generation  
✅ **Update models regularly** - Newer versions often perform better  

Happy content generating! 🎮✨
