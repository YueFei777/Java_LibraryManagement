package redlib.backend.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import redlib.backend.annotation.BackendModule;
import redlib.backend.annotation.NeedNoPrivilege;
import redlib.backend.annotation.Privilege;
import redlib.backend.annotation.Reader;
import redlib.backend.dao.UserPrivilegeMapper;
import redlib.backend.model.Token;
import redlib.backend.utils.ThreadContextHolder;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import static redlib.backend.model.UserType.reader;
import static redlib.backend.model.UserType.root;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class TokenCheckAspect {
    private final UserPrivilegeMapper userPrivilegeMapper;

    /*
            * 合并权限校验和Token检查逻辑
     */
    @Around("execution(* redlib.backend.controller..*Controller.*(..))")
    public Object unifiedCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object target = joinPoint.getTarget();

        // 1. 检查是否需要权限校验
        if (method.isAnnotationPresent(NeedNoPrivilege.class)) {
            return joinPoint.proceed(); // 免权限接口直接放行
        }

        // 2. 获取Token并验证存在性
        Token token = ThreadContextHolder.getToken();
        Assert.notNull(token, "Please login first");

        // 3. Root用户直接放行
        if (root.equals(token.getUserType())) {
            return joinPoint.proceed();
        }

        if (reader.equals(token.getUserType())){
            boolean hasPermission = method.isAnnotationPresent(Reader.class)
                    || target.getClass().isAnnotationPresent(Reader.class);
            if(hasPermission){
                return joinPoint.proceed();
            }
        }

        // 4. 检查Controller类是否有@BackendModule注解（可选）
        if (!target.getClass().isAnnotationPresent(BackendModule.class)) {
            throw new RuntimeException("Controller @BackendModule annotation not found");
        }

        // 5. 处理@Privilege注解的权限校验
        if (method.isAnnotationPresent(Privilege.class)) {
            Privilege privilege = method.getAnnotation(Privilege.class);
            String[] requiredPrivs = privilege.value();

            // 空权限注解表示只需登录
            if (requiredPrivs.length == 0) {
                return joinPoint.proceed();
            }


            Set<String> userPrivs = token.getPrivSet();
            String annotation = privilege.value()[0];
            // 检查权限
            boolean hasPermission = Arrays.stream(requiredPrivs)
                    .anyMatch(userPrivs::contains);

            if (!hasPermission) {
                throw new RuntimeException("Unauthorized");
            }

            String[] privParts = annotation.trim().split("\\.");
            if(userPrivilegeMapper.hasSuspend(
                    token.getUserType(), token.getUserId(),
                    privParts[0], privParts[1])) {
                throw new RuntimeException("Suspended");
            }
        }

        // 6. 放行请求
        return joinPoint.proceed();
    }
}