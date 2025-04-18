package redlib.backend.model;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ApiResponse {
    private Integer code;
    private boolean success;
    private String message;
    private Object data;

    public static ApiResponse success(String msg) {
        return new ApiResponse(200, true, msg, null);
    }
}