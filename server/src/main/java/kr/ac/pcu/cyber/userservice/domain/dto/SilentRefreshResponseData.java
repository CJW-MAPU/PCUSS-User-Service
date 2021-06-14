package kr.ac.pcu.cyber.userservice.domain.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SilentRefreshResponseData {
    private Long id;
    private String nickname;
    private String profileImage;
}
