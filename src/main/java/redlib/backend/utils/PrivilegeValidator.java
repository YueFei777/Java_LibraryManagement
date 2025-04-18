package redlib.backend.utils;

import redlib.backend.model.Token;

import static redlib.backend.model.UserType.root;

public class PrivilegeValidator {
    public static void checkPermission(String requiredPrivilege) {
        Token token = ThreadContextHolder.getToken();
        if (token == null) {
            throw new RuntimeException("未登录");
        }

        // 如果是 Root 用户，直接放行
        if (root.equals(token.getUserType())) {
            return;
        }

        // 普通用户检查权限
        if (!token.getPrivSet().contains(requiredPrivilege)) {
            throw new RuntimeException("权限不足");
        }
    }
}
