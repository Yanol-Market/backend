package site.goldenticket.domain.nego.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.nego.entity.Nego;

import java.time.LocalDateTime;

@Getter
@Builder

public class PriceProposeRequest {

    @NotNull
    private Integer price; // 네고 가격
    @NotNull
    private Integer count; // 네고 횟수

    public Nego toEntity(){
        return Nego.builder()
                .price(price)
                .count(count)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
