package redlib.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecordingsVO {
    String isbn;
    LocalDateTime borrowedTime;
    LocalDateTime returnedTime;
}
