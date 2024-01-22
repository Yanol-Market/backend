package site.goldenticket.domain.product.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    boolean existsByReservationId(Long reservationId);

    List<Product> findAllByUserId(Long userId);

    @Query("SELECT p " +
            "FROM Product p " +
            "LEFT JOIN p.wishProducts wp " +
            "ON wp.userId = :userId " +
            "WHERE p.productStatus IN ('SELLING', 'RESERVED') " +
            "ORDER BY p.goldenPrice ASC, p.id DESC")
    List<Product> findTop5ByGoldenPriceAsc(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT p " +
            "FROM Product p " +
            "LEFT JOIN p.wishProducts wp " +
            "ON wp.userId = :userId " +
            "WHERE p.productStatus IN ('SELLING', 'RESERVED') " +
            "ORDER BY p.viewCount DESC, p.id DESC")
    List<Product> findTop5ByViewCountDesc(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT p " +
            "FROM Product p " +
            "LEFT JOIN p.wishProducts wp " +
            "ON wp.userId = :userId " +
            "WHERE p.productStatus IN ('SELLING', 'RESERVED') " +
            "ORDER BY p.id DESC")
    List<Product> findTop5ByIdDesc(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT p " +
            "FROM Product p " +
            "LEFT JOIN p.wishProducts wp " +
            "ON wp.userId = :userId " +
            "WHERE p.productStatus IN ('SELLING', 'RESERVED') AND p.reservationType = 'DAY_USE'" +
            "ORDER BY p.checkInDate ASC, p.id DESC")
    List<Product> findTop5DayUseProductsCheckInDateAsc(
            @Param("userId") Long userId,
            Pageable pageable
    );
  
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.wishProducts wp " +
            "ON wp.userId = :userId " +
            "WHERE p.id = :productId")
    Product findProductWithWishProductsByProductIdAndUserId(
            @Param("productId") Long productId,
            @Param("userId") Long userId
    );

    List<Product> findByProductStatusInAndUserId(List<ProductStatus> productStatusList, Long userId);

    Product findByProductStatusAndId(ProductStatus productStatus, Long productId);
  
}
