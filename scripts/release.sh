#!/bin/bash

# Release script for AI Mod Generator
# Usage: ./scripts/release.sh <version>
# Example: ./scripts/release.sh 1.0.1

set -e

if [ -z "$1" ]; then
    echo "Usage: $0 <version>"
    echo "Example: $0 1.0.1"
    exit 1
fi

VERSION=$1
echo "Creating release for version $VERSION"

# Validate version format
if ! [[ $VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "Error: Version must be in format X.Y.Z (e.g., 1.0.1)"
    exit 1
fi

# Check if we're on main/master branch
BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [[ "$BRANCH" != "main" && "$BRANCH" != "master" ]]; then
    echo "Warning: Not on main/master branch (currently on $BRANCH)"
    read -p "Continue anyway? (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Check for uncommitted changes
if ! git diff-index --quiet HEAD --; then
    echo "Error: You have uncommitted changes. Please commit or stash them first."
    exit 1
fi

# Update version in build.gradle
echo "Updating version in build.gradle..."
sed -i "s/version = '[^']*'/version = '$VERSION'/" build.gradle

# Commit version change
git add build.gradle
git commit -m "Bump version to $VERSION"

# Create git tag
echo "Creating git tag v$VERSION..."
git tag -a "v$VERSION" -m "Release version $VERSION

Features:
- AI-powered content generation
- Functional item behaviors (teleport, healing, fire, ice)
- Dynamic texture generation
- Persistent content system
- Smart item detection

Compatible with Minecraft 1.19.2 + Forge 43.2.0+"

# Push changes and tag
echo "Pushing to remote..."
git push origin $BRANCH
git push origin "v$VERSION"

echo "✅ Release $VERSION created successfully!"
echo "GitHub Actions will automatically build and create the release."
echo "Visit https://github.com/$(git config remote.origin.url | sed 's/.*github.com[:/]\([^/]*\/[^/]*\).*/\1/' | sed 's/\.git$//')/releases to see the release."
