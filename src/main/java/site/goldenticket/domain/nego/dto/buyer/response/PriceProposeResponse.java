package site.goldenticket.domain.nego.dto.buyer.response;

import lombok.*;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class PriceProposeResponse {
    private Long id;

    private Long productId;

    private Integer price; // 네고 가격

    private Integer count; // 네고 횟수

    private NegotiationStatus status; // 네고 상태

    private LocalDateTime createdAt; // 생성 일시

    private LocalDateTime expirationTime; // 만료 일시


    public static PriceProposeResponse fromEntity(Nego nego) {
        return PriceProposeResponse.builder()
                .id(nego.getId())
                .productId(nego.getProductId())
                .count(nego.getCount())
                .price(nego.getPrice())
                .status(nego.getStatus())
                .createdAt(nego.getCreatedAt())
                .expirationTime(nego.getExpirationTime())
                .build();
    }
}
