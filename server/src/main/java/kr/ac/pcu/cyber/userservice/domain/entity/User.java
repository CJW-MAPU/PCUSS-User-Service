package kr.ac.pcu.cyber.userservice.domain.entity;

import kr.ac.pcu.cyber.userservice.domain.dto.RegisterRequestData;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
    private String userId;
    @Builder.Default
    private boolean deleted = false;

    /**
     * 스스로 UUID 를 생성하는 mapping method 역할을 수행한다.
     *
     * @param registerRequestData : email, nickname, profileImage
     */
    public void enroll(RegisterRequestData registerRequestData) {
        this.email = registerRequestData.getEmail();
        this.nickname = registerRequestData.getNickname();
        this.profileImage = registerRequestData.getProfileImage();
        this.userId = UUID.randomUUID().toString();
    }

    /**
     * 스스로 nickname 을 변경한다.
     *
     * @param source
     */
    public void changeNicknameWith(User source) {
        nickname = source.nickname;
    }

    /**
     * 스스로 profileImage 를 변경한다.
     *
     * @param source
     */
    public void changeProfileImageWith(User source) {
        profileImage = source.profileImage;
    }

    /**
     * 스스로 동일한 회원인지 확인한다.
     *
     * @param targetUserId 검증하려는 userId
     * @return true : 동일
     */
    public boolean isSameUser(String targetUserId) {
        return userId.equals(targetUserId);
    }

    /**
     * deleted 칼럼의 값을 true 로 만들어 탈퇴를 시행한다.
     */
    public void destroy() {
        deleted = true;
    }

}