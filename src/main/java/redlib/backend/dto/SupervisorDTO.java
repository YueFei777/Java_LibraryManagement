package redlib.backend.dto;

import lombok.Data;
import redlib.backend.model.PrivilegeDetail;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SupervisorDTO {
    private Integer supervisorId;
    private String username;
    private LocalDateTime createdAt;
    private String password;

    private List<PrivilegeDetail> privileges;
}
