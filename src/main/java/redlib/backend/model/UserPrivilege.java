package redlib.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_privilege")
@Data
public class UserPrivilege {
    @Enumerated(EnumType.STRING)
    private UserType userType;  // root/supervisor/reader
    private Integer userId;
    private String modId;     // 模块标识（如books）
    private String priv;      // 权限项（如search）
    private Boolean suspended;
}
