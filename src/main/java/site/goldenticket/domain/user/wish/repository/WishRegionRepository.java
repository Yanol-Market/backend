package site.goldenticket.domain.user.wish.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.user.wish.entity.WishRegion;

public interface WishRegionRepository extends JpaRepository<WishRegion, Long> {

    List<WishRegion> findAllByUserId(Long userId);
}
