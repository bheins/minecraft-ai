# Release script for AI Mod Generator (PowerShell)
# Usage: .\scripts\release.ps1 -Version "1.0.1"

param(
    [Parameter(Mandatory=$true)]
    [string]$Version
)

# Validate version format
if ($Version -notmatch '^\d+\.\d+\.\d+$') {
    Write-Error "Version must be in format X.Y.Z (e.g., 1.0.1)"
    exit 1
}

Write-Host "Creating release for version $Version" -ForegroundColor Green

# Check current branch
$branch = git rev-parse --abbrev-ref HEAD
if ($branch -ne "main" -and $branch -ne "master") {
    Write-Warning "Not on main/master branch (currently on $branch)"
    $response = Read-Host "Continue anyway? (y/N)"
    if ($response -ne 'y' -and $response -ne 'Y') {
        exit 1
    }
}

# Check for uncommitted changes
$status = git status --porcelain
if ($status) {
    Write-Error "You have uncommitted changes. Please commit or stash them first."
    exit 1
}

# Update version in build.gradle
Write-Host "Updating version in build.gradle..." -ForegroundColor Yellow
$buildGradle = Get-Content "build.gradle" -Raw
$buildGradle = $buildGradle -replace "version = '[^']*'", "version = '$Version'"
Set-Content "build.gradle" $buildGradle

# Commit version change
git add build.gradle
git commit -m "Bump version to $Version"

# Create git tag
Write-Host "Creating git tag v$Version..." -ForegroundColor Yellow
$tagMessage = @"
Release version $Version

Features:
- Advanced AI-powered content generation with multi-strategy support
- Enhanced texture generation with style, quality and variation controls
- User learning and preference adaptation
- Functional item behaviors (teleport, healing, fire, ice)
- Smart style-specific texture generation
- Persistent content system
- Smart item detection

Compatible with Minecraft 1.19.2 + Forge 43.2.0+
"@

git tag -a "v$Version" -m $tagMessage

# Push changes and tag
Write-Host "Pushing to remote..." -ForegroundColor Yellow
git push origin $branch
git push origin "v$Version"

Write-Host "✅ Release $Version created successfully!" -ForegroundColor Green
Write-Host "GitHub Actions will automatically build and create the release." -ForegroundColor Cyan

# Get repository URL for convenience
$remoteUrl = git config remote.origin.url
$repoPath = $remoteUrl -replace '.*github\.com[:/]([^/]+/[^/]+).*', '$1' -replace '\.git$', ''
Write-Host "Visit https://github.com/$repoPath/releases to see the release." -ForegroundColor Cyan
