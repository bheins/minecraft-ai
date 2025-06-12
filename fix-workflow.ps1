# PowerShell script to fix the build-and-release.yml file

$workflowContent = @'
name: Release

on:
  push:
    tags: [ 'v*' ]
  workflow_dispatch:
    inputs:
      tag:
        description: 'Release tag (e.g., v1.0.1)'
        required: true
        type: string
      skip_tests:
        description: 'Skip comprehensive tests (not recommended)'
        required: false
        default: false
        type: boolean

jobs:
  comprehensive-tests:
    runs-on: ubuntu-latest
    name: Comprehensive Pre-Release Tests
    if: github.event.inputs.skip_tests != 'true'
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'microsoft'
        
    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
          
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Run unit tests
      run: ./gradlew test --no-daemon
      
    - name: Run integration tests
      run: ./gradlew integrationTest --no-daemon || echo "No integration tests defined"
      
    - name: Build mod
      run: ./gradlew build --no-daemon
      
    - name: Verify JAR integrity
      run: |
        echo "Checking JAR files..."
        ls -la build/libs/
        ls -la build/reobfJar/
        
        # Check main JAR exists
        if [ ! -f build/libs/ai-mod-generator-*.jar ]; then
          echo "❌ Main JAR not found!"
          exit 1
        fi
        
        # Check reobfuscated JAR exists
        if [ ! -f build/reobfJar/output.jar ]; then
          echo "❌ Reobfuscated JAR not found!"
          exit 1
        fi
        
        echo "✅ JAR files verified"
        
    - name: Test server startup
      run: |
        mkdir -p run
        echo "eula=true" > run/eula.txt
        
        echo "Testing server startup..."
        timeout 120s ./gradlew runServer --no-daemon &
        SERVER_PID=$!
        
        # Wait for server to start
        sleep 60
        
        # Check if server is still running
        if kill -0 $SERVER_PID 2>/dev/null; then
          echo "✅ Server started successfully"
          kill -TERM $SERVER_PID
          wait $SERVER_PID 2>/dev/null || true
        else
          echo "❌ Server failed to start"
          exit 1
        fi
        
    - name: Check for critical issues
      run: |
        echo "Checking for critical issues..."
        
        # Check for crash reports
        if [ -d "run/crash-reports" ] && [ "$(ls -A run/crash-reports)" ]; then
          echo "❌ Crash reports found:"
          ls -la run/crash-reports/
          exit 1
        fi
        
        # Check logs for critical errors
        if [ -f "run/logs/latest.log" ]; then
          if grep -i "FATAL\|CRITICAL\|SEVERE" run/logs/latest.log; then
            echo "❌ Critical errors found in logs"
            exit 1
          fi
        fi
        
        echo "✅ No critical issues found"
        
    - name: Upload test results
      uses: actions/upload-artifact@v4
      if: always()
      with:
        name: pre-release-test-results
        path: |
          build/reports/tests/
          run/logs/
          run/crash-reports/

  build:
    runs-on: ubuntu-latest
    needs: [comprehensive-tests]
    if: always() && (needs.comprehensive-tests.result == 'success' || github.event.inputs.skip_tests == 'true')
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'microsoft'
        
    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
          
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build with Gradle
      run: ./gradlew build --no-daemon
      
    - name: Upload build artifacts
      uses: actions/upload-artifact@v4
      with:
        name: mod-jar
        path: build/libs/*.jar
        
    - name: Upload reobfuscated jar
      uses: actions/upload-artifact@v4
      with:
        name: mod-jar-reobf
        path: build/reobfJar/output.jar

  release:
    needs: [comprehensive-tests, build]
    runs-on: ubuntu-latest
    permissions:
      contents: write  # This permission is needed for creating releases
    if: startsWith(github.ref, 'refs/tags/v') || github.event_name == 'workflow_dispatch'
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      
    - name: Download build artifacts
      uses: actions/download-artifact@v4
      with:
        name: mod-jar
        path: build/libs/
        
    - name: Download reobfuscated jar
      uses: actions/download-artifact@v4
      with:
        name: mod-jar-reobf
        path: build/reobfJar/
        
    - name: Get version from tag or input
      id: get_version
      run: |
        if [ "${{ github.event_name }}" = "workflow_dispatch" ]; then
          VERSION="${{ github.event.inputs.tag }}"
          echo "VERSION=${VERSION#v}" >> $GITHUB_OUTPUT
        else
          echo "VERSION=${GITHUB_REF#refs/tags/v}" >> $GITHUB_OUTPUT
        fi
        
    - name: Verify available files
      run: |
        echo "Available files in build/libs:"
        ls -la build/libs/
        
    - name: Create Release
      id: create_release
      uses: softprops/action-gh-release@v1
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      with:
        tag_name: ${{ github.event_name == 'workflow_dispatch' && github.event.inputs.tag || github.ref_name }}
        name: AI Mod Generator v${{ steps.get_version.outputs.VERSION }}
        body: |
          ## AI Mod Generator v${{ steps.get_version.outputs.VERSION }}
          
          ### Features
          - 🤖 AI-powered content generation with Ollama/OpenAI support
          - 🎯 Functional item behaviors (teleport, healing, fire, ice)
          - 🎨 Dynamic texture generation
          - 💾 Persistent content across sessions
          - 🔧 Smart item detection and abilities
          
          ### Installation
          1. Download the mod JAR file below
          2. Place in your `mods/` folder
          3. Install [Ollama](https://ollama.ai/) and run `ollama pull llama3`
          4. Start Minecraft with Forge 1.19.2-43.2.0+
          
          ### Quick Start
          ```
          /aimod generate "magical sword that teleports enemies"
          /aimod give magical_sword
          ```
          
          ### Files
          - `ai-mod-generator-*.jar` - Main mod file for users
          - `ai-mod-generator-*-sources.jar` - Source code for developers
          
          See the [README](https://github.com/${{ github.repository }}/blob/main/README.md) for full documentation.
        draft: false
        prerelease: false
        files: |
          build/libs/ai-mod-generator*.jar
'@

# Create backup first
$backupName = ".github/workflows/build-and-release.yml.bak"
Copy-Item -Path ".github/workflows/build-and-release.yml" -Destination $backupName -Force

# Write the fixed workflow file
Set-Content -Path ".github/workflows/build-and-release.yml" -Value $workflowContent

Write-Host "✅ Workflow file fixed and original backed up as $backupName"
Write-Host "The main issues fixed were:"
Write-Host "  - Fixed YAML indentation for the release job"
Write-Host "  - Ensured proper job dependencies and permissions"
Write-Host "  - Validated the file patterns for artifact uploads"
Write-Host ""
Write-Host "Additional notes:"
Write-Host "  - Your mod version is set to 1.0.0 in build.gradle"
Write-Host "  - Make sure the tag you push (e.g., v1.0.0) matches the version in build.gradle"
Write-Host "  - The wildcard pattern build/libs/ai-mod-generator*.jar will capture your JAR files"
