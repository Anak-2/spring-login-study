package user.study.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import user.study.member.domain.user.User;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByName(String name);
    boolean existsByName(String name);

}
