package site.goldenticket.domain.nego.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.domain.nego.entity.Nego;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PriceProposeRequest {

    @NotNull
    private Integer price; // 네고 가격
    @NotNull
    private Integer count; // 네고 횟수
    @NotNull
    private Long productId; // 상품 ID 추가
    @NotNull
    private Long userId;

    public Nego toEntity() {
        return Nego.builder()
                .price(price)
                .count(count)
                .createdAt(LocalDateTime.now())
                .productId(productId) // 상품 ID 설정
                .userId((userId))
                .build();

    }
}
