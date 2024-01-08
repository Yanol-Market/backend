package site.goldenticket.domain.nego.dto.buyer.response;

import lombok.*;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PricePurposeResponse {
    private Long id;

    private Integer price; // 네고 가격

    private Integer count; // 네고 횟수

    private NegotiationStatus status; // 네고 상태

    private LocalDateTime createdAt; // 생성 일시


    public static PricePurposeResponse fromEntity(Nego nego) {
        return PricePurposeResponse.builder()
                .id(nego.getId())
                .count(nego.getCount())
                .price(nego.getPrice())
                .status(nego.getStatus())
                .createdAt(nego.getCreatedAt())
                .build();
    }
}
