package kr.ac.pcu.cyber.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.pcu.cyber.userservice.domain.dto.AuthResponseData;
import kr.ac.pcu.cyber.userservice.domain.dto.RegisterData;
import kr.ac.pcu.cyber.userservice.errors.UserNotFoundException;
import kr.ac.pcu.cyber.userservice.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    private static final String VALID_UUID = "2f48f241-9d64-4d16-bf56-70b9d4e0e79a";
    private static final String INVALID_UUID = "3d87cc41-i5d6-7da0-5bbf-0e7b9d4e9a70";

    private static final String EMAIL = "james123@gmail.com";
    private static final String NICKNAME = "james";
    private static final String PROFILE_URL = "https://cdn.kakao.com/images/james";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1dWlkIjoiMmY0OGYyNDEtOWQ2NC00ZDE2LWJmNTYtNzBiOWQ0ZTBlNzlhIiwiZXhwIjoxNjIyNjM2Nzc3fQ.J" +
            "gzsO-ovbRtts6ufTaix37R12T5Ngqd4cnxIxJ1IgOQ";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        AuthResponseData authResponseData = AuthResponseData.builder()
                .id(1004L)
                .userId(VALID_UUID)
                .accessToken(VALID_TOKEN)
                .refreshToken(VALID_TOKEN)
                .nickname(NICKNAME)
                .profileUrl(PROFILE_URL)
                .build();
        UserNotFoundException userNotFoundException = new UserNotFoundException(INVALID_UUID);

        given(authenticationService.login(VALID_UUID)).willReturn(authResponseData);
        given(authenticationService.login(INVALID_UUID)).willThrow(userNotFoundException);

        given(authenticationService.register(any(RegisterData.class))).willReturn(authResponseData);
    }

    @Test
    @DisplayName("로그인 - 정상 UUID")
    void login_valid() throws Exception{
        mockMvc.perform(get("/auth/login/{uuid}", VALID_UUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("userId").exists())
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists())
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("profileUrl").exists())
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

        RegisterData registerData = RegisterData.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .profileUrl(PROFILE_URL)
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerData)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("userId").exists())
                .andExpect(jsonPath("accessToken").exists())
                .andExpect(jsonPath("refreshToken").exists())
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("profileUrl").exists())
                .andExpect(jsonPath("userId").exists());
    }
}