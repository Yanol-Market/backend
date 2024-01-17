package site.goldenticket.domain.user.wish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.user.wish.entity.WishProduct;

public interface WishProductRepository extends JpaRepository<WishProduct, Long> {
}
