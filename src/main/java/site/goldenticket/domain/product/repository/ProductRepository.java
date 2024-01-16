package site.goldenticket.domain.product.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.goldenticket.domain.product.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    boolean existsByReservationId(Long reservationId);
    @Query("SELECT p FROM Product p WHERE p.productStatus IN ('SELLING', 'RESERVED') ORDER BY p.goldenPrice ASC, p.id DESC")
    List<Product> findTop5ByGoldenPriceAsc(Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.productStatus IN ('SELLING', 'RESERVED') ORDER BY p.viewCount DESC, p.id DESC")
    List<Product> findTop5ByViewCountDesc(Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.productStatus IN ('SELLING', 'RESERVED') ORDER BY p.id DESC")
    List<Product> findTop5ByIdDesc(Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.productStatus IN ('SELLING', 'RESERVED') AND p.reservationType = 'DAY_USE' ORDER BY p.checkInDate ASC, p.id DESC")
    List<Product> findTop5DayUseProductsCheckInDateAsc(Pageable pageable);
}
