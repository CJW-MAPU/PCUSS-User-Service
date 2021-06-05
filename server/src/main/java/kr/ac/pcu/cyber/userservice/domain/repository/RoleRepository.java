package kr.ac.pcu.cyber.userservice.domain.repository;

import kr.ac.pcu.cyber.userservice.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByUserId(String userId);
}
