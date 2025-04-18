package redlib.backend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Root {
    private Integer rootId;
    private String username;
    private String password;
    private LocalDateTime createdAt;
}
