package kr.ac.pcu.cyber.userservice.domain.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestData {
    private String email;
    private String nickname;
    private String profileImage;
}
