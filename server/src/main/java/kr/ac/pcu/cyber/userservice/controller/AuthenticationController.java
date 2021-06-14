package kr.ac.pcu.cyber.userservice.controller;

import kr.ac.pcu.cyber.userservice.domain.dto.AuthResponseData;
import kr.ac.pcu.cyber.userservice.domain.dto.RegisterRequestData;
import kr.ac.pcu.cyber.userservice.domain.dto.SilentRefreshResponseData;
import kr.ac.pcu.cyber.userservice.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/auth", produces = "application/json; charset=utf-8")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/login/{userId}")
    public ResponseEntity<AuthResponseData> login(@PathVariable String userId) {
        return ResponseEntity.ok(authenticationService.login(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseData> register(@RequestBody RegisterRequestData registerRequestData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(registerRequestData));
    }

    @GetMapping("/silent-refresh")
    public ResponseEntity<SilentRefreshResponseData> silentRefresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authenticationService.silentRefresh(request, response));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.status(HttpStatus.OK).headers(authenticationService.clearAllCookies()).build();
    }
}
