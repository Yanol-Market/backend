package site.goldenticket.domain.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.chat.dto.ChatRequest;
import site.goldenticket.domain.chat.dto.ChatResponse;
import site.goldenticket.domain.chat.dto.ChatRoomDetailResponse;
import site.goldenticket.domain.chat.dto.ChatRoomListResponse;
import site.goldenticket.domain.chat.service.ChatService;
import site.goldenticket.domain.security.PrincipalDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/test")
    public ResponseEntity<CommonResponse<ChatResponse>> createChatForTest(
        @Valid @RequestBody ChatRequest chatRequest
    ) {
        return ResponseEntity.ok(
            CommonResponse.ok("새로운 채팅이 저장되었습니다.",
                chatService.createChat(chatRequest)));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<CommonResponse<ChatRoomDetailResponse>> getChatRoom(
        @PathVariable(name = "chatRoomId") Long chatRoomId,
        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(CommonResponse.ok("채팅방 상세 정보를 조회했습니다",
            chatService.getChatRoomDetail(principalDetails.getUserId(), chatRoomId)));
    }

    @GetMapping()
    public ResponseEntity<CommonResponse<ChatRoomListResponse>> getChatRoomList(
        @RequestParam(name = "userType") String userType,
        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(CommonResponse.ok("채팅방 목록(거래 내역)이 조회되었습니다",
            chatService.getChatRoomList(principalDetails.getUserId(), userType)));
    }
}
