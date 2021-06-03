package kr.ac.pcu.cyber.userservice.domain.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterData {
    private String email;
    private String nickname;
    private String profileUrl;
}
