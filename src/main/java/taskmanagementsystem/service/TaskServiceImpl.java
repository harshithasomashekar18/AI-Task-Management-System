package taskmanagementsystem.service;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import taskmanagementsystem.ai.TaskAiService;
import taskmanagementsystem.dto.ApiResponse;
import taskmanagementsystem.model.*;
import taskmanagementsystem.repository.*;
@Service
@Transactional
public class TaskServiceImpl implements TaskService {
 private final TaskRepository tasks; private final UserRepository users; private final TaskAiService ai;
 public TaskServiceImpl(TaskRepository tasks, UserRepository users, TaskAiService ai) { this.tasks=tasks; this.users=users; this.ai=ai; }
 private User currentUser() { return users.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED)); }
 private User owner(Long id) { User u=currentUser(); if(!u.getId().equals(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN); return u; }
 private Task ownedTask(Integer id) {
  Task t=tasks.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found"));
  owner(t.getUser().getId()); return t;
 }
 private void enrich(Task task) {
  String text=task.getTask()+(task.getDetails()==null ? "" : "\n"+task.getDetails());
  TaskAiService.PrioritySuggestion suggestion=ai.suggestPriorityWithSource(text);
  task.setPriority(suggestion.priority()); task.setPrioritySource(suggestion.source()); task.setSummary(ai.summarize(text));
 }
 public ApiResponse createTask(Task input, Long userId) {
  User u=owner(userId); Task t=new Task(); t.setTask(input.getTask()); t.setDetails(input.getDetails()); t.setUser(u); t.setCompleted(false); t.setTaskCreatedAt(Instant.now().toString()); enrich(t);
  return new ApiResponse("Task saved",tasks.save(t));
 }
 public ApiResponse getTaskById(Integer id) { return new ApiResponse("Task found",ownedTask(id)); }
 public List<Task> getAllTasks(Long userId) { owner(userId); return tasks.findAllByUserId(userId); }
 public ApiResponse updateTask(Task input,Integer id) { Task t=ownedTask(id); t.setTask(input.getTask()); t.setDetails(input.getDetails()); enrich(t); return new ApiResponse("Task updated",tasks.save(t)); }
 public void deleteTask(Integer id) { tasks.delete(ownedTask(id)); }
 public ApiResponse doneTask(Integer id) { Task t=ownedTask(id);t.setCompleted(true);return new ApiResponse("Task completed",tasks.save(t)); }
 public ApiResponse pendingTask(Integer id) { Task t=ownedTask(id);t.setCompleted(false);return new ApiResponse("Task pending",tasks.save(t)); }
}
