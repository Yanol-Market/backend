package site.goldenticket.domain.product.wish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.goldenticket.domain.product.wish.entity.WishProduct;

import java.util.List;

public interface WishProductRepository extends JpaRepository<WishProduct, Long> {

    @Query(
            value = "SELECT wp " +
                    "FROM WishProduct wp " +
                    "JOIN FETCH wp.product p " +
                    "WHERE wp.userId = :userId"
    )
    List<WishProduct> findByUserIdWithProduct(@Param("userId") Long userId);
}
