@echo off
echo ========================================================
echo  Launching Smart Assessment Platform (Backend & Frontend)
echo ========================================================

start "Smart Assessment - Backend (Spring Boot :8080)" cmd /k "run-backend.bat"
start "Smart Assessment - Frontend (React :3000)" cmd /k "run-frontend.bat"

echo.
echo Both startup scripts have been launched in separate terminal windows.
echo - Backend will be available at: http://localhost:8080
echo - Frontend will be available at: http://localhost:3000
echo.
pause
