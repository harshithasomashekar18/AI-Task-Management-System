package taskmanagementsystem.security;
import taskmanagementsystem.model.User;
public record AuthResponse(Long id, String username, String email, String role) {
 public static AuthResponse from(User user) { return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole()); }
}
