@echo off
cd /d "%~dp0frontend"
if not exist node_modules (
    call npm.cmd ci
    if errorlevel 1 goto finished
)
call npm.cmd run dev
:finished
pause
