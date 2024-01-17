package site.goldenticket.common.utils;

import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;
import java.time.LocalTime;

public final class ProductUtils {

    private ProductUtils() {
    }

    public static Product createProduct() {
        return Product.builder()
                .areaCode(AreaCode.SEOUL)
                .accommodationImage("숙소 이미지 URL")
                .accommodationName("숙소명")
                .accommodationAddress("숙소 주소")
                .reservationType(ReservationType.STAY)
                .roomName("객실명")
                .standardNumber(2)
                .maximumNumber(4)
                .checkInTime(LocalTime.of(15, 0))
                .checkOutTime(LocalTime.of(12, 0))
                .checkInDate(LocalDate.of(2024, 1, 10))
                .checkOutDate(LocalDate.of(2024, 1, 15))
                .reservationDate(LocalDate.of(2023, 12, 30))
                .originPrice(180000)
                .yanoljaPrice(200000)
                .goldenPrice(150000)
                .content("상품 설명")
                .viewCount(7)
                .productStatus(ProductStatus.SELLING)
                .reservationId(1L)
                .userId(1L)
                .sellerViewCheck(false)
                .build();
    }
}
