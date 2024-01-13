package site.goldenticket.domain.product.repository;

import org.springframework.data.domain.Pageable;
import site.goldenticket.common.constants.AreaCode;
import site.goldenticket.common.constants.PriceRange;
import site.goldenticket.common.pagination.slice.CustomSlice;
import site.goldenticket.domain.product.model.Product;

import java.time.LocalDate;

public interface ProductRepositoryCustom {
    CustomSlice<Product> getProductsBySearch(
            AreaCode region, String keyword, LocalDate checkInDate, LocalDate checkOutDate, PriceRange priceRange,
            LocalDate cursorCheckInDate, Long cursorId, Pageable pageable
    );

    CustomSlice<Product> getProductsByAreaCode(
            AreaCode areaCode, LocalDate cursorCheckInDate, Long cursorId, Pageable pageable
    );
}
