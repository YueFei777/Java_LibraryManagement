package redlib.backend.model;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Supervisor {
    private Integer supervisorId;
    private String username;
    private String password;
    private LocalDateTime createdAt;
}
