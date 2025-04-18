package redlib.backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import redlib.backend.model.Token;
import redlib.backend.service.TokenService;
import redlib.backend.utils.ThreadContextHolder;
import redlib.backend.utils.TokenAuthentication;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import static redlib.backend.model.UserType.root;

@Slf4j
public class TokenFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    public TokenFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // 1. 跳过无需校验的路径
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 提取 Token
        String accessToken = extractToken(request);
        if (accessToken == null || accessToken.isEmpty()) {
            sendError(response, 401, "token was null or empty");
            return;
        }

        // 3. 验证 Token
        Token token = tokenService.getToken(accessToken);
        if (token == null) {
            sendError(response, 401, "token expired");
            return;
        }

        // 4. 更新 Token 活跃时间（可选）
        token.setLastAction(new Date());
        log.info("Token updated: {}", token);

        // 5. 将 Token 绑定到 Spring Security 上下文
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        token.getUserName(),
                        null,
                        // 将权限转换为 GrantedAuthority 对象
                        token.getPrivSet().stream()
                                .map(priv -> new SimpleGrantedAuthority(priv))
                                .collect(Collectors.toList())
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 6.将 Token 存入线程上下文
        ThreadContextHolder.setToken(token);

        // 7. 继续执行后续过滤器链
        try {
            filterChain.doFilter(request, response);
        } finally {
            ThreadContextHolder.clear(); // 确保清除上下文
        }
    }

    private String extractToken(HttpServletRequest request) {
        // 1. 从 Cookie 中提取 accessToken
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    log.info("Token accessed from cookie: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }

        // 2. 如果 Cookie 中没有，再尝试从 Header 提取
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.info("Token accessed from header: {}", authHeader.substring(7));
            return authHeader.substring(7);
        }

        // 3. 未找到 Token
        log.warn("@extractToken: Token was nor found.");
        return null;
    }

    private void sendError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"code\": %d, \"message\": \"%s\"}", code, message));
    }

    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.contains("/swagger-ui")
                || uri.contains("/v3/api-docs")
                || uri.equals("/api/authentication/login")
                || uri.equals("/error");
    }
}