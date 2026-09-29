package taskmanagementsystem.dto;
import jakarta.validation.constraints.*;
public record Register(@NotBlank @Pattern(regexp="[A-Za-z0-9_.-]{3,50}") String username,
 @NotBlank @Email @Size(max=254) String email, @NotBlank @Size(min=6,max=72) String password) {}
