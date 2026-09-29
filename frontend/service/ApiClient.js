import axios from "axios";

// Override with VITE_API_BASE_URL when the backend is hosted elsewhere.
export const AUTH_KEY = "task-management.basic-auth";
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8081/api/v1",
});
api.interceptors.request.use((config) => {
  const authorization = sessionStorage.getItem(AUTH_KEY);
  if (authorization && !config.url?.startsWith("/auth/")) config.headers.Authorization = authorization;
  return config;
});
export default api;
