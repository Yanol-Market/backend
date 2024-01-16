package site.goldenticket.domain.product.dto;

import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;

public record ProductRequest(
        Integer goldenPrice,
        String content
) {

    public Product toEntity(ReservationDetailsResponse reservationDetailsResponse, Long userId) {

        return Product.builder()
                .areaCode(reservationDetailsResponse.getAreaCode())
                .accommodationImage(reservationDetailsResponse.getAccommodationImage())
                .accommodationName(reservationDetailsResponse.getAccommodationName())
                .accommodationAddress(reservationDetailsResponse.getAccommodationAddress())
                .reservationType(reservationDetailsResponse.getReservationType())
                .roomName(reservationDetailsResponse.getRoomName())
                .standardNumber(reservationDetailsResponse.getStandardNumber())
                .maximumNumber(reservationDetailsResponse.getMaximumNumber())
                .checkInDate(reservationDetailsResponse.getCheckInDate())
                .checkOutDate(reservationDetailsResponse.getCheckOutDate())
                .reservationDate(reservationDetailsResponse.getReservationDate())
                .originPrice(reservationDetailsResponse.getOriginPrice())
                .yanoljaPrice(reservationDetailsResponse.getYanoljaPrice())
                .goldenPrice(goldenPrice)
                .content(content)
                .viewCount(0)
                .productStatus(ProductStatus.SELLING)
                .reservationId(reservationDetailsResponse.getReservationId())
                .userId(userId)
                .sellerViewCheck(false)
                .build();
    }
}
