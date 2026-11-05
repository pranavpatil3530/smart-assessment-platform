@echo off
echo ========================================================
echo  Starting Smart Assessment Platform - Spring Boot Backend
echo ========================================================
cd backend
echo Checking Maven installation...
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [!] 'mvn' was not detected in your PATH.
    echo If Maven is not installed, install it using:
    echo     choco install maven
    echo or download from: https://maven.apache.org/download.cgi
    echo.
    echo If you are using IntelliJ IDEA or Eclipse:
    echo Simply open the 'backend' folder and click Run on 'SmartAssessmentPlatformApplication.java'.
    echo.
    pause
    exit /b 1
)

echo Running Spring Boot Application on http://localhost:8080 ...
mvn spring-boot:run
pause
