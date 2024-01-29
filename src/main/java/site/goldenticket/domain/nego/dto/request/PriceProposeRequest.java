package site.goldenticket.domain.nego.dto.request;

import jakarta.validation.constraints.NotNull;


public record PriceProposeRequest(
        @NotNull Integer price,
        @NotNull Integer count,
        @NotNull Long productId,
        @NotNull Long userId
) {
    @Override
    public Integer price() {
        return price;
    }

}