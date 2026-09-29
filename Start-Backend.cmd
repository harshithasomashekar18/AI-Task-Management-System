@echo off
cd /d "%~dp0"
echo Starting backend. MySQL must be running and task_management_system_db must exist.
powershell -NoProfile -Command "$env:DB_USERNAME = Read-Host 'MySQL username (press Enter for root)'; if ([string]::IsNullOrWhiteSpace($env:DB_USERNAME)) { $env:DB_USERNAME = 'root' }; $secret = Read-Host 'MySQL password' -AsSecureString; $env:DB_PASSWORD = (New-Object System.Net.NetworkCredential('', $secret)).Password; if ($env:OPENAI_BASE_URL -eq 'http://127.0.0.1:11434/v1') { Write-Host 'Using local Ollama model; no API key needed.' } else { ${aiKey} = Read-Host 'OpenAI API key (optional; press Enter for local rules)' -AsSecureString; if (${aiKey}.Length -gt 0) { $env:OPENAI_API_KEY = (New-Object System.Net.NetworkCredential('', ${aiKey})).Password } }; & '.\mvnw.cmd' 'clean' 'spring-boot:run'; exit $LASTEXITCODE"
pause
