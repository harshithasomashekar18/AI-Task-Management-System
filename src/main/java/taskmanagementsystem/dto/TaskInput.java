package taskmanagementsystem.dto;
import jakarta.validation.constraints.*;
import taskmanagementsystem.model.Task;
public record TaskInput(@NotBlank @Size(max=255) String task, @Size(max=4000) String details) {
 public Task toTask() { Task value = new Task(); value.setTask(task); value.setDetails(details); return value; }
}
