package site.goldenticket.domain.user.wish.dto;

import lombok.Builder;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.user.wish.entity.WishProduct;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;

import static site.goldenticket.common.utils.DateUtil.daysBetween;
import static site.goldenticket.common.utils.DateUtil.daysFromNow;

@Builder
record WishProductResponse(
        Long id,
        Long productId,
        String accommodationName,
        String accommodationImage,
        String roomName,
        ProductStatus status,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        long dDay,
        long nights,
        int yanoljaPrice,
        int originPrice,
        int goldenPrice,
        ReservationType type
) {

    public static WishProductResponse of(WishProduct wishProduct) {
        return WishProductResponse.builder()
                .id(wishProduct.getId())
                .productId(wishProduct.getProduct().getId())
                .accommodationName(wishProduct.getProduct().getAccommodationName())
                .accommodationImage(wishProduct.getProduct().getAccommodationImage())
                .roomName(wishProduct.getProduct().getRoomName())
                .status(wishProduct.getProduct().getProductStatus())
                .checkInDate(wishProduct.getProduct().getCheckInDate())
                .checkOutDate(wishProduct.getProduct().getCheckOutDate())
                .dDay(daysFromNow(wishProduct.getProduct().getCheckInDate()))
                .nights(daysBetween(wishProduct.getProduct().getCheckInDate(), wishProduct.getProduct().getCheckOutDate()))
                .yanoljaPrice(wishProduct.getProduct().getYanoljaPrice())
                .originPrice(wishProduct.getProduct().getOriginPrice())
                .goldenPrice(wishProduct.getProduct().getGoldenPrice())
                .type(wishProduct.getProduct().getReservationType())
                .build();
    }
}
