Write-Host "========================================================" -ForegroundColor Cyan
Write-Host " Starting Smart Assessment Platform - React Frontend" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

Set-Location "$PSScriptRoot\frontend"

if (-not (Test-Path "node_modules")) {
    Write-Host "Installing npm dependencies (first time setup)..." -ForegroundColor Yellow
    npm install
}

Write-Host "`nLaunching React Application on http://localhost:3000 ..." -ForegroundColor Green
npm start
