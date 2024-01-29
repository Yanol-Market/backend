package site.goldenticket.dummy.yauser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.goldenticket.dummy.yauser.model.YaUser;

import java.util.Optional;

@Repository
public interface YaUserRepository extends JpaRepository<YaUser, Long> {

    Optional<YaUser> findByEmail(String email);
}
