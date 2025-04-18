package redlib.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import redlib.backend.model.PrivilegeDetail;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorVO {
    private Integer supervisorId;
    private String username;
    private LocalDateTime createdAt;

    private List<PrivilegeDetail> privileges;
}

