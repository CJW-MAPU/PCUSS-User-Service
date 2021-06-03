package kr.ac.pcu.cyber.userservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kr.ac.pcu.cyber.userservice.errors.InvalidTokenException;
import kr.ac.pcu.cyber.userservice.errors.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final int ONE_DAY = 1000 * 60 * 60 * 24;
    private final int ONE_MONTH = (1000 * 60 * 60 * 24) * 30;

    private Date now = new Date();

    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 사용자 UUID 와 TokenType 을 를 받아 access, refresh 생성
     *
     * @param uuid 사용자 UUID
     * @param type AccessToken, RefreshToken 타입
     * @return Jwt 토큰
     */
    public String generateToken(String uuid, TokenType type) {

        Date expiredAt;

        if(type.equals(TokenType.ACCESS_TOKEN)) { // accessToken 일 때
            expiredAt = new Date(now.getTime() + ONE_DAY);
        }else { // refreshToken 일 때
            expiredAt = new Date(now.getTime() + ONE_MONTH);
        }

        return Jwts.builder()
                .claim("uuid", uuid)
                .setExpiration(expiredAt)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰 문자열을 받아서 Claims 를 반환한다.
     *
     * @param token access, refresh token
     * @return 사용자 정보가 담긴 문자열
     * @throws TokenExpiredException 토큰이 만료되었을 때
     * @throws InvalidTokenException 토큰이 유효하지 않을 때
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e) {
            throw new TokenExpiredException(token);
        } catch (SignatureException e){
            throw new InvalidTokenException(token);
        }
    }
}
