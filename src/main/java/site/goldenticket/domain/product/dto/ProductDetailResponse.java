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

    private String accommodationImage;
    private String accommodationName;
    private String accommodationAddress;
    private ReservationType reservationType;
    private String roomName;
    private int standardNumber;
    private int maximumNumber;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long nights;
    private long days;
    private int originPrice;
    private int yanoljaPrice;
    private int goldenPrice;
    private int originPriceRatio;
    private int marketPriceRatio;
    private String content;
    private ProductStatus productStatus;

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
