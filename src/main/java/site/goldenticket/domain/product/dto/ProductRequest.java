package site.goldenticket.domain.product.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.common.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.reservation.model.Reservation;

@Getter
@Builder
public class ProductRequest {

    private Integer goldenPrice;
    private String content;

    public Product toEntity(Reservation reservation, Long reservationId, Long userId) {

        return Product.builder()
                .areaCode(reservation.getAreaCode())
                .accommodationImage(reservation.getAccommodationImage())
                .accommodationName(reservation.getAccommodationName())
                .accommodationAddress(reservation.getAccommodationAddress())
                .reservationType(reservation.getReservationType())
                .roomName(reservation.getRoomName())
                .standardNumber(reservation.getStandardNumber())
                .maximumNumber(reservation.getMaximumNumber())
                .checkInTime(reservation.getCheckInTime())
                .checkOutDate(reservation.getCheckOutDate())
                .checkInDate(reservation.getCheckInDate())
                .checkOutTime(reservation.getCheckOutTime())
                .reservationDate(reservation.getReservationDate())
                .originPrice(reservation.getOriginPrice())
                .yanoljaPrice(reservation.getYanoljaPrice())
                .goldenPrice(goldenPrice)
                .content(content)
                .viewCount(0)
                .productStatus(ProductStatus.SELLING)
                .reservationId(reservationId)
                .userId(userId)
                .build();
    }
}
