package taskmanagementsystem.controller;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class ApiErrors {
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<?> validation(MethodArgumentNotValidException e) { return ResponseEntity.badRequest().body(Map.of("message","Please check the submitted fields")); }
 @ExceptionHandler(DataIntegrityViolationException.class)
 public ResponseEntity<?> conflict(DataIntegrityViolationException e) { return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","The submitted data conflicts with an existing record")); }
}
