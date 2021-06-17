package kr.ac.pcu.cyber.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.pcu.cyber.userservice.domain.dto.ModifyRequestData;
import kr.ac.pcu.cyber.userservice.domain.entity.Role;
import kr.ac.pcu.cyber.userservice.domain.entity.RoleType;
import kr.ac.pcu.cyber.userservice.domain.entity.User;
import kr.ac.pcu.cyber.userservice.errors.UserNotFoundException;
import kr.ac.pcu.cyber.userservice.service.AuthenticationService;
import kr.ac.pcu.cyber.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserService userService;

    private final static Long VALID_ID = 1004L;
    private final static String VALID_USER_ID = UUID.randomUUID().toString();

    private final static Long INVALID_ID = 10L;
    private final static String INVALID_USER_ID = "abc.def.xyz";

    private final static String NEW_NICKNAME = "Lily";
    private final static String NEW_PROFILE_IMAGE = "https://cdn.naver.com";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOiIyZjQ4ZjI0MS05ZDY0LTRkMTYtYmY1Ni03MGI5ZDRlMGU3OWEifQ." +
            "diJ35TNZtRqYIkkiUZX0JC0IQ_Yia8c5p8FDd_FMgYo";

    @BeforeEach
    void setUp() {

        User user = User.builder()
                .id(VALID_ID)
                .email("test123@namver.com")
                .nickname(NEW_NICKNAME)
                .profileImage(NEW_PROFILE_IMAGE)
                .userId(VALID_USER_ID)
                .build();

        given(authenticationService.parseUserIdFromCookies(any(Cookie[].class)))
                .willReturn(VALID_USER_ID);

        given(authenticationService.getRoles(VALID_USER_ID))
                .willReturn(Collections.singletonList(new Role(RoleType.USER)));

        given(userService.modifyUser(eq(VALID_ID), any(ModifyRequestData.class), eq(VALID_USER_ID)))
                .willReturn(user);

        given(userService.modifyUser(eq(INVALID_ID), any(ModifyRequestData.class), eq(VALID_USER_ID)))
                .willThrow(UserNotFoundException.class);

        given(userService.modifyUser(eq(VALID_ID), any(ModifyRequestData.class), eq(INVALID_USER_ID)))
                .willThrow(AccessDeniedException.class);

        given(authenticationService.getRoles(VALID_USER_ID))
                .willReturn(List.of(new Role(RoleType.USER)));

        given(authenticationService.getRoles(INVALID_USER_ID))
                .willThrow(UserNotFoundException.class);
    }

    @Test
    @DisplayName("사용자 nickname 변경 - 성공")
    void modify_valid_nickname() throws Exception {

        Cookie access = new Cookie("access_token", VALID_TOKEN);
        Cookie refresh = new Cookie("refresh_token", VALID_TOKEN);

        ModifyRequestData requestData = ModifyRequestData.builder()
                .nickname("lily")
                .build();

        mockMvc.perform(patch("/users/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestData))
                        .cookie(access, refresh))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("profileImage").exists());
    }

    @Test
    @DisplayName("사용자 profileImageUrl 변경 - 성공")
    void modify_valid_profile() throws Exception {
        // given
        Cookie access = new Cookie("access_token", VALID_TOKEN);
        Cookie refresh = new Cookie("refresh_token", VALID_TOKEN);

        String profileImage = "https://cdn.kakao.net";

        ModifyRequestData requestData = ModifyRequestData.builder()
                .profileImage(profileImage)
                .build();

        // when & then
        mockMvc.perform(patch("/users/{id}", VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData))
                .cookie(access, refresh))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("profileImage").exists());
    }

    @Test
    @DisplayName("사용자 nickname 변경 - 실패 - 토큰 없음")
    void modify_invalid_without_cookie() throws Exception {

        String newNickname = "lily";

        ModifyRequestData requestData = ModifyRequestData.builder()
                .nickname(newNickname)
                .build();

        mockMvc.perform(patch("/users/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestData)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getRole 정상")
    void getRole_valid() throws Exception {
        mockMvc.perform(get("/users/{userId}/roles", VALID_USER_ID))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("role 조회 실패 - 존재하지 않는 사용자")
    void getRole_invalid_with_user_not_found() throws Exception {
        mockMvc.perform(get("/users/{userId}/roles", INVALID_USER_ID))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}