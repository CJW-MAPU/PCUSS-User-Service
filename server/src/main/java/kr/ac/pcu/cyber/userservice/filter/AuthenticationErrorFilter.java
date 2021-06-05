package kr.ac.pcu.cyber.userservice.filter;

import kr.ac.pcu.cyber.userservice.errors.EmptyCookieException;
import kr.ac.pcu.cyber.userservice.errors.InvalidTokenException;
import kr.ac.pcu.cyber.userservice.errors.TokenExpiredException;
import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationErrorFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        } catch (EmptyCookieException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        } catch (TokenExpiredException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }
}