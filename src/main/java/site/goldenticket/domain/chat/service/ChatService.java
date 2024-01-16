package site.goldenticket.domain.chat.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ProductService productService;
    private final UserService userService;

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
                Product product = productService.findProduct(chatRoom.getProductId());
                User receiver = userService.findUser(product.getUserId());
                Chat lastChat = getLastChat(chatRoom.getId(), userId);
                chatRoomShortResponseList.add(ChatRoomShortResponse.builder()
                    .chatRoomId(chatRoom.getId())
                    .receiverNickname(receiver.getNickname())
                    .receiverProfileImage(receiver.getImageUrl())
                    .accommodationName(product.getAccommodationName())
                    .roomName(product.getRoomName())
                    .price(product.getGoldenPrice())
                    .lastMessage(lastChat.getContent())
                    .lastMessageCreatedAt(lastChat.getCreatedAt())
                    .build());
            }
        }

        if (userType.equals("seller")) {
            //내가 판매자. 상대가 구매자.
            List<Product> productList = productService.findProductListByUserId(userId);
            chatRoomList = chatRoomRepository.findAllByUserId(userId);

            for (ChatRoom chatRoom : chatRoomList) {
                User receiver = userService.findUser(chatRoom.getUserId());
                Product product = productService.findProduct(chatRoom.getProductId());
                Chat chat = getLastChat(chatRoom.getId(), userId);
                chatRoomShortResponseList.add(ChatRoomShortResponse.builder()
                    .chatRoomId(chatRoom.getId())
                    .receiverNickname(receiver.getNickname())
                    .receiverProfileImage(receiver.getImageUrl())
                    .accommodationName(product.getAccommodationName())
                    .roomName(product.getRoomName())
                    .price(product.getGoldenPrice())
                    .lastMessage(chat.getContent())
                    .lastMessageCreatedAt(chat.getCreatedAt())
                    .build());
            }
        }
        return chatRoomShortResponseList;
    }

    private Chat getLastChat(Long chatRoomId, Long userId) {
        List<Chat> chatList = chatRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
        //SenderType이 SYSTEM인 경우 userId와 일치하지 않는 chat은 삭제
        chatList.removeIf(
            chat -> chat.getSenderType().equals(SenderType.SYSTEM) && !chat.getUserId()
                .equals(userId));
        //가장 최신 chat 추출
        if (!chatList.isEmpty()) {
            return chatList.get(0);
        } else {
            return null;
        }
    }
}
