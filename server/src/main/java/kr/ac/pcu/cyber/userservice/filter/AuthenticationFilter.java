package kr.ac.pcu.cyber.userservice.filter;

import kr.ac.pcu.cyber.userservice.domain.entity.Role;
import kr.ac.pcu.cyber.userservice.security.CustomUserAuthentication;
import kr.ac.pcu.cyber.userservice.service.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AuthenticationFilter extends BasicAuthenticationFilter {


    private final AuthenticationService authenticationService;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        if(request.getCookies() != null) {
            String userId = authenticationService.parseUserIdFromCookies(request.getCookies());
            List<Role> roles = authenticationService.getRoles(userId);

            Authentication customUserAuthentication = new CustomUserAuthentication(
                    userId,
                    roles
            );

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(customUserAuthentication);
        }

        chain.doFilter(request, response);
    }
}
