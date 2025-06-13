# GitHub Actions Workflows

This repository includes automated workflows for building and releasing the AI Mod Generator.

## 🔄 Workflows

### 1. **Build Mod** (`ci.yml`)
**Equivalent to VS Code task**: "Build Mod"

**Triggers**:
- Push to `main`, `master`, or `develop` branches
- Pull requests to `main` or `master`
- Manual trigger

**Actions**:
- Sets up JDK 17
- Caches Gradle dependencies
- Builds the mod with `./gradlew build`
- Uploads JAR artifacts

### 2. **Run Client** (`run-client.yml`)
**Equivalent to VS Code task**: "Run Client"

**Triggers**:
- Manual trigger only (workflow_dispatch)

**Options**:
- `timeout`: Client timeout in minutes (default: 10)

**Actions**:
- Sets up virtual display (Xvfb)
- Builds and runs `./gradlew runClient`
- Uploads client logs and generated content

### 3. **Run Server** (`run-server.yml`)
**Equivalent to VS Code task**: "Run Server"

**Triggers**:
- Manual trigger only (workflow_dispatch)

**Options**:
- `timeout`: Server timeout in minutes (default: 5)
- `test_commands`: Commands to run on server

**Actions**:
- Accepts Minecraft EULA
- Builds and runs `./gradlew runServer`
- Uploads server logs and world data

### 4. **Test Mod Functionality** (`test-functionality.yml`)
**Extended testing suite**

**Triggers**:
- Push to `main` or `master`
- Pull requests
- Manual trigger

**Test Suites**:
- `basic`: Build and startup tests
- `full`: Feature validation
- `ai-integration`: AI service testing

### 5. **Release** (`build-and-release.yml`)
**Automated release creation**

**Triggers**:
- Tags starting with `v` (e.g., `v1.0.1`)
- Manual trigger with tag input

**Actions**:
- Creates GitHub release
- Uploads release assets

## 🚀 **Running Workflows**

### Manual Triggers

All workflows can be triggered manually from the GitHub Actions tab:

1. **Go to**: `https://github.com/YOUR_USERNAME/YOUR_REPO/actions`
2. **Select workflow** from the left sidebar
3. **Click "Run workflow"** button
4. **Configure options** (if available)
5. **Click "Run workflow"** to start

### Common Use Cases

#### **Build and Test**
```bash
# Equivalent to: ./gradlew build
Workflow: "Build Mod" → Run workflow
```

#### **Test Client Startup**
```bash
# Equivalent to: ./gradlew runClient
Workflow: "Run Client" → Run workflow → Set timeout: 10 minutes
```

#### **Test Server Functionality**
```bash
# Equivalent to: ./gradlew runServer
Workflow: "Run Server" → Run workflow → Set timeout: 5 minutes
```

#### **Full Testing Suite**
```bash
Workflow: "Test Mod Functionality" → Run workflow → Test suite: "full"
```

## 📦 **Workflow Outputs**

### Option 1: Use Release Scripts

**Windows (PowerShell)**:
```powershell
.\scripts\release.ps1 -Version "1.0.1"
```

**Linux/Mac (Bash)**:
```bash
chmod +x scripts/release.sh
./scripts/release.sh 1.0.1
```

### Option 2: Manual Release

1. **Update version in `build.gradle`**:
   ```gradle
   version = '1.0.1'
   ```

2. **Commit and tag**:
   ```bash
   git add build.gradle
   git commit -m "Bump version to 1.0.1"
   git tag -a "v1.0.1" -m "Release version 1.0.1"
   git push origin main
   git push origin v1.0.1
   ```

3. **GitHub Actions automatically**:
   - Builds the mod
   - Creates release on GitHub
   - Uploads JAR files as assets

## Release Assets

Each release includes:

- **`ai-mod-generator-X.Y.Z.jar`** - Main mod file for users
- **`ai-mod-generator-X.Y.Z-sources.jar`** - Source code for developers

## Requirements

- **Java 17** (Microsoft distribution)
- **Gradle** (wrapper included)
- **Minecraft Forge 1.19.2-43.2.0+**

## Build Status

The CI workflow validates:
- ✅ Code compiles successfully
- ✅ Tests pass
- ✅ Gradle wrapper is valid
- ✅ Build artifacts are generated

## Troubleshooting

### Build Fails
- Check Java version is 17
- Ensure Gradle wrapper has execute permissions
- Verify all dependencies are available

### Release Not Created
- Ensure tag starts with `v` (e.g., `v1.0.1`)
- Check GitHub token permissions
- Verify build completed successfully

### Missing Artifacts
- Build artifacts are available for 90 days
- Release assets are permanent
- Check workflow logs for upload errors

## Local Development

To build locally:
```bash
./gradlew build
```

To run client:
```bash
./gradlew runClient
```

Generated files:
- `build/libs/ai-mod-generator-*.jar` - Main mod
- `build/libs/ai-mod-generator-*-sources.jar` - Sources
- `build/reobfJar/output.jar` - Reobfuscated version
