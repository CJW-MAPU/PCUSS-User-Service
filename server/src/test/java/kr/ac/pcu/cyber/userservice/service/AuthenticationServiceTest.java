package kr.ac.pcu.cyber.userservice.service;

import kr.ac.pcu.cyber.userservice.domain.dto.AuthResponseData;
import kr.ac.pcu.cyber.userservice.domain.dto.RegisterRequestData;
import kr.ac.pcu.cyber.userservice.domain.entity.Role;
import kr.ac.pcu.cyber.userservice.domain.entity.RoleType;
import kr.ac.pcu.cyber.userservice.domain.entity.User;
import kr.ac.pcu.cyber.userservice.domain.repository.RoleRepository;
import kr.ac.pcu.cyber.userservice.domain.repository.UserRepository;
import kr.ac.pcu.cyber.userservice.errors.EmptyCookieException;
import kr.ac.pcu.cyber.userservice.errors.UserNotFoundException;
import kr.ac.pcu.cyber.userservice.utils.CookieUtil;
import kr.ac.pcu.cyber.userservice.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthenticationServiceTest {

    private static final Long USER_ID = 1004L;
    private static final String NICKNAME = "james";
    private static final String SECRET = "12345678901234567890123456789012";
    private static final String VALID_UUID = "2f48f241-9d64-4d16-bf56-70b9d4e0e79a";
    private static final String INVALID_UUID = "3d87cc41-i5d6-7da0-5bbf-0e7b9d4e9a70";
    private static final String EMAIL = "james123@gmail.com";
    private static final String PROFILE_IMAGE = "https://cdn.kakao.com/images/james";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOiIyZjQ4ZjI0MS05ZDY0LTRkMTYtYmY1Ni03MGI5ZDRlMGU3OWEifQ." +
            "diJ35TNZtRqYIkkiUZX0JC0IQ_Yia8c5p8FDd_FMgYo";

    private AuthenticationService authenticationService;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final CookieUtil cookieUtil = mock(CookieUtil.class);

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        JwtUtil jwtUtil = new JwtUtil(SECRET);

        authenticationService = new AuthenticationService(modelMapper, userRepository, roleRepository, jwtUtil, cookieUtil);

        User user = User.builder()
                .id(USER_ID)
                .nickname(NICKNAME)
                .email(EMAIL)
                .profileImage(PROFILE_IMAGE)
                .userId(VALID_UUID)
                .build();

        Cookie accessCookie = new Cookie("access_token", VALID_TOKEN);
        Cookie refreshCookie = new Cookie("refresh_token", VALID_TOKEN);

        List<Role> roles = new ArrayList<>();
        roles.add(new Role(RoleType.USER));
        roles.add(new Role(RoleType.ADMIN));

        Role role = new Role(RoleType.USER);

        given(roleRepository.save(any(Role.class))).willReturn(role);

        given(userRepository.findByUserId(VALID_UUID)).willReturn(Optional.of(user));
        given(userRepository.findByUserId(INVALID_UUID)).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(user);

        given(cookieUtil.parseTokenFromCookies(any(), any())).willReturn(VALID_TOKEN);
        given(cookieUtil.createCookieWithToken(any(), any())).willReturn(accessCookie);

        given(roleRepository.findAllByUserId(VALID_UUID)).willReturn(roles);
        given(userRepository.existsByUserId(VALID_UUID)).willReturn(true);
        given(roleRepository.findAllByUserId(INVALID_UUID)).willThrow(UserNotFoundException.class);
        given(userRepository.existsByUserId(INVALID_UUID)).willReturn(false);
    }

    @Test
    @DisplayName("login 성공")
    void login_valid() {
        AuthResponseData responseData = authenticationService.login(VALID_UUID);

        assertEquals(NICKNAME, responseData.getNickname());
        assertNotEquals("", responseData.getAccessToken());
    }

    @Test
    @DisplayName("login 실패 - 존재하지 않는 uuid")
    void login_invalid_uuid() {
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> authenticationService.login(INVALID_UUID)
        );
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("register 성공")
    void register_valid() {
        RegisterRequestData registerRequestData = RegisterRequestData.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .profileImage(PROFILE_IMAGE)
                .build();

        AuthResponseData responseData = authenticationService.register(registerRequestData);

        assertNotNull(responseData.getId());
        assertEquals(NICKNAME, responseData.getNickname());
        assertNotEquals( "", responseData.getAccessToken());
    }

    @Test
    @DisplayName("parseUserIdFromCookies - 성공")
    void parseUserIdFromCookies_valid() {
        Cookie[] cookies = new Cookie[1];

        String userId = authenticationService.parseUserIdFromCookies(cookies);

        assertEquals(VALID_UUID, userId);
    }

    @Test
    @DisplayName("silentRefresh - 실패")
    void silentRefresh_invalid() {

        MockHttpServletRequest request = new MockHttpServletRequest();

        EmptyCookieException exception = assertThrows(
                EmptyCookieException.class,
                () -> authenticationService.silentRefresh(request)
        );

        assertNotNull(exception);
    }

    @Test
    @DisplayName("쿠키 삭제 - 성공")
    void clearAllCookies() {
        HttpHeaders httpHeaders = authenticationService.clearAllCookies();

        assertNotNull(httpHeaders);
    }

    @Test
    @DisplayName("getRoles - 성공")
    void getRoles_valid() {
        // when
        List<Role> roles = authenticationService.getRoles(VALID_UUID);

        // then
        assertNotNull(roles);
    }

    @Test
    @DisplayName("getRole - 실패 - 존재하지 않는 사용자")
    void getRoles_invalid_not_found_user() {
        // when
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> authenticationService.getRoles(INVALID_UUID)
        );

        // then
        assertNotNull(exception.getMessage());
    }
}