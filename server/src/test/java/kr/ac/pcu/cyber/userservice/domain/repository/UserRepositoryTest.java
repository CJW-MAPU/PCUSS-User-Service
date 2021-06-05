package kr.ac.pcu.cyber.userservice.domain.repository;

import kr.ac.pcu.cyber.userservice.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("User 생성")
    void createUser() {
        String nickname = "james";
        User user = User.builder()
                .email("test123@gmail.com")
                .nickname(nickname)
                .userId(UUID.randomUUID().toString())
                .build();

        User savedUser = userRepository.save(user);

        assertEquals(nickname, savedUser.getNickname());
    }

    @Test
    @DisplayName("userId 로 회원 조회")
    void findUserByUserId() {
        // given
        String nickname = "james";
        String uuid = UUID.randomUUID().toString();

        User user = User.builder()
                .email("test123@gmail.com")
                .nickname(nickname)
                .userId(uuid)
                .build();

        userRepository.save(user);

        // when
        Optional<User> optionalUser = userRepository.findByUserId(uuid);

        // then
        User selectedUser = optionalUser.get();
        assertEquals(nickname, selectedUser.getNickname());
    }

    @Test
    @DisplayName("userId 로 사용자 조회")
    void existsByUserId_valid() {

        // given
        String userId = UUID.randomUUID().toString();

        User user = User.builder()
                .email("test123@gmail.com")
                .nickname("james")
                .userId(userId)
                .build();

        userRepository.save(user);

        // when
        boolean isExists = userRepository.existsByUserId(userId);

        // then
        assertTrue(isExists);
    }

}