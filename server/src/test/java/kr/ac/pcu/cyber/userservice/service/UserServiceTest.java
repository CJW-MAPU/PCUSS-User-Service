package kr.ac.pcu.cyber.userservice.service;

import kr.ac.pcu.cyber.userservice.domain.dto.ModifyRequestData;
import kr.ac.pcu.cyber.userservice.domain.entity.User;
import kr.ac.pcu.cyber.userservice.domain.repository.UserRepository;
import kr.ac.pcu.cyber.userservice.errors.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class UserServiceTest {
    private UserService userService;

    private final UserRepository userRepository = mock(UserRepository.class);

    private static final Long VALID_ID = 1004L;
    private static final Long INVALID_ID = 10L;

    private static final String VALID_USER_ID = UUID.randomUUID().toString();
    private static final String INVALID_USER_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        userService = new UserService(userRepository, modelMapper);

        User user = User.builder()
                .email("email@email.com")
                .nickname("James")
                .profileImage("none")
                .userId(VALID_USER_ID)
                .build();

        given(userRepository.findById(VALID_ID)).willReturn(Optional.of(user));
        given(userRepository.findById(INVALID_ID)).willReturn(Optional.empty());
    }

    @Test
    @DisplayName("닉네임 수정 테스트 - 성공")
    void modify_nickname_valid() {
        // given
        String newNickname = "Lily";
        ModifyRequestData modifyRequestData = ModifyRequestData.builder()
                .nickname(newNickname)
                .build();

        // when
        User user = userService.modifyUser(VALID_ID, modifyRequestData, VALID_USER_ID);

        // then
        assertEquals(newNickname, user.getNickname());
    }

    @Test
    @DisplayName("닉네임 수정 테스트 - 실패 - 존재하지 않는 사용자")
    void modify_nickname_invalid_with_not_same_user() {
        // given
        String newNickname = "Lily";
        ModifyRequestData modifyRequestData = ModifyRequestData.builder()
                .nickname(newNickname)
                .build();

        // when
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.modifyUser(INVALID_ID,
                        modifyRequestData,
                        VALID_USER_ID)
        );

        // then
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("닉네임 수정 테스트 - 실패 - 접근 불가")
    void modify_nickname_invalid_with_Access_denied() {
        // given
        String newNickname = "Lily";
        ModifyRequestData modifyRequestData = ModifyRequestData.builder()
                .nickname(newNickname)
                .build();

        // when
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> userService.modifyUser(VALID_ID,
                        modifyRequestData,
                        INVALID_USER_ID)
        );

        // then
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("이미지 수정 테스트 - 성공")
    void modify_profile_image_valid() {
        // given
        String newImage = "https://daum.net";
        ModifyRequestData modifyRequestData = ModifyRequestData.builder()
                .profileImage(newImage)
                .build();

        // when
        User user = userService.modifyUser(VALID_ID, modifyRequestData, VALID_USER_ID);

        // then
        assertEquals(newImage, user.getProfileImage());
    }
}
