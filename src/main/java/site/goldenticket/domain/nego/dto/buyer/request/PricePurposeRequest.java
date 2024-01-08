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
    private Integer count; // 네고 횟수

    public Nego toEntity(){
        if (count == null) {
            count = 1; // 네고 횟수를 지정하지 않으면 기본값으로 1로 설정
        }

        return Nego.builder()
                .price(price)
                .count(count)
                .createdAt(LocalDateTime.now())
                .build();
    }
    public void increaseCount() {
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
    }
}
