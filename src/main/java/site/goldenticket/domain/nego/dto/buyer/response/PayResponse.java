package site.goldenticket.domain.nego.dto.buyer.response;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class PayResponse {
    private Long id;
    private Long productId;
    private Integer price;
    private Long userId;
    private NegotiationStatus status;
    private LocalDateTime createdAt; // 생성 일시
    private LocalDateTime updatedAt; // 생성 일시



    public static PayResponse fromEntity(Nego nego) {
        return PayResponse.builder()
                .id(nego.getId())
                .price(nego.getPrice())
                .status(nego.getStatus())
                .createdAt(nego.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
