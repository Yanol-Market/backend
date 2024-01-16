package site.goldenticket.domain.chat.service;

import static site.goldenticket.common.response.ErrorCode.CHAT_ROOM_NOT_FOUND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.chat.dto.ChatRequest;
import site.goldenticket.domain.chat.dto.ChatResponse;
import site.goldenticket.domain.chat.dto.ChatRoomDetailResponse;
import site.goldenticket.domain.chat.dto.ChatRoomInfoResponse;
import site.goldenticket.domain.chat.dto.ChatRoomListResponse;
import site.goldenticket.domain.chat.dto.ChatRoomShortResponse;
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

    public ChatResponse createChat(Long userId, ChatRequest chatRequest) {
        Chat chat = Chat.builder()
            .chatRoomId(chatRequest.chatRoomId())
            .senderType(chatRequest.senderType())
            .userId(chatRequest.userId())
            .content(chatRequest.content())
            .viewed(false)
            .build();
        chatRepository.save(chat);

        return ChatResponse.builder()
            .chatId(chat.getId())
            .senderType(chat.getSenderType())
            .userId(chat.getUserId())
            .content(chat.getContent())
            .viewed(chat.getViewed())
            .createdAt(chat.getCreatedAt())
            .build();
    }

    public ChatRoomDetailResponse getChatRoomDetail(Long userId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));
        Product product = productService.getProduct(chatRoom.getProductId());
        Long sellerId = product.getUserId();
        Long buyerId = chatRoom.getUserId();
        Long receiverId = (sellerId.equals(userId)) ? buyerId : sellerId; //채팅 상대 ID
        User receiver = userService.findById(receiverId);

        ChatRoomInfoResponse chatRoomInfoResponse = ChatRoomInfoResponse.builder()
            .chatRoomId(chatRoomId)
            .accommodationName(product.getAccommodationName())
            .roomName(product.getRoomName())
            .receiverProfileImage(receiver.getImageUrl())
            .receiverNickname(receiver.getNickname())
            .price(product.getGoldenPrice()) // *네고 승인 시, 네고가격으로 수정하는 로직 추가 예정
            .productId(product.getId())
            .productStatus(product.getProductStatus())
            .build();

        List<Chat> chatList = getChatList(chatRoomId, userId);
        List<ChatResponse> chatResponseList = new ArrayList<>();

        for (Chat chat : chatList) {
            chat.setViewed(true);
            chatRepository.save(chat); // *확인 필요
            ChatResponse chatResponse = ChatResponse.builder()
                .chatId(chat.getId())
                .senderType(chat.getSenderType())
                .userId(chat.getUserId())
                .content(chat.getContent())
                .createdAt(chat.getCreatedAt())
                .viewed(chat.getViewed())
                .build();
            chatResponseList.add(chatResponse);
        }

        return ChatRoomDetailResponse.builder()
            .chatRoomInfoResponse(chatRoomInfoResponse)
            .chatResponseList(chatResponseList).build();
    }

    public ChatRoomListResponse getChatRoomList(Long userId, String userType) {
        List<ChatRoomShortResponse> chatRoomShortResponseList = new ArrayList<>();
        //내가 판매자인 경우 = 내가 판매중인 상품 id 목록 => 상품id가 일치하는 채팅방 목록 조회
        //내가 구매자인 경우 = 채팅방의 구매자 id와 유저id가 일치하는 채팅방 목록 조회
        //모든 채팅 조회 = 판매자인 경우 + 구매자인 경우
        if (userType.equals("all")) {
            chatRoomShortResponseList.addAll(getChatRoomListByUserType(userId, "seller"));
            chatRoomShortResponseList.addAll(getChatRoomListByUserType(userId, "buyer"));
        }
        if (userType.equals("seller") || userType.equals("buyer")) {
            chatRoomShortResponseList = getChatRoomListByUserType(userId, userType);
        }
        Collections.sort(chatRoomShortResponseList,
            Comparator.comparing(ChatRoomShortResponse::lastMessageCreatedAt).reversed());
        return ChatRoomListResponse.builder().
            chatRoomShortList(chatRoomShortResponseList)
            .build();
    }

    private List<ChatRoomShortResponse> getChatRoomListByUserType(Long userId, String userType) {
        List<ChatRoomShortResponse> chatRoomShortResponseList = new ArrayList<>();
        List<ChatRoom> chatRoomList;

        if (userType.equals("buyer")) {
            //내가 구매자. 상대가 판매자.
            chatRoomList = chatRoomRepository.findAllByUserId(userId);
            for (ChatRoom chatRoom : chatRoomList) {
                Product product = productService.getProduct(chatRoom.getProductId());
                User receiver = userService.findById(product.getUserId());
                // *채팅 내역이 비어 있을 경우 예외 처리 추가 예정
                Chat lastChat = getChatList(chatRoom.getId(), userId).get(0);
                chatRoomShortResponseList.add(ChatRoomShortResponse.builder()
                    .chatRoomId(chatRoom.getId())
                    .receiverNickname(receiver.getNickname())
                    .receiverProfileImage(receiver.getImageUrl())
                    .accommodationName(product.getAccommodationName())
                    .roomName(product.getRoomName())
                    .lastMessage(lastChat.getContent())
                    .lastMessageCreatedAt(lastChat.getCreatedAt())
                    .viewed(lastChat.getViewed())
                    .build());
            }
        }

        if (userType.equals("seller")) {
            //내가 판매자. 상대가 구매자.
            List<Product> productList = productService.findProductListByUserId(userId);
            chatRoomList = chatRoomRepository.findAllByUserId(userId);

            for (ChatRoom chatRoom : chatRoomList) {
                User receiver = userService.findById(chatRoom.getUserId());
                Product product = productService.getProduct(chatRoom.getProductId());
                Chat lastChat = getChatList(chatRoom.getId(), userId).get(0);
                chatRoomShortResponseList.add(ChatRoomShortResponse.builder()
                    .chatRoomId(chatRoom.getId())
                    .receiverNickname(receiver.getNickname())
                    .receiverProfileImage(receiver.getImageUrl())
                    .accommodationName(product.getAccommodationName())
                    .roomName(product.getRoomName())
                    .lastMessage(lastChat.getContent())
                    .lastMessageCreatedAt(lastChat.getCreatedAt())
                    .viewed(lastChat.getViewed())
                    .build());
            }
        }
        return chatRoomShortResponseList;
    }

    private List<Chat> getChatList(Long chatRoomId, Long userId) {
        List<Chat> chatList = chatRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
        //SenderType이 SYSTEM인 경우 userId와 일치하지 않는 chat은 삭제
        chatList.removeIf(
            chat -> chat.getSenderType().equals(SenderType.SYSTEM) && !chat.getUserId()
                .equals(userId));

        return chatList;
    }
}
