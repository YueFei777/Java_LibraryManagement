package redlib.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReaderVO {
    private Integer readerId;
    private String username;
    private LocalDateTime createdAt;
    private String studentId;
    private List<RecordingsVO> recordings;
}
