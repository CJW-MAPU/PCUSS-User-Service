package kr.ac.pcu.cyber.userservice.domain.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseData {

    private Long id;
    private String accessToken;
    private String refreshToken;
    private String nickname;
    private String profileUrl;

    public void complete(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
