package taskmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    @NotEmpty(message = "Invalid: task cannot be empty")
    private String task;
    @Column(length = 4000)
    private String details;
    private Boolean completed;
    private String taskCreatedAt;
    @ManyToOne
    @JsonIgnore
    private User user;

    private String priority; // HIGH/MEDIUM/LOW
    private String prioritySource; // AI or RULES
    @Column(length = 500)
    private String summary;  // AI-generated short summary
}
