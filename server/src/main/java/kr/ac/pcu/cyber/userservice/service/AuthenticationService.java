package kr.ac.pcu.cyber.userservice.service;

import io.jsonwebtoken.Claims;
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
import kr.ac.pcu.cyber.userservice.utils.TokenType;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AuthenticationService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public AuthenticationService(ModelMapper modelMapper,
                                 UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 JwtUtil jwtUtil,
                                 CookieUtil cookieUtil) {

        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }

    /**
     * userId 로 사용자 정보를 반환한다.
     *
     * @param userId Auth 서버로 부터 넘어온 사용자 userId
     * @return accessToken, refreshToken, nickname, profileImage 데이터
     * @throws UserNotFoundException
     */
    public AuthResponseData login(String userId) {

        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(userId));

        AuthResponseData responseData = modelMapper.map(user, AuthResponseData.class);
        tokenDispenser(jwtUtil, responseData);

        return responseData;
    }

    /**
     * body 로 들어오는 nickname, email, profile 정보로 사용자를 저장(회원가입)한다.
     *
     * @param
     * @return AuthResponseData (accessToken, refreshToken, id, nickname, uuid, profileImage)
     */
    public AuthResponseData register(RegisterRequestData registerRequestData) {
        User user = new User();
        user.enroll(registerRequestData);

        User savedUser = userRepository.save(user);

        AuthResponseData responseData = modelMapper.map(savedUser, AuthResponseData.class);
        tokenDispenser(jwtUtil, responseData);

        roleRepository.save(new Role(user.getUserId(), RoleType.USER));

        return responseData;
    }

    /**
     * 쿠키에 존재하는 refresh_token 을 검증하고 새로운 access_token 을 cookie 에 추가한 후 반환한다.
     *
     * @param request
     */
    public Cookie silentRefresh(HttpServletRequest request) {

        if(request.getCookies() == null) {
            throw new EmptyCookieException();
        }

        String refreshToken = cookieUtil.parseTokenFromCookies(
                request.getCookies(),
                TokenType.REFRESH_TOKEN);

        Claims claims = jwtUtil.parseToken(refreshToken);
        String userId = claims.get("userId", String.class);

        String newAccessToken = jwtUtil.generateToken(userId, TokenType.ACCESS_TOKEN);


        return cookieUtil.createCookieWithToken(newAccessToken, TokenType.ACCESS_TOKEN);
    }

    /**
     * 쿠키에 존재하는 모든 토큰을 제거한다.
     *
     * @return set-cookie 로 access_token 의 max-age 와 value 를 null 하는 HttpHeaders
     */
    public HttpHeaders clearAllCookies() {
        HttpHeaders headers = new HttpHeaders();

        headers.add("set-cookie", "access_token=null; max-age=0");
        headers.add("set-cookie", "refresh_token=null; max-age=0");

        return headers;
    }

    /**
     * Cookie 배열로 부터 accessToken 의 userId 를 반환한다.
     *
     * @param cookies : Request 에 존재하는 cookies
     * @return userId : accessToken 이 담긴 cookie 에서 토큰을 파싱하고 userId 를 반환한다.
     */
    public String parseUserIdFromCookies(Cookie[] cookies) {
        String accessToken = cookieUtil.parseTokenFromCookies(cookies, TokenType.REFRESH_TOKEN);
        Claims claims = jwtUtil.parseToken(accessToken);
        return claims.get("userId", String.class);
    }

    /**
     * userId 를 받고 해당 user 의 role 을 조회한다.
     *
     * @param
     * @return
     */
    public List<Role> getRoles(String userId) {
        if(!userRepository.existsByUserId(userId)) {
            throw new UserNotFoundException(userId);
        }

        return roleRepository.findAllByUserId(userId);
    }

    /**
     * AuthResponseData 가 스스로 access, refresh 토큰을 생성하도록 한다.
     *
     * @param jwtUtil generateToken 을 수행할 jwtUtil;
     * @param data 토큰을 추가할 AuthResponseData
     */
    private static void tokenDispenser(JwtUtil jwtUtil, AuthResponseData data) {
        String accessToken = jwtUtil.generateToken(data.getUserId(), TokenType.ACCESS_TOKEN);
        String refreshToken = jwtUtil.generateToken(data.getUserId(), TokenType.REFRESH_TOKEN);

        data.complete(accessToken, refreshToken);
    }
}
