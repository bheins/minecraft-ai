#!/usr/bin/env pwsh

param (
    [Parameter(Position=0)]
    [ValidateSet('smoke', 'enhanced', 'comprehensive', 'all')]
    [string]$TestType = 'smoke'
)

Write-Host "==== AI Mod Generator Test Runner ====" -ForegroundColor Cyan

switch ($TestType) {
    'smoke' {
        Write-Host "Running Smoke Tests..." -ForegroundColor Yellow
        ./gradlew runSmokeTests
    }
    'enhanced' {
        Write-Host "Running Enhanced Feature Tests..." -ForegroundColor Yellow
        ./gradlew runEnhancedTests
    }
    'comprehensive' {
        Write-Host "Running Comprehensive Tests..." -ForegroundColor Yellow
        ./gradlew runComprehensiveTests
    }
    'all' {
        Write-Host "Running All Test Suites..." -ForegroundColor Yellow
        ./gradlew runSmokeTests
        ./gradlew runEnhancedTests
        ./gradlew runComprehensiveTests
    }
}

Write-Host "==== Test Runner Complete ====" -ForegroundColor Cyan
