# Test Framework Updates - June 12, 2025

## Summary of changes
This update improves the testing framework for the AI Mod Generator, making it easier to run specific tests and integrate with the build system.

## Main improvements

### 1. Custom Gradle tasks
- Added `runSmokeTests` - Runs quick verification tests
- Added `runEnhancedTests` - Runs tests for enhanced feature set
- Added `runComprehensiveTests` - Runs all meaningful tests

### 2. PowerShell test runner script (`run-tests.ps1`)
- Simple interface for running tests with a single command
- Four test modes: smoke, enhanced, comprehensive, all
- Proper integration with Gradle tasks

### 3. Documentation updates
- Updated README.md with new testing instructions
- Added testing improvements to CHANGELOG.md
- Updated RELEASE_NOTES.md with testing framework details
- Created commit message templates and guides

### 4. Version & build fixes
- Fixed build.gradle manifest Specification-Version (now "2" to match v2.0.0)
- Added more detailed release tag information
- Added VS Code tasks for test execution

## How to use the new test framework

```powershell
# Quick verification of critical features
.\run-tests.ps1 smoke

# Test only enhanced features
.\run-tests.ps1 enhanced

# Run all meaningful tests
.\run-tests.ps1 comprehensive

# Run everything
.\run-tests.ps1 all
```

## Next steps
- Fix remaining test failures that depend on Minecraft environment
- Add more comprehensive tests for integrated features
- Improve test reporting
