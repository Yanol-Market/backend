package site.goldenticket.domain.nego.dto.buyer.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.domain.nego.entity.Nego;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PricePurposeRequest {

    @NotNull
    private Integer price; // 네고 가격
    @NotNull
    private Long userId;
    @NotNull
    private Long productId;
    @NotNull
    private Integer count; // 네고 횟수

    public Nego toEntity(){
        return Nego.builder()
                .price(price)
                .count(count)
                .productId(productId)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
