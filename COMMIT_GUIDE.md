# How to Commit Changes to the AI Mod Generator Project

This document outlines the recommended process for committing changes to the AI Mod Generator project.

## Commit Message Format

```
<type>(<scope>): <short summary>

<detailed description>

<references/resolves/closes/etc>
```

### Types
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation only changes
- `style`: Changes that do not affect the meaning of the code (white-space, formatting, etc)
- `refactor`: A code change that neither fixes a bug nor adds a feature
- `perf`: A code change that improves performance
- `test`: Adding missing tests or correcting existing tests
- `build`: Changes that affect the build system or external dependencies
- `ci`: Changes to CI configuration files and scripts

### Scope
The scope should be the area of the codebase affected, such as:
- `texture`: Texture generation systems
- `command`: Command handling
- `ai`: AI service integrations
- `doc`: Documentation
- `build`: Build system
- `test`: Testing framework

### Examples

```
feat(texture): implement multi-strategy texture generation

Added new AdvancedTextureGenerator class with support for multiple generation strategies
and fallback mechanisms. This improves texture quality and reliability.

Resolves #42
```

```
fix(ai): improve base64 image extraction from responses

Fixed reliability issues when extracting base64-encoded images from AI responses
by adding better validation and error handling.

Fixes #56
```

## Pre-Commit Checklist

1. ✅ Run tests (`./run-tests.ps1 all`) and verify they pass
2. ✅ Update version numbers if necessary
3. ✅ Update CHANGELOG.md for feature additions or significant changes
4. ✅ Check documentation is up to date
5. ✅ Verify code formatting meets project standards

## Workflow for Significant Changes

1. Create a feature branch from `main`
2. Make and test your changes
3. Update documentation and CHANGELOG.md
4. Merge latest `main` and resolve conflicts
5. Run full test suite
6. Create Pull Request to `main`
