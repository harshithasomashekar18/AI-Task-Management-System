@echo off
cd /d "%~dp0"
where ollama >nul 2>&1
if errorlevel 1 (
  echo Ollama is not installed. Install it from https://ollama.com/download/windows
  pause
  exit /b 1
)
curl.exe --silent --fail http://127.0.0.1:11434/api/tags >nul
if errorlevel 1 (
  echo Ollama is not running. Open the Ollama app and try again.
  pause
  exit /b 1
)
ollama list | findstr /C:"gemma3:1b" >nul
if errorlevel 1 (
  echo The local AI model is missing. Run: ollama pull gemma3:1b
  pause
  exit /b 1
)
set "SERVER_PORT=8081"
set "OPENAI_BASE_URL=http://127.0.0.1:11434/v1"
set "OPENAI_MODEL=gemma3:1b"
set "OPENAI_API_KEY=ollama"
call "%~dp0Start-Backend.cmd"
