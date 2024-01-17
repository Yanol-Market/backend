package site.goldenticket.domain.user.wish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.user.wish.entity.Wish;

public interface WishRepository extends JpaRepository<Wish, Long> {
}
