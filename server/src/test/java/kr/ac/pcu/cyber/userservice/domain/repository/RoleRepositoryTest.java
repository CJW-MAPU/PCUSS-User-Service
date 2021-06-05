package kr.ac.pcu.cyber.userservice.domain.repository;

import kr.ac.pcu.cyber.userservice.domain.entity.Role;
import kr.ac.pcu.cyber.userservice.domain.entity.RoleType;
import kr.ac.pcu.cyber.userservice.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.transaction.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Role 저장")
    void createRole_when_register() {
        // given
        String userId = UUID.randomUUID().toString();

        User user = User.builder()
                .nickname("james")
                .email("james123@gmail.com")
                .profileImage("https://cdn.kakao.com/fi")
                .userId(userId)
                .build();

        User savedUser = userRepository.save(user);
        Role role = new Role(savedUser.getUserId(), RoleType.USER);

        // when
        Role savedRole = roleRepository.save(role);

        // then
        assertEquals(RoleType.USER, savedRole.getRoleType());
    }
}