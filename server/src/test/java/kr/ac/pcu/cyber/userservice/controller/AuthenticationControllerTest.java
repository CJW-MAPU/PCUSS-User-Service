package kr.ac.pcu.cyber.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import kr.ac.pcu.cyber.userservice.domain.dto.AuthResponseData;
import kr.ac.pcu.cyber.userservice.domain.dto.RegisterRequestData;
import kr.ac.pcu.cyber.userservice.domain.dto.SilentRefreshResponseData;
import kr.ac.pcu.cyber.userservice.errors.EmptyCookieException;
import kr.ac.pcu.cyber.userservice.errors.InvalidTokenException;
import kr.ac.pcu.cyber.userservice.errors.TokenExpiredException;
import kr.ac.pcu.cyber.userservice.errors.UserNotFoundException;
import kr.ac.pcu.cyber.userservice.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    private static final String VALID_UUID = "2f48f241-9d64-4d16-bf56-70b9d4e0e79a";
    private static final String INVALID_UUID = "3d87cc41-i5d6-7da0-5bbf-0e7b9d4e9a70";

    private static final String EMAIL = "james123@gmail.com";
    private static final String NICKNAME = "james";
    private static final String PROFILE_IMAGE = "https://cdn.kakao.com/images/james";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOiIyZjQ4ZjI0MS05ZDY0LTRkMTYtYmY1Ni03MGI5ZDRlMGU3OWEifQ." +
            "diJ35TNZtRqYIkkiUZX0JC0IQ_Yia8c5p8FDd_FMgYo";

    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOiIyZjQ4ZjI0MS05ZDY0LTRkMTYtYmY1Ni03MGI5ZDRlMGU3OWEifQ." +
            "_FMgYodiJ35TNZtRqYIkkiUZX0JC0IQ_Yia8c5p8FDd";
    private static final String MALFORMED_TOKEN = "abc.def.xyz";



    @BeforeEach
    void setUp() {
        AuthResponseData authResponseData = AuthResponseData.builder()
                .id(1004L)
                .userId(VALID_UUID)
                .accessToken(VALID_TOKEN)
                .refreshToken(VALID_TOKEN)
                .nickname(NICKNAME)
                .profileImage(PROFILE_IMAGE)
                .build();

        Cookie cookie = new Cookie("access_token", VALID_TOKEN);
        cookie.setMaxAge(100000);
        cookie.setHttpOnly(true);

        UserNotFoundException userNotFoundException = new UserNotFoundException(INVALID_UUID);

        HttpHeaders headers = new HttpHeaders();

        headers.add("set-cookie", "access_token=null; max-age=0");
        headers.add("set-cookie", "refresh_token=null; max-age=0");

        given(authenticationService.login(VALID_UUID))
                .willReturn(authResponseData);

        given(authenticationService.login(INVALID_UUID))
                .willThrow(userNotFoundException);

        given(authenticationService.register(any(RegisterRequestData.class)))
                .willReturn(authResponseData);

        given(authenticationService.silentRefresh(any(HttpServletRequest.class), any(HttpServletResponse.class))).will(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);

            SilentRefreshResponseData responseData = SilentRefreshResponseData.builder()
                    .id(1004L)
                    .nickname("James")
                    .profileImage("https://cdn.kakao.com")
                    .build();

            if(request.getCookies() == null) {
                throw new EmptyCookieException();
            }else if(request.getCookies()[0].getValue().equals(INVALID_TOKEN)) {
                throw new TokenExpiredException(INVALID_TOKEN);
            }else if(request.getCookies()[0].getValue().equals(MALFORMED_TOKEN)) {
                throw new MalformedJwtException(MALFORMED_TOKEN);
            }else if(request.getCookies()[1].getValue().equals(INVALID_TOKEN)) {
                throw new InvalidTokenException(INVALID_TOKEN);
            } else {
                return responseData;
            }
        });

        given(authenticationService.clearAllCookies())
                .willReturn(headers);
    }

    @Test
    @DisplayName("로그인 - 정상 UUID")
    void login_valid() throws Exception{
        mockMvc.perform(get("/auth/login/{uuid}", VALID_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("asdf", "sd")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("userId").exists())
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists())
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("profileImage").exists())
                .andExpect(jsonPath("userId").exists());
    }

    @Test
    @DisplayName("로그인 - 비정상 UUID")
    void login_invalid_does_not_exist_uuid() throws Exception {
        mockMvc.perform(get("/auth/login/{uuid}", INVALID_UUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("회원가입 - 정상")
    void register_valid() throws Exception {

        RegisterRequestData registerRequestData = RegisterRequestData.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .profileImage(PROFILE_IMAGE)
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestData)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("userId").exists())
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists())
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("profileImage").exists());
    }

    @Test
    @DisplayName("silent-refresh 정상")
    void silentRefresh_valid() throws Exception {
        // given
        Cookie accessCookie = new Cookie("access_token", VALID_TOKEN);
        Cookie refreshCookie = new Cookie("refresh_token", VALID_TOKEN);

        // when & then
        mockMvc.perform(get("/auth/silent-refresh")
                        .cookie(accessCookie)
                        .cookie(refreshCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("profileImage").exists());
    }

    @Test
    @DisplayName("silent-refresh 실패 - 비어있는 쿠키")
    void silentRefresh_invalid_with_empty_cookie() throws Exception {
        // when & then
        mockMvc.perform(get("/auth/silent-refresh"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("silent-refresh 실패 - 유요하지 않은 토큰")
    void silentRefresh_invalid_with_token() throws Exception {

        Cookie cookie = new Cookie("refresh_token", INVALID_TOKEN);

        // when & then
        mockMvc.perform(get("/auth/silent-refresh")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("silent-refresh 실패 - 유효하지 않은 토큰2")
    void silentRefresh_invalid_with_malformed_token2() throws Exception {
        Cookie cookie = new Cookie("refresh_token", MALFORMED_TOKEN);
        Cookie cookie2 = new Cookie("refresh_token", INVALID_TOKEN);
        // when & then
        mockMvc.perform(get("/auth/silent-refresh")
                        .cookie(cookie)
                        .cookie(cookie2))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("silent-refresh 실패 - 이상한 토큰")
    void silentRefresh_invalid_with_malformed_token() throws Exception {
        Cookie cookie = new Cookie("refresh_token", MALFORMED_TOKEN);
        // when & then
        mockMvc.perform(get("/auth/silent-refresh")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("logout 정상")
    void logout_valid() throws Exception {
        mockMvc.perform(get("/auth/logout"))
                .andDo(print())
                .andExpect(status().isOk());
    }


}