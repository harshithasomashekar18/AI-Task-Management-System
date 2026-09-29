package taskmanagementsystem.controller;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import taskmanagementsystem.dto.*;
import taskmanagementsystem.model.Task;
import taskmanagementsystem.service.TaskService;
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
 private final TaskService tasks;
 public TaskController(TaskService tasks) { this.tasks=tasks; }
 @PostMapping(value="/user/{userId}",consumes="application/json") @ResponseStatus(HttpStatus.CREATED)
 public ApiResponse create(@PathVariable Long userId,@Valid @RequestBody TaskInput input) { return tasks.createTask(input.toTask(),userId); }
 @GetMapping("/user/{userId}") public List<Task> list(@PathVariable Long userId) { return tasks.getAllTasks(userId); }
 @GetMapping("/{id}") public ApiResponse get(@PathVariable Integer id) { return tasks.getTaskById(id); }
 @PutMapping(value="/{id}",consumes="application/json") public ApiResponse update(@PathVariable Integer id,@Valid @RequestBody TaskInput input) { return tasks.updateTask(input.toTask(),id); }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Integer id) { tasks.deleteTask(id); }
 @PatchMapping("/{id}/done") public ApiResponse done(@PathVariable Integer id) { return tasks.doneTask(id); }
 @PatchMapping("/{id}/pending") public ApiResponse pending(@PathVariable Integer id) { return tasks.pendingTask(id); }
}
