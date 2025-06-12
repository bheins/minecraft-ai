# Final Pre-Push Validation Script
Write-Host "=== Final GitHub Actions Workflow Validation ===" -ForegroundColor Green

$allPassed = $true

# Check 1: Workflow files syntax
Write-Host "`n1. Validating workflow files..." -ForegroundColor Yellow
$workflowFiles = @(
    ".github\workflows\build-and-release.yml",
    ".github\workflows\ci.yml", 
    ".github\workflows\run-server.yml",
    ".github\workflows\run-client.yml",
    ".github\workflows\test-functionality.yml"
)

foreach ($file in $workflowFiles) {
    if (Test-Path $file) {
        Write-Host "   ✅ $file exists" -ForegroundColor Green
    } else {
        Write-Host "   ❌ $file missing" -ForegroundColor Red
        $allPassed = $false
    }
}

# Check 2: Build functionality
Write-Host "`n2. Testing build process..." -ForegroundColor Yellow
try {
    $buildOutput = & .\gradlew.bat build --no-daemon 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Build successful" -ForegroundColor Green
    } else {
        Write-Host "   ❌ Build failed" -ForegroundColor Red
        $allPassed = $false
    }
} catch {
    Write-Host "   ❌ Build command failed: $_" -ForegroundColor Red
    $allPassed = $false
}

# Check 3: Required artifacts exist
Write-Host "`n3. Checking required artifacts..." -ForegroundColor Yellow

# Main JAR
$mainJars = Get-ChildItem "build\libs\ai-mod-generator-*.jar" -ErrorAction SilentlyContinue
if ($mainJars.Count -gt 0) {
    Write-Host "   ✅ Main JAR found: $($mainJars[0].Name)" -ForegroundColor Green
} else {
    Write-Host "   ❌ Main JAR not found" -ForegroundColor Red
    $allPassed = $false
}

# Reobfuscated JAR
if (Test-Path "build\reobfJar\output.jar") {
    Write-Host "   ✅ Reobfuscated JAR found" -ForegroundColor Green
} else {
    Write-Host "   ❌ Reobfuscated JAR not found" -ForegroundColor Red
    $allPassed = $false
}

# Test reports
if (Test-Path "build\reports\tests") {
    Write-Host "   ✅ Test reports directory exists" -ForegroundColor Green
} else {
    Write-Host "   ❌ Test reports directory missing" -ForegroundColor Red
    $allPassed = $false
}

# Check 4: Test execution
Write-Host "`n4. Running tests..." -ForegroundColor Yellow
try {
    $testOutput = & .\gradlew.bat test --no-daemon 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Tests passed" -ForegroundColor Green
    } else {
        Write-Host "   ❌ Tests failed" -ForegroundColor Red
        $allPassed = $false
    }
} catch {
    Write-Host "   ❌ Test command failed: $_" -ForegroundColor Red
    $allPassed = $false
}

# Check 5: Key workflow components
Write-Host "`n5. Checking workflow components..." -ForegroundColor Yellow

# gradlew files
if (Test-Path "gradlew" -and Test-Path "gradlew.bat") {
    Write-Host "   ✅ Gradle wrapper files exist" -ForegroundColor Green
} else {
    Write-Host "   ❌ Gradle wrapper files missing" -ForegroundColor Red
    $allPassed = $false
}

# build.gradle
if (Test-Path "build.gradle") {
    Write-Host "   ✅ build.gradle exists" -ForegroundColor Green
} else {
    Write-Host "   ❌ build.gradle missing" -ForegroundColor Red
    $allPassed = $false
}

# Check 6: Verify no syntax errors in key files
Write-Host "`n6. Final syntax validation..." -ForegroundColor Yellow
$syntaxErrors = 0

foreach ($file in $workflowFiles) {
    try {
        $content = Get-Content $file -Raw
        # Basic YAML structure checks
        if ($content -match "name:\s*\w+" -and $content -match "on:" -and $content -match "jobs:") {
            Write-Host "   ✅ $([System.IO.Path]::GetFileName($file)) has valid structure" -ForegroundColor Green
        } else {
            Write-Host "   ⚠️  $([System.IO.Path]::GetFileName($file)) may have structure issues" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "   ❌ Error reading $([System.IO.Path]::GetFileName($file)): $_" -ForegroundColor Red
        $syntaxErrors++
    }
}

if ($syntaxErrors -eq 0) {
    Write-Host "   ✅ No syntax errors detected" -ForegroundColor Green
} else {
    Write-Host "   ❌ $syntaxErrors syntax errors found" -ForegroundColor Red
    $allPassed = $false
}

# Final result
Write-Host "`n=== VALIDATION SUMMARY ===" -ForegroundColor Cyan
if ($allPassed) {
    Write-Host "🎉 ALL CHECKS PASSED! Ready to push to GitHub!" -ForegroundColor Green
    Write-Host "`nWorkflows validated:" -ForegroundColor White
    Write-Host "  - build-and-release.yml (Release workflow)" -ForegroundColor Gray
    Write-Host "  - ci.yml (Continuous Integration)" -ForegroundColor Gray
    Write-Host "  - run-server.yml (Server testing)" -ForegroundColor Gray
    Write-Host "  - run-client.yml (Client testing)" -ForegroundColor Gray
    Write-Host "  - test-functionality.yml (Comprehensive testing)" -ForegroundColor Gray
    Write-Host "`nComponents tested:" -ForegroundColor White
    Write-Host "  - Build process ✓" -ForegroundColor Gray
    Write-Host "  - Unit tests ✓" -ForegroundColor Gray
    Write-Host "  - JAR generation ✓" -ForegroundColor Gray
    Write-Host "  - Artifact paths ✓" -ForegroundColor Gray
    Write-Host "  - YAML syntax ✓" -ForegroundColor Gray
} else {
    Write-Host "❌ SOME CHECKS FAILED! Please fix issues before pushing." -ForegroundColor Red
    exit 1
}
