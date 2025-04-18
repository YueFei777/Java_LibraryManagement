package redlib.backend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Recordings {
    private Integer id;
    private Integer readerId;
    private String username;
    private String isbn;
    private LocalDateTime borrowedTime;
    private LocalDateTime returnedTime;
}
