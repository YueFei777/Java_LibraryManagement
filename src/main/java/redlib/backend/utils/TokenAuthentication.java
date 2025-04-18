package redlib.backend.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import redlib.backend.model.Token;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class TokenAuthentication implements Authentication {
    private final Token token;
    private boolean authenticated;

    public TokenAuthentication(Token token) {
        this.token = token;
        this.authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return token.getPrivSet().stream()
                .map(priv -> (GrantedAuthority) () -> priv)
                .collect(Collectors.toList());
    }

    @Override
    public Object getCredentials() {
        return token.getAccessToken();
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return token.getUserId();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return token.getUserName();
    }
}