package taskmanagementsystem.controller;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import taskmanagementsystem.dto.*;
import taskmanagementsystem.security.AuthResponse;
import taskmanagementsystem.service.AuthService;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
 private final AuthService auth;
 public AuthController(AuthService auth) { this.auth=auth; }
 @PostMapping(value="/register", consumes="application/json") @ResponseStatus(HttpStatus.CREATED)
 public AuthResponse register(@Valid @RequestBody Register input) { return auth.register(input); }
 @PostMapping(value="/login", consumes="application/json")
 public AuthResponse login(@Valid @RequestBody Login input) { return auth.login(input).map(AuthResponse::from).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials")); }
}
