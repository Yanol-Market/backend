package site.goldenticket.domain.nego.dto.response;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.common.constants.ReservationType;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.product.model.Product;

import java.time.LocalDate;

@Getter
@Builder
public class HandoverResponse {
    private String accommodationImage;
    private String accommodationName;
    private ReservationType reservationType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer price;

    public static HandoverResponse fromEntity(Product product, Nego nego){
        return HandoverResponse.builder()
                .accommodationImage(product.getAccommodationImage())
                .accommodationName(product.getAccommodationName())
                .reservationType(product.getReservationType())
                .checkInDate(product.getCheckInDate())
                .checkOutDate(product.getCheckOutDate())
                .price(nego.getPrice())
                .build();

    }

}
