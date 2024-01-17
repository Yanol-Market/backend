package site.goldenticket.domain.product.dto;

import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.dummy.reservation.dto.ReservationDetailsResponse;

public record ProductRequest(
        Integer goldenPrice,
        String content
) {

    public Product toEntity(ReservationDetailsResponse reservationDetailsResponse, Long userId) {

        return Product.builder()
                .areaCode(reservationDetailsResponse.areaCode())
                .accommodationImage(reservationDetailsResponse.accommodationImage())
                .accommodationName(reservationDetailsResponse.accommodationName())
                .accommodationAddress(reservationDetailsResponse.accommodationAddress())
                .reservationType(reservationDetailsResponse.reservationType())
                .roomName(reservationDetailsResponse.roomName())
                .standardNumber(reservationDetailsResponse.standardNumber())
                .maximumNumber(reservationDetailsResponse.maximumNumber())
                .checkInDate(reservationDetailsResponse.checkInDate())
                .checkOutDate(reservationDetailsResponse.checkOutDate())
                .reservationDate(reservationDetailsResponse.reservationDate())
                .originPrice(reservationDetailsResponse.originPrice())
                .yanoljaPrice(reservationDetailsResponse.yanoljaPrice())
                .goldenPrice(goldenPrice)
                .content(content)
                .viewCount(0)
                .productStatus(ProductStatus.SELLING)
                .reservationId(reservationDetailsResponse.reservationId())
                .userId(userId)
                .sellerViewCheck(false)
                .build();
    }
}
