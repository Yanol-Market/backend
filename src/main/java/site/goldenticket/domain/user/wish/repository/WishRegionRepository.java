package site.goldenticket.domain.user.wish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.user.wish.entity.WishRegion;

import java.util.List;

public interface WishRegionRepository extends JpaRepository<WishRegion, Long> {

    List<WishRegion> findByUserId(Long userId);
}
