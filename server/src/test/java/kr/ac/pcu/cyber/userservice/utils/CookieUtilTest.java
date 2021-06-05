package kr.ac.pcu.cyber.userservice.utils;

import kr.ac.pcu.cyber.userservice.errors.EmptyCookieException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;

class CookieUtilTest {

    private CookieUtil cookieUtil;

    private static final String SECRET = "12345678901234567890123456789012";

    private static final String USER_ID = "2f48f241-9d64-4d16-bf56-70b9d4e0e79a";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOiIyZjQ4ZjI0MS05ZDY0LTRkMTYtYmY1Ni03MGI5ZDRlMGU3OWEifQ." +
            "diJ35TNZtRqYIkkiUZX0JC0IQ_Yia8c5p8FDd_FMgYo";

    private final int ONE_MONTH = (1000 * 60 * 60 * 24) * 30;

    @BeforeEach
    void setUp() {
        cookieUtil = new CookieUtil();
    }

    @Test
    @DisplayName("parseTokenFromCookies - 정상 access 토큰")
    void parseTokenFromCookies_valid_access() {
        // given
        Cookie cookie1 = new Cookie("no", "null");
        Cookie cookie2 = new Cookie("access_token", VALID_TOKEN);

        Cookie[] cookies = { cookie1, cookie2 };

        // when
        String token = cookieUtil.parseTokenFromCookies(cookies, TokenType.ACCESS_TOKEN);

        // then
        assertEquals(VALID_TOKEN, token);
    }

    @Test
    @DisplayName("parseTokenFromCookies - 정상 refresh 토큰")
    void parseUserIdFromCookies_valid_refresh() {

        // given
        Cookie cookie1 = new Cookie("no", "null");
        Cookie cookie2 = new Cookie("refresh_token", VALID_TOKEN);

        Cookie[] cookies = { cookie1, cookie2 };

        // when
        String token = cookieUtil.parseTokenFromCookies(cookies, TokenType.REFRESH_TOKEN);

        // then
        assertEquals(VALID_TOKEN, token);
    }

    @Test
    @DisplayName("parseTokenFromCookies - 실패 - 토큰 없음")
    void parseTokenFromCookies_invalid_empty_cookie() {
        // given
        Cookie cookie1 = new Cookie("no", "null");
        Cookie cookie2 = new Cookie("fresh_token", VALID_TOKEN);

        Cookie[] cookies = { cookie1, cookie2 };

        // when
        EmptyCookieException exception = assertThrows(EmptyCookieException.class,
                () -> cookieUtil.parseTokenFromCookies(cookies, TokenType.REFRESH_TOKEN)
        );

        // then
        assertNotNull(exception);
    }

    @Test
    @DisplayName("createCookieWithToken - 정상 refresh 토큰")
    void createCookieWithToken_valid() {

        // given & when
        Cookie cookie = cookieUtil.createCookieWithToken(VALID_TOKEN, TokenType.ACCESS_TOKEN);

        // then
        assertAll(
                () ->assertEquals(TokenType.ACCESS_TOKEN.toString().toLowerCase(), cookie.getName()),
                () -> assertEquals(VALID_TOKEN, cookie.getValue())
        );

    }

    @Test
    @DisplayName("expireTokenFromCookies - valid")
    void expireTokenFromCookies_valid() {

        // given
        Cookie cookie1 = new Cookie("access_token", VALID_TOKEN);
        cookie1.setMaxAge(100000);
        System.out.println("cookie1 = " + cookie1.getMaxAge());
        Cookie cookie2 = new Cookie("refresh_token", VALID_TOKEN);
        cookie2.setMaxAge(100000);

        Cookie[] cookies = { cookie1, cookie2 };

        // when
        cookieUtil.expireTokenFromCookies(cookies, TokenType.ACCESS_TOKEN);
        cookieUtil.expireTokenFromCookies(cookies, TokenType.REFRESH_TOKEN);

        // then
        assertAll(
                () -> assertEquals(0, cookie1.getMaxAge()),
                () -> assertEquals(0, cookie2.getMaxAge())
        );
    }

}