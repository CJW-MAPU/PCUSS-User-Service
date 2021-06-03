package kr.ac.pcu.cyber.userservice.service;

import kr.ac.pcu.cyber.userservice.domain.dto.AuthResponseData;
import kr.ac.pcu.cyber.userservice.domain.dto.RegisterData;
import kr.ac.pcu.cyber.userservice.domain.entity.User;
import kr.ac.pcu.cyber.userservice.domain.repository.UserRepository;
import kr.ac.pcu.cyber.userservice.errors.UserNotFoundException;
import kr.ac.pcu.cyber.userservice.utils.JwtUtil;
import kr.ac.pcu.cyber.userservice.utils.TokenType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationService(ModelMapper modelMapper, UserRepository userRepository, JwtUtil jwtUtil) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * userId 로 사용자 정보를 반환한다.
     *
     * @param userId Auth 서버로 부터 넘어온 사용자 userId
     * @return accessToken, refreshToken, nickname, profileUrl 데이터
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
     * @return AuthResponseData (accessToken, refreshToken, id, nickname, uuid, profileUrl)
     */
    public AuthResponseData register(RegisterData registerData) {
        User user = new User();
        user.enroll(registerData);

        User savedUser = userRepository.save(user);

        AuthResponseData responseData = modelMapper.map(savedUser, AuthResponseData.class);
        tokenDispenser(jwtUtil, responseData);

        return responseData;
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
