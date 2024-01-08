package site.goldenticket.yauser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.goldenticket.yauser.model.YaUser;

@Repository
public interface YaUserRepository extends JpaRepository<YaUser, Long> {
}