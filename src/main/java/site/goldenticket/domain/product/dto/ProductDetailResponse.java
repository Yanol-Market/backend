package site.goldenticket.domain.product.dto;

import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.dummy.reservation.constants.ReservationType;
import site.goldenticket.domain.product.util.DateUtil;
import site.goldenticket.domain.product.util.DiscountCalculatorUtil;
import site.goldenticket.domain.product.model.Product;

import java.time.LocalDate;
import java.time.LocalTime;

public record ProductDetailResponse(
        String accommodationImage,
        String accommodationName,
        String accommodationAddress,
        ReservationType reservationType,
        String roomName,
        int standardNumber,
        int maximumNumber,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        long nights,
        long days,
        int originPrice,
        int yanoljaPrice,
        int goldenPrice,
        int originPriceRatio,
        int marketPriceRatio,
        String content,
        ProductStatus productStatus,
        boolean isSeller
) {

    public static ProductDetailResponse fromEntity(Product product, boolean isSeller) {
        LocalDate checkInDate = product.getCheckInDate();
        LocalDate checkOutDate = product.getCheckOutDate();

        long nights = DateUtil.daysBetween(checkInDate, checkOutDate);
        long days = DateUtil.daysFromNow(checkInDate);

        int originPrice = product.getOriginPrice();
        int yanoljaPrice = product.getYanoljaPrice();
        int goldenPrice = product.getGoldenPrice();

        int originPriceRatio = DiscountCalculatorUtil.calculateDiscountPercentage(originPrice, goldenPrice);
        int marketPriceRatio = DiscountCalculatorUtil.calculateDiscountPercentage(yanoljaPrice, goldenPrice);

        return new ProductDetailResponse(
                product.getAccommodationImage(),
                product.getAccommodationName(),
                product.getAccommodationAddress(),
                product.getReservationType(),
                product.getRoomName(),
                product.getStandardNumber(),
                product.getMaximumNumber(),
                product.getCheckInTime(),
                product.getCheckOutTime(),
                product.getCheckInDate(),
                product.getCheckOutDate(),
                nights,
                days,
                originPrice,
                yanoljaPrice,
                goldenPrice,
                originPriceRatio,
                marketPriceRatio,
                product.getContent(),
                product.getProductStatus(),
                isSeller
        );
    }
}
