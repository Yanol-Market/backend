package site.goldenticket.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.chat.dto.ChatRoomDetailResponse;
import site.goldenticket.domain.chat.service.ChatService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<CommonResponse<ChatRoomDetailResponse>> getChatRoom(
        @PathVariable Long chatRoomId) {
        Long userId = 1L; //시큐리티 적용 후 수정 예정
        return ResponseEntity.ok(CommonResponse.ok("채팅방 상세 정보를 조회했습니다",
            chatService.getChatRoomDetail(userId, chatRoomId)));
    }

}
