package site.goldenticket.domain.wishRegion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.wishRegion.entity.WishRegion;

public interface WishRegionRepository extends JpaRepository<WishRegion, Long> {

    List<WishRegion> findAllByUserId(Long userId);
}
