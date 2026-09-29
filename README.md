# AI-assisted Task Management System

A local task manager with a React/Vite frontend, Java 17/Spring Boot backend, and MySQL database.

## Start on Windows

Follow [SETUP-WINDOWS.md](SETUP-WINDOWS.md). Create the MySQL database, then use Start-Backend.cmd and Start-Frontend.cmd. The Maven wrapper downloads Maven 3.9.6. Open http://localhost:3000/ in a browser. The backend and frontend API client default to port 8081. The backend launcher clears old compiled files before starting.

## Features

- Register, log in, and log out.
- Create, view, edit, delete, complete, and reopen your own tasks.
- Task history and visible priority badges. Pending tasks are ordered HIGH, MEDIUM, LOW. Each new priority records whether it came from the model or local rules.
- Optional OpenAI chat-completion suggestions; keyword and text-truncation fallbacks when no key is configured or requests fail.

## Layout

- `src/main/java/taskmanagementsystem`: controllers, DTOs, services, security, entities, and repositories.
- `src/main/resources/application.properties`: database and optional AI configuration using environment variables.
- `src/test`: integration tests and isolated test database configuration.
- `frontend`: React app and API clients.

## API

All routes use the `/api/v1` prefix. Registration and login are public JSON POST requests to `/auth/register` and `/auth/login`. Task requests require HTTP Basic authentication. Responses never expose password hashes. Task ownership is enforced on the server.

| Method | Path | Operation |
| --- | --- | --- |
| POST | /tasks/user/{userId} | Create task with `{task, details}` |
| GET | /tasks/user/{userId} | List own tasks |
| GET | /tasks/{id} | Read own task |
| PUT | /tasks/{id} | Update own task text/details |
| DELETE | /tasks/{id} | Delete own task |
| PATCH | /tasks/{id}/done | Mark complete |
| PATCH | /tasks/{id}/pending | Reopen task |

## Checks

`./mvnw test` (Windows: `.\mvnw.cmd test`) runs integration tests against an isolated H2 database. `npm run build` from frontend validates the frontend bundle. Never point tests at a database containing user data: the test configuration creates and drops its tables.

This is a development setup; see SETUP-WINDOWS.md for limitations and verification details.
