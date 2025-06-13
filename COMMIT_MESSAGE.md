# Enhanced Testing Framework and Documentation Updates

## Changes
- Added custom Gradle tasks for targeted test execution
  - `runSmokeTests`: Fast verification of core functionality
  - `runEnhancedTests`: Tests for enhanced feature set
  - `runComprehensiveTests`: Complete test suite execution
  
- Created PowerShell test runner script (`run-tests.ps1`)
  - Simple command-line interface for test execution
  - Support for smoke, enhanced, comprehensive, and all tests
  - Better error handling and reporting
  - Fixed script issues for reliable execution

- Updated documentation:
  - Enhanced README.md with new testing instructions
  - Updated CHANGELOG.md with testing improvements
  - Updated RELEASE_NOTES.md with test framework details

- Fixed build.gradle manifest to use version 2 instead of 1
  - Updated Specification-Version to match current release

- Updated release script tag message
  - Added enhanced feature descriptions to tag message template

- Updated VS Code tasks configuration
  - Added test-specific tasks to tasks.json

## Related Items
- Resolves #xx: Implement better testing framework
- Improves test execution and developer workflow efficiency
- Ensures proper version details in build artifacts
