@echo off
echo ========================================================
echo  Starting Smart Assessment Platform - React Frontend
echo ========================================================
cd frontend
if not exist node_modules (
    echo Installing npm dependencies...
    call npm install
)
echo Starting React development server on http://localhost:3000 ...
call npm start
pause
