package site.goldenticket.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.goldenticket.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByYanoljaId(Long yanoljaId);

    Optional<User> findByYanoljaId(Long yanoljaId);

    @Query(
            value = "SELECT u " +
                    "FROM User u " +
                    "LEFT JOIN FETCH u.wishRegions wr " +
                    "WHERE u.id = :id"
    )
    Optional<User> findByIdFetchWishRegion(@Param("id") Long id);
}
