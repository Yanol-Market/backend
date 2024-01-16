package site.goldenticket.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.chat.dto.ChatRoomListResponse;
import site.goldenticket.domain.chat.service.ChatService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping()
    public ResponseEntity<CommonResponse<ChatRoomListResponse>> getChatRoomList(
        @RequestParam String userType) {
        Long userId = 1L; // 시큐리티 적용 후 수정 예정
        return ResponseEntity.ok(CommonResponse.ok("채팅방 목록(거래 내역)이 조회되었습니다",
            chatService.getChatRoomList(userId, userType)));
    }
}
