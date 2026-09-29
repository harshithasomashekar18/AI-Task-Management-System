import api, { AUTH_KEY } from "./ApiClient";

const USER_KEY = "task-management.user";
export const registerApi = (user) => api.post("/auth/register", user);
export const loginApi = (username, password) =>
  api.post("/auth/login", { username, password });
export const storeBasicAuth = (authorization) =>
  sessionStorage.setItem(AUTH_KEY, authorization);
export const saveLoggedUser = (id, username, role) =>
  sessionStorage.setItem(USER_KEY, JSON.stringify({ id, username, role }));
function currentUser() {
  try {
    return JSON.parse(sessionStorage.getItem(USER_KEY) || "null");
  } catch {
    return null;
  }
}
export const getLoggedInUserId = () => currentUser()?.id ?? null;
export const isUserLoggedIn = () =>
  getLoggedInUserId() !== null && Boolean(sessionStorage.getItem(AUTH_KEY));
export function logout() {
  sessionStorage.removeItem(AUTH_KEY);
  sessionStorage.removeItem(USER_KEY);
}
