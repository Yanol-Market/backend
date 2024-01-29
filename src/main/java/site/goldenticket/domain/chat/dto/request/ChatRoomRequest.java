package site.goldenticket.domain.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChatRoomRequest(
    @NotNull(message = "구매자 ID를 입력해주세요")
    Long userId,
    @NotNull(message = "상품 ID를 입력해주세요")
    Long productId
) {

}
