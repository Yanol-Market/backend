package site.goldenticket.domain.product.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.common.constants.ProductStatus;
import site.goldenticket.common.constants.ReservationType;
import site.goldenticket.common.util.DateUtil;
import site.goldenticket.common.util.DiscountCalculatorUtil;
import site.goldenticket.domain.product.model.Product;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ProductDetailResponse {

    String accommodationImage;
    String accommodationName;
    String accommodationAddress;
    ReservationType reservationType;
    String roomName;
    int standardNumber;
    int maximumNumber;
    LocalTime checkInTime;
    LocalTime checkOutTime;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    long nights;
    long days;
    int originPrice;
    int yanoljaPrice;
    int goldenPrice;
    int originPriceRatio;
    int marketPriceRatio;
    String content;
    ProductStatus productStatus;

    public static ProductDetailResponse fromEntity(Product product) {

        LocalDate checkInDate = product.getCheckInDate();
        LocalDate checkOutDate = product.getCheckOutDate();

        long nights = DateUtil.daysBetween(checkInDate, checkOutDate);
        long days = DateUtil.daysFromNow(checkInDate);

        int originPrice = product.getOriginPrice();
        int yanoljaPrice = product.getYanoljaPrice();
        int goldenPrice = product.getGoldenPrice();

        int originPriceRatio = DiscountCalculatorUtil.calculateDiscountPercentage(originPrice, goldenPrice);
        int marketPriceRatio = DiscountCalculatorUtil.calculateDiscountPercentage(yanoljaPrice, goldenPrice);

        return ProductDetailResponse.builder()
                .accommodationImage(product.getAccommodationImage())
                .accommodationName(product.getAccommodationName())
                .accommodationAddress(product.getAccommodationAddress())
                .reservationType(product.getReservationType())
                .roomName(product.getRoomName())
                .standardNumber(product.getStandardNumber())
                .maximumNumber(product.getMaximumNumber())
                .checkInTime(product.getCheckInTime())
                .checkOutTime(product.getCheckOutTime())
                .checkInDate(product.getCheckInDate())
                .checkOutDate(product.getCheckOutDate())
                .nights(nights)
                .days(days)
                .originPrice(product.getOriginPrice())
                .yanoljaPrice(product.getYanoljaPrice())
                .goldenPrice(product.getGoldenPrice())
                .originPriceRatio(originPriceRatio)
                .marketPriceRatio(marketPriceRatio)
                .content(product.getContent())
                .productStatus(product.getProductStatus())
                .build();
    }
}
