Write-Host "========================================================" -ForegroundColor Cyan
Write-Host " Starting Smart Assessment Platform - Spring Boot Backend" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

Set-Location "$PSScriptRoot\backend"

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "`n[!] 'mvn' (Apache Maven) was not detected in your PATH." -ForegroundColor Yellow
    Write-Host "To install Maven automatically with Chocolatey, run:" -ForegroundColor Yellow
    Write-Host "    choco install maven -y" -ForegroundColor White
    Write-Host "Or if you use IntelliJ IDEA / Eclipse / VS Code:" -ForegroundColor Yellow
    Write-Host "Simply open the 'backend' folder and hit 'Run' on SmartAssessmentPlatformApplication.java" -ForegroundColor White
    Read-Host -Prompt "Press Enter to exit"
    exit 1
}

Write-Host "`nRunning Spring Boot Application on http://localhost:8080 ..." -ForegroundColor Green
mvn spring-boot:run
