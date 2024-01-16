package site.goldenticket.domain.product.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.util.DateUtil;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;

@Getter
@Builder
public class ProductResponse {

    private Long productId;
    private String accommodationImage;
    private String accommodationName;
    private ReservationType reservationType;
    private String roomName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long nights;
    private long days;
    private int originPrice;
    private int yanoljaPrice;
    private int goldenPrice;
    private ProductStatus productStatus;

    public static ProductResponse fromEntity(Product product) {

        LocalDate checkInDate = product.getCheckInDate();
        LocalDate checkOutDate = product.getCheckOutDate();

        long nights = DateUtil.daysBetween(checkInDate, checkOutDate);
        long days = DateUtil.daysFromNow(checkInDate);

        return ProductResponse.builder()
                .productId(product.getId())
                .accommodationImage(product.getAccommodationImage())
                .accommodationName(product.getAccommodationName())
                .reservationType(product.getReservationType())
                .roomName(product.getRoomName())
                .checkInDate(product.getCheckInDate())
                .checkOutDate(product.getCheckOutDate())
                .nights(nights)
                .days(days)
                .originPrice(product.getOriginPrice())
                .yanoljaPrice(product.getYanoljaPrice())
                .goldenPrice(product.getGoldenPrice())
                .productStatus(product.getProductStatus())
                .build();
    }
}
