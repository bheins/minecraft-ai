# Simple test script to verify PowerShell output is working
Write-Host "=== Simple PowerShell Output Test ===" -ForegroundColor Green
Write-Host "This is a test message" -ForegroundColor Yellow
Write-Host "Current directory: $(Get-Location)" -ForegroundColor Cyan

# Test file system operations
$files = Get-ChildItem -Path "." -File | Select-Object -First 5
Write-Host "`nFirst 5 files in current directory:" -ForegroundColor Magenta
foreach ($file in $files) {
    Write-Host " - $($file.Name)" -ForegroundColor White
}

# Test external command execution
Write-Host "`nRunning simple gradlew command:" -ForegroundColor Yellow
& .\gradlew.bat --version

Write-Host "`nTest complete!" -ForegroundColor Green
