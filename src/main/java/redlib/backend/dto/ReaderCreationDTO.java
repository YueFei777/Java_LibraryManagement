package redlib.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReaderCreationDTO {
    @NotBlank(message = "用户名不能为空")
    String Username;
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "密码必须至少8位，包含字母和数字")
    String rawPassword;
    String student_id;
    String contact;
}
