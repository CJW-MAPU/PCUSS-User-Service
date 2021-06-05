package kr.ac.pcu.cyber.userservice.domain.entity;

import kr.ac.pcu.cyber.userservice.domain.dto.RegisterRequestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final String NICKNAME = "james";
    private static final String PROFILE_IMAGE_URL = "https://kakao.com";
    private static final String EMAIL = "jame102@gmail.com";
    private static final String USER_ID = UUID.randomUUID().toString();

    private static User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .nickname(NICKNAME)
                .email(EMAIL)
                .profileImage(PROFILE_IMAGE_URL)
                .userId(USER_ID)
                .build();
    }

    @Test
    @DisplayName("register")
    void register() {
        // given
        RegisterRequestData registerRequestData = RegisterRequestData.builder()
                .nickname(NICKNAME)
                .email(EMAIL)
                .profileImage("https://cdn.kakao.com/sf")
                .build();

        // when
        user.enroll(registerRequestData);

        // then
        assertEquals(NICKNAME, user.getNickname());
        assertEquals(EMAIL, user.getEmail());
        assertNotNull(user.getUserId());
    }

    @Test
    @DisplayName("changeNickname - 성공")
    void changeNicknameWith_valid() {
        // given
        String newNickname = "Lily";

        User source = User.builder()
                .nickname(newNickname)
                .build();
        // when
        user.changeNicknameWith(source);

        // then
        assertEquals(newNickname, user.getNickname());
    }

    @Test
    @DisplayName("changeProfileImage - 성공")
    void changeProfileImage_valid() {
        // given
        String newProfile = "htt";

        User source = User.builder()
                .profileImage(newProfile)
                .build();

        // when
        user.changeProfileImageWith(source);

        // then
        assertEquals(newProfile, user.getProfileImage());
    }

    @Test
    @DisplayName("isSameUser - 성공")
    void isSameUser_valid() {
        String newUserId = UUID.randomUUID().toString();

        boolean isSame = user.isSameUser(newUserId);

        assertFalse(isSame);
    }

    @Test
    @DisplayName("delete - 성공 (마지막에 테스트 해야함)")
    void destroy_valid() {
        user.destroy();

        assertEquals(true, user.isDeleted());
    }
}