package kr.ac.pcu.cyber.userservice.utils;

import kr.ac.pcu.cyber.userservice.errors.EmptyCookieException;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
public class CookieUtil {

    // integerOverflow 발생하니까 변경 요망, 아래 의존 코드 존재함
    private final int ONE_DAY = 1000 * 60 * 60 * 24;
    private final int ONE_MONTH = (1000 * 60 * 60 * 24) * 30;

    /**
     * 쿠키 배열로부터 토큰을 파싱한다.
     *
     * @param cookies : RequestHeader 에 getCookies() 연산 반환 값;
     * @param tokenType : access_token 인지 refresh_token 인지 Enum
     * @return userId : 사용자 UUID
     * @throws EmptyCookieException 쿠키에 refresh_token, access_token 쿠키가 없을 때,
     */
    public String parseTokenFromCookies(Cookie[] cookies, TokenType tokenType) {

        String cookieName;

        if(tokenType.equals(TokenType.ACCESS_TOKEN)) {
            cookieName = TokenType.ACCESS_TOKEN.toString().toLowerCase();
        }else if(tokenType.equals(TokenType.REFRESH_TOKEN)) {
            cookieName = TokenType.REFRESH_TOKEN.toString().toLowerCase();
        }else {
            cookieName = "empty";
        }

        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }


        throw new EmptyCookieException();
    }

    /**
     * userId 를 받아 새로운 토큰을 쿠키에 저장하고 쿠키를 생성한다.
     *
     * @param token : 발급 받은 토큰
     * @return tokenType : 발급 받은 토큰의 타입
     */
    public Cookie createCookieWithToken(String token, TokenType tokenType) {

        Cookie cookie;

        if(tokenType.equals(TokenType.ACCESS_TOKEN)) {
            cookie = new Cookie(TokenType.ACCESS_TOKEN.toString().toLowerCase(), token);
            cookie.setMaxAge(ONE_DAY);
        }else {
            cookie = new Cookie(TokenType.REFRESH_TOKEN.toString().toLowerCase(), token);
            cookie.setMaxAge(ONE_MONTH);
        }

        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    /**
     * 쿠키에 존재하는 토큰을 만료시킨다.
     *
     * @param cookies : request 의 쿠키 배열
     * @param tokenType : 만료시킬 토큰의 타입
     */
    public void expireTokenFromCookies(Cookie[] cookies, TokenType tokenType) {

        String cookieName = "empty";

        if(tokenType.equals(TokenType.ACCESS_TOKEN)) {
            cookieName = TokenType.ACCESS_TOKEN.toString().toLowerCase();
        }else if(tokenType.equals(TokenType.REFRESH_TOKEN)) {
            cookieName = TokenType.REFRESH_TOKEN.toString().toLowerCase();
        }

        for(Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                cookie.setMaxAge(0);
                cookie.setValue(null);
            }
        }
    }
}
