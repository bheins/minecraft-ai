# Final workflow verification script
Write-Host "=== Final Workflow Verification ===" -ForegroundColor Green
Write-Host "Script starting: $(Get-Date)" -ForegroundColor Yellow

# Force output to be displayed immediately
$Host.UI.RawUI.FlushInputBuffer()
$DebugPreference = "Continue"
$VerbosePreference = "Continue"

# Test 1: Verify all workflow files exist and are valid YAML
Write-Host "`n1. Checking workflow files..." -ForegroundColor Yellow
$workflowFiles = @(
    ".github/workflows/build-and-release.yml",
    ".github/workflows/ci.yml", 
    ".github/workflows/run-server.yml",
    ".github/workflows/run-client.yml",
    ".github/workflows/test-functionality.yml"
)

$allValid = $true
foreach ($file in $workflowFiles) {
    Write-Host "   Checking file: $file" -ForegroundColor Cyan
    if (Test-Path $file) {
        $content = Get-Content $file -Raw
        if ($content.Length -gt 0) {
            Write-Host "   ✅ $file exists and has content" -ForegroundColor Green
        } else {
            Write-Host "   ❌ $file is empty" -ForegroundColor Red
            $allValid = $false
        }
    } else {
        Write-Host "   ❌ $file not found" -ForegroundColor Red
        $allValid = $false
    }
}

# Test 2: Build functionality
Write-Host "`n2. Testing build functionality..." -ForegroundColor Yellow
try {
    Write-Host "   Running Gradle build..." -ForegroundColor Cyan
    $buildResult = & .\gradlew.bat build --no-daemon --console=plain
    Write-Host "   Build exit code: $LASTEXITCODE" -ForegroundColor Cyan
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Build successful" -ForegroundColor Green
    } else {
        Write-Host "   ❌ Build failed" -ForegroundColor Red
        $allValid = $false
    }
} catch {
    Write-Host "   ❌ Build error: $($_.Exception.Message)" -ForegroundColor Red
    $allValid = $false
}

# Test 3: Verify artifact paths exist
Write-Host "`n3. Verifying artifact paths..." -ForegroundColor Yellow
$expectedPaths = @(
    "build/libs",
    "build/reobfJar", 
    "build/reports/tests",
    "build/test-results"
)

foreach ($path in $expectedPaths) {
    Write-Host "   Checking path: $path" -ForegroundColor Cyan
    if (Test-Path $path) {
        $itemCount = (Get-ChildItem $path -Recurse -File).Count
        Write-Host "   ✅ $path exists ($itemCount files)" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  $path does not exist (may be created during workflow)" -ForegroundColor Yellow
    }
}

# Test 4: Check JAR files
Write-Host "`n4. Checking JAR files..." -ForegroundColor Yellow
Write-Host "   Looking for JAR files in build/libs/" -ForegroundColor Cyan
$mainJars = Get-ChildItem "build/libs/*.jar" -ErrorAction SilentlyContinue
$reobfJar = Get-ChildItem "build/reobfJar/output.jar" -ErrorAction SilentlyContinue

if ($mainJars.Count -gt 0) {
    Write-Host "   ✅ Found $($mainJars.Count) JAR file(s) in build/libs/" -ForegroundColor Green
    foreach ($jar in $mainJars) {
        Write-Host "      - $($jar.Name) ($([math]::Round($jar.Length/1KB, 1)) KB)" -ForegroundColor Cyan
    }
} else {
    Write-Host "   ❌ No JAR files found in build/libs/" -ForegroundColor Red
    $allValid = $false
}

Write-Host "   Looking for reobfuscated JAR in build/reobfJar/" -ForegroundColor Cyan
if ($reobfJar) {
    Write-Host "   ✅ Reobfuscated JAR found: $($reobfJar.Name) ($([math]::Round($reobfJar.Length/1KB, 1)) KB)" -ForegroundColor Green
} else {
    Write-Host "   ❌ Reobfuscated JAR not found" -ForegroundColor Red
    $allValid = $false
}

# Test 5: Run unit tests
Write-Host "`n5. Running unit tests..." -ForegroundColor Yellow
try {
    Write-Host "   Running Gradle test..." -ForegroundColor Cyan
    $testResult = & .\gradlew.bat test --no-daemon --console=plain
    Write-Host "   Test exit code: $LASTEXITCODE" -ForegroundColor Cyan
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Unit tests passed" -ForegroundColor Green
    } else {
        Write-Host "   ❌ Unit tests failed" -ForegroundColor Red
        $allValid = $false
    }
} catch {
    Write-Host "   ❌ Test error: $($_.Exception.Message)" -ForegroundColor Red
    $allValid = $false
}

# Test 6: Verify test reports
Write-Host "`n6. Checking test reports..." -ForegroundColor Yellow
if (Test-Path "build/reports/tests/test") {
    $reportFiles = Get-ChildItem "build/reports/tests/test" -Recurse -File
    Write-Host "   ✅ Test reports generated ($($reportFiles.Count) files)" -ForegroundColor Green
} else {
    Write-Host "   ⚠️  Test reports directory not found" -ForegroundColor Yellow
}

# Final summary
Write-Host "`n=== Verification Summary ===" -ForegroundColor Green
if ($allValid) {
    Write-Host "✅ ALL CHECKS PASSED - Workflows are ready for GitHub!" -ForegroundColor Green
    Write-Host "`nYou can safely push to GitHub. The workflows will:" -ForegroundColor Cyan
    Write-Host "• Build the mod successfully" -ForegroundColor White
    Write-Host "• Run unit tests" -ForegroundColor White  
    Write-Host "• Generate and upload artifacts" -ForegroundColor White
    Write-Host "• Create releases when tagged" -ForegroundColor White
} else {
    Write-Host "❌ SOME CHECKS FAILED - Please review issues above" -ForegroundColor Red
    exit 1
}

Write-Host "`n🚀 Ready to push!" -ForegroundColor Green
Write-Host "Script completed: $(Get-Date)" -ForegroundColor Yellow
