# Frontend

The entry point is main.jsx. Components, API services, CSS, and assets are included in their named folders. Run npm.cmd ci and npm.cmd run dev from this folder, or use Start-Frontend.cmd at the project root.

The backend defaults to http://localhost:8081/api/v1. VITE_API_BASE_URL may override it through .env.local (restart Vite afterward). The included backend implements the auth and task routes in service/. A task detail/update response contains {message, object}; listing returns an array; login returns {id, username, email, role}.

Read ../SETUP-WINDOWS.md for full startup instructions. The original artwork was absent; the task icon is a replacement. No fake login or task storage is used.
