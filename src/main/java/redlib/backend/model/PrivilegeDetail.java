package redlib.backend.model;

import lombok.Data;

@Data
public class PrivilegeDetail {
    private String modId;      // 作用域（如 "book"）
    private String priv;       // 权限项（如 "search"）
    private Boolean suspended; // 是否被暂停
}
