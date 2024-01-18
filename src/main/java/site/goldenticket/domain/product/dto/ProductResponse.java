package site.goldenticket.domain.product.dto;

import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.common.utils.DateUtil;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;

public record ProductResponse(
        Long productId,
        String accommodationImage,
        String accommodationName,
        ReservationType reservationType,
        String roomName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        long nights,
        long days,
        int originPrice,
        int yanoljaPrice,
        int goldenPrice,
        ProductStatus productStatus
) {

    public static ProductResponse fromEntity(Product product) {
        LocalDate checkInDate = product.getCheckInDate();
        LocalDate checkOutDate = product.getCheckOutDate();

        long nights = DateUtil.daysBetween(checkInDate, checkOutDate);
        long days = DateUtil.daysFromNow(checkInDate);

        return new ProductResponse(
                product.getId(),
                product.getAccommodationImage(),
                product.getAccommodationName(),
                product.getReservationType(),
                product.getRoomName(),
                product.getCheckInDate(),
                product.getCheckOutDate(),
                nights,
                days,
                product.getOriginPrice(),
                product.getYanoljaPrice(),
                product.getGoldenPrice(),
                product.getProductStatus()
        );
    }
}
