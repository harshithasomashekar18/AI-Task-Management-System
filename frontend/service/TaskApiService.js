import api from "./ApiClient";

export const createTask = (task, userId) => api.post(`/tasks/user/${userId}`, task);
export const retrieveTaskById = (id) => api.get(`/tasks/${id}`);
export const retrieveAllTasks = (userId) => api.get(`/tasks/user/${userId}`);
export const updateTask = (task, id) => api.put(`/tasks/${id}`, task);
export const deleteTask = (id) => api.delete(`/tasks/${id}`);
export const markDone = (id) => api.patch(`/tasks/${id}/done`);
export const markPending = (id) => api.patch(`/tasks/${id}/pending`);
