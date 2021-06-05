package kr.ac.pcu.cyber.userservice.controller;

import kr.ac.pcu.cyber.userservice.domain.dto.ModifyRequestData;
import kr.ac.pcu.cyber.userservice.domain.entity.User;
import kr.ac.pcu.cyber.userservice.security.CustomUserAuthentication;
import kr.ac.pcu.cyber.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users", produces = "application/json; charset=utf-8")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
