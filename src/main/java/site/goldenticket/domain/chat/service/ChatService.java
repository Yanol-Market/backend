package site.goldenticket.domain.chat.service;

import static site.goldenticket.common.response.ErrorCode.CHAT_ROOM_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.chat.dto.ChatResponse;
import site.goldenticket.domain.chat.dto.ChatRoomDetailResponse;
import site.goldenticket.domain.chat.dto.ChatRoomInfoResponse;
import site.goldenticket.domain.chat.entity.Chat;
import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.chat.entity.SenderType;
import site.goldenticket.domain.chat.repository.ChatRepository;
import site.goldenticket.domain.chat.repository.ChatRoomRepository;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ProductService productService;
    private final UserService userService;

    public ChatRoomDetailResponse getChatRoomDetail(Long userId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(()-> new CustomException(CHAT_ROOM_NOT_FOUND));
        Product product = productService.findProduct(chatRoom.getProductId());
        Long sellerId = 1L;  //판매자 ID 추후 수정 예정
        Long buyerId = chatRoom.getUserId();
        Long receiverId = (sellerId.equals(userId)) ? buyerId: sellerId; //채팅 상대 ID
        User receiver = userService.findUser(receiverId);

        ChatRoomInfoResponse chatRoomInfoResponse = ChatRoomInfoResponse.builder()
            .chatRoomId(chatRoomId)
            .accommodationName(product.getAccommodationName())
            .roomName(product.getRoomName())
            .receiverProfileImage(receiver.getImageUrl())
            .receiverNickname(receiver.getNickname())
            .price(product.getGoldenPrice()) //네고 승인 시, 네고가격으로 수정하는 로직 추가 예정
            .productId(product.getId())
            .productStatus(product.getProductStatus())
            .build();

        List<Chat> chatList = chatRepository.findAllByChatRoomId(chatRoomId);
        List<ChatResponse> chatResponseList = new ArrayList<>();

        for (Chat chat : chatList) {

            // 시스템 메세지 필터
            if(chat.getSenderType().equals(SenderType.SYSTEM)&&chat.getUserId()!=userId)
                continue;

            ChatResponse chatResponse = ChatResponse.builder()
                .chatId(chat.getId())
                .senderType(chat.getSenderType())
                .userId(chat.getUserId())
                .content(chat.getContent())
                .createdAt(chat.getCreatedAt())
                .build();
            chatResponseList.add(chatResponse);
        }

        return ChatRoomDetailResponse.builder()
            .chatRoomInfoResponse(chatRoomInfoResponse)
            .chatResponseList(chatResponseList).build();
    }
}
