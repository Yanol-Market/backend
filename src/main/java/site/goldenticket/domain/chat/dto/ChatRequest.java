package site.goldenticket.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatRequest(
    @NotNull(message = "채팅방 ID를 입력해주세요")
    Long chatRoomId,
    @NotBlank(message = "송신자 타입을 입력해주세요: 판매자(SELLER)/구매자(BUYER)/시스템(SYSTEM)")
    String senderType,
    @NotNull(message = "채팅 작성자 ID를 입력해주세요")
    Long userId,
    @NotBlank(message = "채팅 내용을 입력해주세요")
    String content
) {

}
