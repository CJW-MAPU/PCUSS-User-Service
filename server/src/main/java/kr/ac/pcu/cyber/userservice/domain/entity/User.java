package kr.ac.pcu.cyber.userservice.domain.entity;

import kr.ac.pcu.cyber.userservice.domain.dto.RegisterData;
import lombok.*;

import javax.persistence.*;
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
    private String profileUrl;
    private String userId;

    /**
     * 스스로 UUID 를 생성하는 mapping method 역할을 수행한다.
     *
     * @param registerData : email, nickname, profileUrl
     */
    public void enroll(RegisterData registerData) {
        this.email = registerData.getEmail();
        this.nickname = registerData.getNickname();
        this.profileUrl = registerData.getProfileUrl();
        this.userId = UUID.randomUUID().toString();
    }
}
