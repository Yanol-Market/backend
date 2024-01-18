package site.goldenticket.domain.chat.service;

import static site.goldenticket.common.response.ErrorCode.CHAT_ROOM_NOT_FOUND;
import static site.goldenticket.common.response.ErrorCode.INVALID_SENDER_TYPE;
import static site.goldenticket.common.response.ErrorCode.INVALID_USER_TYPE;
import static site.goldenticket.common.response.ErrorCode.NEGO_NOT_FOUND;
import static site.goldenticket.common.response.ErrorCode.ORDER_NOT_FOUND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.constants.OrderStatus;
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
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.repository.OrderRepository;
import site.goldenticket.domain.product.constants.ProductStatus;
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
    private final NegoRepository negoRepository;
    private final OrderRepository orderRepository;

    /***
     * 채팅 생성
     * @param chatRequest 채팅 생성 요청 DTO
     * @return 채팅 응답 DTO
     */
    public ChatResponse createChat(ChatRequest chatRequest) {
        if (!chatRequest.senderType().equals(SenderType.SYSTEM)
            && !chatRequest.senderType().equals(SenderType.BUYER)
            && !chatRequest.senderType().equals(SenderType.SELLER)) {
            throw new CustomException(INVALID_SENDER_TYPE);
        }
        // *존재하는 채팅방 ID인지 확인하는 로직 추가 예정
        Chat chat = Chat.builder()
            .chatRoomId(chatRequest.chatRoomId())
            .senderType(chatRequest.senderType())
            .userId(chatRequest.userId())
            .content(chatRequest.content())
            .viewedBySeller(false)
            .viewedByBuyer(false)
            .build();
        chatRepository.save(chat);

        return ChatResponse.builder()
            .chatId(chat.getId())
            .senderType(chat.getSenderType())
            .userId(chat.getUserId())
            .content(chat.getContent())
            .viewed(false)
            .createdAt(chat.getCreatedAt())
            .build();
    }

    /***
     * 채팅방 생성
     * @param userId 구매자 ID
     * @param productId 상품 ID
     * @return 생성된 채팅방 Entity
     */
    public ChatRoom createChatRoom(Long userId, Long productId) {
        return chatRoomRepository.save(
            ChatRoom.builder()
                .userId(userId)
                .productId(productId)
                .build()
        );
    }

    /***
     * 채팅방 상세 조회 (상품 및 상대방 정보 + 채팅내역)
     * @param userId 회원 ID
     * @param chatRoomId 채팅방 ID
     * @return 채팅방 상세 응답 DTO
     */
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
            .productId(product.getId())
            .accommodationName(product.getAccommodationName())
            .roomName(product.getRoomName())
            .accommodationImage(product.getAccommodationImage())
            .checkInDate(product.getCheckInDate())
            .checkOutDate(product.getCheckOutDate())
            .checkInTime(product.getCheckInTime())
            .checkOutTime(product.getCheckOutTime())
            .receiverProfileImage(receiver.getImageUrl())
            .receiverNickname(receiver.getNickname())
            // * .price(getPriceOfChatRoom(buyerId, product.getId()))
            .price(product.getGoldenPrice())
            .productStatus(product.getProductStatus())
            .build();

        List<Chat> chatList = getChatList(chatRoomId, userId);
        List<ChatResponse> chatResponseList = new ArrayList<>();

        for (Chat chat : chatList) {
            if (sellerId.equals(userId)) {
                chat.setViewedBySeller(true);
            } else {
                chat.setViewedByBuyer(true);
            }
            chatRepository.save(chat); // *확인 필요
            ChatResponse chatResponse = ChatResponse.builder()
                .chatId(chat.getId())
                .senderType(chat.getSenderType())
                .userId(chat.getUserId())
                .content(chat.getContent())
                .createdAt(chat.getCreatedAt())
                .viewed(true)
                .build();
            chatResponseList.add(chatResponse);
        }

        return ChatRoomDetailResponse.builder()
            .chatRoomInfoResponse(chatRoomInfoResponse)
            .chatResponseList(chatResponseList).build();
    }

    /***
     * 채팅방 상단 가격값 조회
     * @param buyerId 구매자 ID
     * @param productId 상품 ID
     * @return 채팅방 상단에 띄울 가격값
     */
    private Integer getPriceOfChatRoom(Long buyerId, Long productId) {
        Product product = productService.getProduct(productId);
        Integer price = product.getGoldenPrice();

        Boolean existsNego = negoRepository.existsByUser_IdAndProduct_Id(buyerId, product.getId());
        if (product.getProductStatus().equals(ProductStatus.SELLING) && existsNego) {
            Nego nego = negoRepository.findFirstByUser_IdAndProduct_IdOrderByCreatedAtDesc(buyerId,
                product.getId()).orElseThrow(() -> new CustomException(NEGO_NOT_FOUND));
            if (nego.getStatus().equals(NegotiationStatus.NEGOTIATION_TIMEOUT)) {
                price = nego.getPrice();
            }
        } else if (product.getProductStatus().equals(ProductStatus.RESERVED) && existsNego) {
            Nego nego = negoRepository.findFirstByUser_IdAndProduct_IdOrderByCreatedAtDesc(buyerId,
                product.getId()).orElseThrow(() -> new CustomException(NEGO_NOT_FOUND));
            if (nego.getStatus().equals(NegotiationStatus.PAYMENT_PENDING)) {
                price = nego.getPrice();
            }
        } else if (product.getProductStatus().equals(ProductStatus.SOLD_OUT)) {
            Order order = orderRepository.findByProductIdAndStatus(product.getId(),
                    OrderStatus.COMPLETED_TRANSFER)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
            price = order.getPrice();
        }
        return price;
    }

    /***
     * 채팅방 목록(거래내역) 조회
     * @param userId 회원 ID
     * @param userType 회원타입: all(전체), seller(판매내역), buyer(구매내역)
     * @return 채팅방 목록 응답 DTO
     */
    public ChatRoomListResponse getChatRoomList(Long userId, String userType) {
        List<ChatRoomShortResponse> chatRoomShortResponseList = new ArrayList<>();
        //모든 채팅 조회 = 판매자인 경우 + 구매자인 경우
        if (userType.equals("all")) {
            chatRoomShortResponseList.addAll(getChatRoomListByUserType(userId, "seller"));
            chatRoomShortResponseList.addAll(getChatRoomListByUserType(userId, "buyer"));
        } else if (userType.equals("seller") || userType.equals("buyer")) {
            chatRoomShortResponseList = getChatRoomListByUserType(userId, userType);
        } else {
            throw new CustomException(INVALID_USER_TYPE);
        }
        Collections.sort(chatRoomShortResponseList,
            Comparator.comparing(ChatRoomShortResponse::lastMessageCreatedAt).reversed());
        return ChatRoomListResponse.builder().
            chatRoomShortList(chatRoomShortResponseList)
            .build();
    }

    /***
     * 회원 타입별 채팅방 목록 조회
     * @param userId 회원 ID
     * @param userType 회원 타입: all(전체), seller(판매내역), buyer(구매내역)
     * @return 채팅방 요약 응답 DTO List
     */
    private List<ChatRoomShortResponse> getChatRoomListByUserType(Long userId, String userType) {
        List<ChatRoomShortResponse> chatRoomShortResponseList = new ArrayList<>();
        List<ChatRoom> chatRoomList = new ArrayList<>();

        //내가 구매자인 경우 = 채팅방의 구매자 id와 유저id가 일치하는 채팅방 목록 조회
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
                    .viewed(lastChat.getViewedByBuyer())
                    .build());
            }
        }

        //내가 판매자인 경우 = 내가 판매중인 상품 id 목록 => 상품id가 일치하는 채팅방 목록 조회
        if (userType.equals("seller")) {
            //내가 판매자. 상대가 구매자.
            List<Product> productList = productService.findProductListByUserId(userId);
            for (Product product : productList) {
                chatRoomList.addAll(chatRoomRepository.findAllByProductId(product.getId()));
            }

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
                    .viewed(lastChat.getViewedBySeller())
                    .build());
            }
        }
        return chatRoomShortResponseList;
    }

    /***
     * 채팅 목록 조회
     * @param chatRoomId 채팅방 ID
     * @param userId 회원 ID
     * @return 채팅 Entity List
     */
    private List<Chat> getChatList(Long chatRoomId, Long userId) {
        List<Chat> chatList = chatRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
        //SenderType이 SYSTEM인 경우 userId와 일치하지 않는 chat은 삭제
        chatList.removeIf(
            chat -> chat.getSenderType().equals(SenderType.SYSTEM) && !chat.getUserId()
                .equals(userId));

        return chatList;
    }

    /***
     * 채팅방 조회
     * @param buyerId 구매자 ID
     * @param productId 상품 ID
     * @return 채팅방 Entity
     */
    public ChatRoom getChatRoomByBuyerIdAndProductId(Long buyerId, Long productId) {
        return chatRoomRepository.findByUserIdAndProductId(buyerId, productId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));
    }

    /***
     * 채팅방 존재 여부 조회
     * @param userId 구매자 ID
     * @param productId 상품 ID
     * @return
     */
    public Boolean existsChatRoomByUserIdAndProductId(Long userId, Long productId) {
        return chatRoomRepository.existsByUserIdAndProductId(userId, productId);
    }
}
