# Run on Windows

Extract task-management-final.zip into a NEW folder, then open that folder in VS Code. Your old downloaded copy can stay unchanged. Stop its backend and frontend windows before starting this copy.

The frontend uses http://localhost:3000/ and the backend uses port 8081 by default, matching your working Windows setup. Your existing accounts and tasks stay in MySQL; the ZIP does not contain or replace the database.

## 1. Requirements

- JDK 17 with JAVA_HOME pointing to the JDK folder.
- Node.js and npm (your Node 24 installation works).
- MySQL Server running locally on port 3306.
- Internet access for the first Maven/npm dependency download.

Maven 3.9.6 is downloaded by the included wrapper. Your globally installed Maven 3.6.0 is not used by the launcher.

## 2. Create the database once

In a VS Code PowerShell terminal:

```powershell
mysql -u root -p
```

Type your MySQL password when prompted. At the `mysql>` prompt, run:

```sql
CREATE DATABASE IF NOT EXISTS task_management_system_db;
exit;
```

## 3. Start the backend

Open the extracted project folder (the one containing pom.xml) in File Explorer.
Double-click **Start-Backend.cmd**. Enter your MySQL username and password when prompted. You may also enter an optional OpenAI API key. Both secrets are hidden and passed only to the backend process; they are not written into the project.

The launcher runs Maven clean before startup to remove old compiled classes. This does not delete MySQL data.

Keep this window open. Wait for **Started TaskManagementSystemApplication**. First startup can take several minutes while Maven downloads dependencies. Hibernate creates the tables in the selected database.

If MySQL uses a different address/database, set DB_URL before launching from your terminal. The default is jdbc:mysql://localhost:3306/task_management_system_db.

## 4. Start the frontend

Stop the old Vite terminal first (Ctrl+C, then Y if prompted), so port 3000 is free.
Double-click **Start-Frontend.cmd** in the new project folder. It installs dependencies the first time and starts Vite. Keep this window open too.

Open **http://localhost:3000/** in Chrome. Use Create account, then Login, then Add Task.

## 5. Optional model suggestions

Without OPENAI_API_KEY, priority uses keyword rules and summary uses shortened task text. The UI labels these task priorities as rules. This is a local fallback, not a model response.

### Run a local AI model without an API key

1. Install [Ollama for Windows](https://ollama.com/download/windows), then open the Ollama app. It runs a local service in the background.
2. Open a new PowerShell terminal and run `ollama pull gemma3:1b`. This downloads the model once (about 815 MB). Keep the Ollama app running.
3. Stop the current backend window with Ctrl+C. In this project folder, double-click **Start-Backend-Local-AI.cmd**. Enter your MySQL username and password as before. This launcher uses backend port 8081 and the local model; it needs no OpenAI API key.
4. Keep the frontend running at http://localhost:3000/. Create a new task or edit an existing one. Its priority badge should say **AI** when the model replies successfully. A **rules** badge means the model request failed or returned an unusable answer; check the backend window for a warning and confirm Ollama is running.

The launcher checks that Ollama is running and the model is downloaded before starting the backend. Ollama's local OpenAI-compatible endpoint accepts a placeholder key (`ollama`); it is not a real credential. Model inference runs on your PC and may be slow on CPU. Existing tasks keep their previous priority until edited. The frontend already defaults to `http://localhost:8081/api/v1`; no `.env.local` is needed. If you choose a different backend port, set SERVER_PORT and the matching frontend VITE_API_BASE_URL in frontend/.env.local.

Local API reference: https://docs.ollama.com/api/openai-compatibility; model: https://ollama.com/library/gemma3:1b

To enable real model responses, enter your OpenAI API key at the hidden prompt in Start-Backend.cmd, or set OPENAI_API_KEY privately in the backend process environment before running .\mvnw.cmd spring-boot:run. Restart the backend after changing the key. New or edited tasks get a fresh priority; existing tasks keep their saved value until edited. OPENAI_MODEL defaults to gpt-4o-mini, which the official documentation lists for Chat Completions; select another model available to your account if needed. OPENAI_BASE_URL defaults to https://api.openai.com/v1. This repair uses Spring's HTTP client with the documented /chat/completions API, removing the unavailable Spring AI milestone dependency. The local Windows OpenAI requests returned HTTP 429, so the demonstrated priorities use rules. Real model output remains unverified; an available API credit balance and quota are required for the OpenAI option. API failures fall back to the local rules. Do not send your API key or database password in chat.

API reference: https://developers.openai.com/api/reference/resources/chat/subresources/completions/methods/create

## Verification performed

- Java compilation and Maven wrapper packaging passed on Linux with JDK 17 and Maven 3.9.6.
- Registration/login, validation, ownership enforcement, task CRUD/completion, and CORS tests passed with H2 and MySQL 8.0.44.
- Browser registration, login, task creation and completion/history passed against MySQL 8.0.44.
- Frontend production build passed on Node 24.
- Windows launcher scripts require verification on your Windows machine; they cannot be executed in the Linux test environment.
- CodeRabbit review was disabled for this task.

This is a local development setup. HTTP Basic credentials are held in sessionStorage until logout; deployment needs HTTPS and a separate production security/configuration review.
