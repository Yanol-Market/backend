package site.goldenticket.domain.nego.dto.response;

import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;

public record HandoverResponse(
        String accommodationImage,
        String accommodationName,
        ReservationType reservationType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer price,
        Long productId,
        ProductStatus productStatus
) {
    public static HandoverResponse fromEntity(Product product, Nego nego) {
        return new HandoverResponse(
                product.getAccommodationImage(),
                product.getAccommodationName(),
                product.getReservationType(),
                product.getCheckInDate(),
                product.getCheckOutDate(),
                nego.getPrice(),
                product.getId(),
                product.getProductStatus()
        );
    }
}
