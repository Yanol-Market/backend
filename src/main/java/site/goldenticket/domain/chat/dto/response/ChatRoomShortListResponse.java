package site.goldenticket.domain.chat.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record ChatRoomShortListResponse(
    List<ChatRoomShortResponse> chatRoomShortList
) {

}
