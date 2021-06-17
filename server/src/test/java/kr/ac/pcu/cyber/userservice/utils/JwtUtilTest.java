package kr.ac.pcu.cyber.userservice.utils;

import io.jsonwebtoken.Claims;
import kr.ac.pcu.cyber.userservice.errors.InvalidTokenException;
import kr.ac.pcu.cyber.userservice.errors.TokenExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private JwtUtil jwtUtil;

    private static final String SECRET = "12345678901234567890123456789012";

    private static final String UserId = "2f48f241-9d64-4d16-bf56-70b9d4e0e79a";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOiIyZjQ4ZjI0MS05ZDY0LTRkMTYtYmY1Ni03MGI5ZDRlMGU3OWEifQ." +
            "diJ35TNZtRqYIkkiUZX0JC0IQ_Yia8c5p8FDd_FMgYo";

    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1dWlkIjoiMmY0OGYyNDEtOWQ2NC00ZDE2LWJmNTYtNzBiOWQ0ZTBlNzlhIiwiZXhwIjoxNjIyNjM2Nzc3fQ.J" +
            "gzsO-ovbRtts6ufTaix37R12T5Ngqd4cnxIgOQIxJ1";

    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOiIyZjQ4ZjI0MS05ZDY0LTRkMTYtYmY1Ni03MGI5ZDRlMGU3OWEiLCJleHAiOjE2MjI2MzE2Nzd9." +
            "75VsVNYXpd7_SZDS1jZFh_4LVaFmRzZmW5XFfpWKC5g";
    ;


    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
    }

    @Test
    @DisplayName("토큰 생성 - 정상 입력")
    void generate_token_valid() {
        String accessToken = jwtUtil.generateToken(UserId, TokenType.ACCESS_TOKEN);
        String refreshToken = jwtUtil.generateToken(UserId, TokenType.REFRESH_TOKEN);
        System.out.println(accessToken);
        assertAll(
                () -> assertNotNull(accessToken),
                () -> assertNotNull(refreshToken),
                () -> assertNotEquals(accessToken, refreshToken)
        );

    }

    @Test
    @DisplayName("토큰 파싱 - 정상")
    void parse_token_valid() {
        Claims parsedClaim = jwtUtil.parseToken(VALID_TOKEN);

        assertEquals(parsedClaim.get("userId", String.class), UserId);
    }

    @Test
    @DisplayName("토큰 파싱 - 실패 invalid 토큰")
    void parse_token_invalid_token() {
        InvalidTokenException exception = assertThrows(
                InvalidTokenException.class,
                () -> jwtUtil.parseToken(INVALID_TOKEN)
        );

        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("토큰 파싱 - 실패 만료된 토큰")
    void parse_token_invalid_expired_token() {
        TokenExpiredException exception = assertThrows(
                TokenExpiredException.class,
                () -> jwtUtil.parseToken(EXPIRED_TOKEN)
        );

        assertNotNull(exception.getMessage());
    }
}