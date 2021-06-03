package kr.ac.pcu.cyber.userservice.domain.entity;

import kr.ac.pcu.cyber.userservice.domain.dto.RegisterData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User user = new User();

    @Test
    @DisplayName("register")
    void register() {
        // given
        String nickname = "james";

        RegisterData registerData = RegisterData.builder()
                .nickname(nickname)
                .email("jame102@gmail.com")
                .profileUrl("https://cdn.kakao.com/sf")
                .build();

        // when
        user.enroll(registerData);

        // then
        assertEquals(user.getNickname(), nickname);
        assertNotNull(user.getUserId());
    }

}