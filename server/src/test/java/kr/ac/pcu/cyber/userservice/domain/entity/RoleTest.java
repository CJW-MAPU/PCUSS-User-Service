package kr.ac.pcu.cyber.userservice.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    @DisplayName("Role 생성 - 생성자 1")
    void createRole_two_arguments () {

        // given
        String VALID_UUID = "2f48f241-9d64-4d16-bf56-70b9d4e0e79a";
        RoleType roleType = RoleType.USER;
        // when
        Role role = new Role(VALID_UUID, roleType);

        // then
        assertEquals(roleType, role.getRoleType());
    }

    @Test
    @DisplayName("Role 생성 - 생성자 2")
    void createRole_one_arguments() {
        RoleType roleType = RoleType.ADMIN;

        Role role = new Role(roleType);

        assertEquals(roleType, role.getRoleType());
    }

}