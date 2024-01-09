package site.goldenticket.domain.yauser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.goldenticket.domain.yauser.model.YaUser;

@Repository
public interface YaUserRepository extends JpaRepository<YaUser, Long> {
}