package redlib.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecordingsDTO {
    private Integer readerId;
    private String username;
    private String book_title;
    private String isbn;
    private LocalDateTime borrowedTime;
    private LocalDateTime returnedTime;
    private Boolean overdue;
}
