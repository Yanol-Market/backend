package site.goldenticket.domain.chat.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record ChatRoomDetailResponse(
    ChatRoomInfoResponse chatRoomInfoResponse,
    List<ChatResponse> chatResponseList
) {

}
