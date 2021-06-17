package kr.ac.pcu.cyber.userservice.controller;

import kr.ac.pcu.cyber.userservice.domain.dto.ModifyRequestData;
import kr.ac.pcu.cyber.userservice.domain.entity.Role;
import kr.ac.pcu.cyber.userservice.domain.entity.User;
import kr.ac.pcu.cyber.userservice.security.CustomUserAuthentication;
import kr.ac.pcu.cyber.userservice.service.AuthenticationService;
import kr.ac.pcu.cyber.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = "application/json; charset=utf-8")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService,
                          AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<User> modify(@PathVariable("id") Long id,
                                       @RequestBody ModifyRequestData modifyRequestData,
                                       CustomUserAuthentication authentication) {

        String userId = authentication.getPrincipal();

        return ResponseEntity.ok(
                userService.modifyUser(id, modifyRequestData, userId)
        );
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<Role>> getRoles(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(authenticationService.getRoles(userId));
    }
}
