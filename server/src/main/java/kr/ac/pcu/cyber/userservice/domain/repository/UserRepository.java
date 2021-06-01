package kr.ac.pcu.cyber.userservice.domain.repository;

import kr.ac.pcu.cyber.userservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUUID(String UUID);
}
