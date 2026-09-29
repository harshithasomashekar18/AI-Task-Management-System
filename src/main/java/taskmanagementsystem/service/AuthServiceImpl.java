package taskmanagementsystem.service;
import java.util.Optional;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import taskmanagementsystem.dto.*;
import taskmanagementsystem.model.User;
import taskmanagementsystem.repository.UserRepository;
import taskmanagementsystem.security.AuthResponse;
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
 private final UserRepository users; private final PasswordEncoder encoder;
 public AuthServiceImpl(UserRepository users, PasswordEncoder encoder) { this.users=users; this.encoder=encoder; }
 public AuthResponse register(Register input) {
  if(input.password().getBytes(StandardCharsets.UTF_8).length > 72) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password must be at most 72 UTF-8 bytes");
  if(users.existsByUsername(input.username()) || users.existsByEmail(input.email())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Username or email already exists");
  User u = new User(); u.setUsername(input.username()); u.setEmail(input.email()); u.setPassword(encoder.encode(input.password())); u.setRole("USER");
  return AuthResponse.from(users.save(u));
 }
 public Optional<User> login(Login input) {
  if(input.password().getBytes(StandardCharsets.UTF_8).length > 72) return Optional.empty();
  return users.findByUsername(input.username()).filter(u -> encoder.matches(input.password(),u.getPassword()));
 }
}
