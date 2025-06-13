# PowerShell script to validate GitHub Action workflow YAML files

function Test-YamlSyntax {
    param([string]$filePath)
    
    try {
        # PowerShell Core has ConvertFrom-Yaml, but we'll use a more compatible approach
        $content = Get-Content -Path $filePath -Raw
        
        # Simple check for common YAML errors
        $hasErrors = $false
        
        # Check for indentation consistency
        $lines = $content -split "`n"
        $previousIndent = 0
        
        foreach ($line in $lines) {
            if ($line.Trim() -eq "") { continue }
            if ($line.Trim().StartsWith("#")) { continue }
            
            $indent = ($line -replace "^(\s*).*", '$1').Length
            
            # Check for sudden indent jumps (more than 2 spaces)
            if (($indent - $previousIndent) -gt 4 -and -not $line.Trim().StartsWith("-")) {
                Write-Host "⚠️ Potential indentation issue at line: $line" -ForegroundColor Yellow
                $hasErrors = $true
            }
            
            $previousIndent = $indent
        }
        
        # Check for common YAML symbols that could indicate issues
        if ($content -match "[^\s]:((?!\s))") {
            Write-Host "⚠️ Potential missing space after colon in file: $filePath" -ForegroundColor Yellow
            $hasErrors = $true
        }
        
        if ($hasErrors) {
            Write-Host "⚠️ File $filePath might have syntax issues, but could still be valid" -ForegroundColor Yellow
        }
        else {
            Write-Host "✅ File $filePath appears to have valid syntax" -ForegroundColor Green
        }
        
        return -not $hasErrors
    }
    catch {
        Write-Host "❌ Error checking $filePath: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Get all workflow files
$workflowFiles = Get-ChildItem -Path ".github/workflows/*.yml"
$allValid = $true

Write-Host "🔍 Checking GitHub Actions workflow files..." -ForegroundColor Cyan
foreach ($file in $workflowFiles) {
    $isValid = Test-YamlSyntax -filePath $file.FullName
    if (-not $isValid) {
        $allValid = $false
    }
}

# Now let's check if the build works with our fixed workflow
Write-Host "`n🏗️ Testing build..." -ForegroundColor Cyan
try {
    & ./gradlew build --no-daemon 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Build successful" -ForegroundColor Green
        
        # Check if JAR files were created
        $jarFiles = Get-ChildItem -Path "build/libs/ai-mod-generator*.jar" -ErrorAction SilentlyContinue
        if ($jarFiles.Count -gt 0) {
            Write-Host "✅ JAR files generated:" -ForegroundColor Green
            foreach ($jar in $jarFiles) {
                Write-Host "   - $($jar.Name)" -ForegroundColor Green
            }
        }
        else {
            Write-Host "❌ No JAR files were generated in build/libs/" -ForegroundColor Red
            $allValid = $false
        }
    }
    else {
        Write-Host "❌ Build failed with exit code $LASTEXITCODE" -ForegroundColor Red
        $allValid = $false
    }
}
catch {
    Write-Host "❌ Error running build: $_" -ForegroundColor Red
    $allValid = $false
}

# Summary
Write-Host "`n📋 Summary:" -ForegroundColor Cyan
if ($allValid) {
    Write-Host "✅ All checks passed! Your workflow files appear to be valid and the build works." -ForegroundColor Green
    Write-Host "   You can now push these changes to GitHub and test the workflows online." -ForegroundColor Green
}
else {
    Write-Host "⚠️ Some issues were detected. Please review the output above." -ForegroundColor Yellow
}

Write-Host "`nRelease Workflow Tips:" -ForegroundColor Cyan
Write-Host "  1. To trigger the release workflow, push a tag matching 'v*' (e.g., v2.0.0)"
Write-Host "  2. Or use the workflow_dispatch trigger with a tag parameter"
Write-Host "  3. Make sure the version in build.gradle matches your release tag"
Write-Host "  4. Current version in build.gradle: $(Get-Content build.gradle | Select-String -Pattern 'version\s*=\s*')"
