package kr.ac.pcu.cyber.userservice.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;

    @Getter
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    public Role(String userId, RoleType roleType) {
        this.userId = userId;
        this.roleType = roleType;
    }

    public Role(RoleType roleType) {
        this(null, roleType);
    }
}
