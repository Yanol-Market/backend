package site.goldenticket.domain.nego.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.chat.service.ChatService;
import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.HandoverResponse;
import site.goldenticket.domain.nego.dto.response.NegoAvailableResponse;
import site.goldenticket.domain.nego.dto.response.NegoResponse;
import site.goldenticket.domain.nego.dto.response.PayResponse;
import site.goldenticket.domain.nego.dto.response.PriceProposeResponse;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NegoServiceImpl implements NegoService {

    private final NegoRepository negoRepository;
    private final ProductService productService;
    private final UserRepository userRepository;
    private final ChatService chatService;

    @Override
    public NegoResponse confirmPrice(Long negoId, PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Nego nego = negoRepository.findById(negoId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + negoId));

        if (nego.getStatus() == NegotiationStatus.NEGOTIATING) {
            nego.setUpdatedAt(LocalDateTime.now());
            nego.setStatus(NegotiationStatus.PAYMENT_PENDING);
            nego.setExpirationTime(LocalDateTime.now().plusMinutes(20));
            nego.setConsent(Boolean.TRUE);
            negoRepository.save(nego);

        } else {
            // 다른 상태의 네고는 가격 승낙을 처리할 수 없음
            throw new CustomException("네고를 승인할수 없습니다.", ErrorCode.COMMON_CANNOT_CONFIRM_NEGO);
        }
        if (nego.getCount() > 3) {
            throw new CustomException("네고를 승인할수 없습니다.", ErrorCode.COMMON_CANNOT_CONFIRM_NEGO);
        }
        return NegoResponse.fromEntity(nego);
    }


    @Override
    public NegoResponse denyPrice(Long negoId, PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Nego nego = negoRepository.findById(negoId)
            .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        if (nego.getStatus() == NegotiationStatus.NEGOTIATING
            || nego.getStatus() == NegotiationStatus.NEGOTIATION_CANCELLED) {
            nego.setUpdatedAt(LocalDateTime.now());
            nego.setConsent(Boolean.FALSE);
            nego.setUpdatedAt(LocalDateTime.now());

            // 네고 취소 상태로 변경
            if (nego.getCount() == 2) {
                nego.setStatus(NegotiationStatus.NEGOTIATION_CANCELLED);
            }

            negoRepository.save(nego);  // 네고 업데이트
            return NegoResponse.fromEntity(nego);
        } else {
            // NEGOTIATING 상태가 아닌 경우 거절 처리 불가
            throw new CustomException("네고 중인 경우에만 거절할 수 있습니다.",
                ErrorCode.COMMON_ONLY_CAN_DENY_WHEN_NEGOTIATING);
        }
    }


    // 가격제안은 productId를 받아서 사용할 예정 아래는 임시!
    public PriceProposeResponse proposePrice(Long productId, PriceProposeRequest request,
        PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Product product = productService.getProduct(productId);
        Nego nego = negoRepository.findByProduct(product);

        // 사용자별 상품에 대한 네고 조회
        Nego userNego = negoRepository.findByUserAndProduct(user, product)
            .orElse(new Nego(user, product)); // 네고가 없으면 새로 생성

        if (nego != null && nego.getStatus() == NegotiationStatus.NEGOTIATION_COMPLETED) {
            throw new CustomException("승인된 네고는 가격 제안을 할 수 없습니다.",
                ErrorCode.COMMON_NEGO_ALREADY_APPROVED);
        }

        if (userNego.getStatus() == NegotiationStatus.NEGOTIATION_TIMEOUT) {
            throw new CustomException("20분이 지나 제안할수 없습니다.", ErrorCode.COMMON_NEGO_TIMEOUT);
        }

        // count 증가
        int newCount = userNego.getCount() + 1;

        // 여기서 count가 3인 경우 예외 처리
        if (newCount > 2) {
            throw new CustomException("더 이상 네고할 수 없습니다.", ErrorCode.COMMON_CANNOT_NEGOTIATE);
        }

        // 네고 엔터티 업데이트
        userNego.setCount(newCount);
        userNego.setPrice(request.getPrice());
        userNego.setStatus(NegotiationStatus.NEGOTIATING);
        userNego.setCreatedAt(LocalDateTime.now());
        userNego.setUpdatedAt(LocalDateTime.now());
        userNego.setConsent(Boolean.FALSE);

        // 네고 저장
        negoRepository.save(userNego);

        return PriceProposeResponse.fromEntity(userNego);
    }

    @Override
    public PayResponse pay(Long negoId, PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));
        Nego nego = negoRepository.findById(negoId)
            .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        if (nego.getConsent()) {
            nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
            nego.setUpdatedAt(LocalDateTime.now());
            negoRepository.save(nego);

            return PayResponse.fromEntity(nego);
        } else {
            throw new CustomException("네고 승인이 필요합니다.", ErrorCode.COMMON_NEGO_APPROVAL_REQUIRED);
        }
    }

    @Override
    public PayResponse payOriginPrice(Long negoId, PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));
        Nego nego = negoRepository.findById(negoId)
            .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        Integer originPrice = nego.getProduct().getOriginPrice();

        // 네고의 가격을 상품의 원래 가격으로 업데이트
        nego.setPrice(originPrice);

        // 네고 상태를 완료로 변경
        nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
        nego.setConsent(Boolean.TRUE);
        nego.setUpdatedAt(LocalDateTime.now());
        negoRepository.save(nego);

        return PayResponse.fromEntity(nego);

    }

    @Override
    public HandoverResponse handOverProduct(Long negoId, PrincipalDetails principalDetails) {
        // Nego ID로 Nego 정보 가져오기

        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Nego nego = negoRepository.findById(negoId)
            .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        // Nego의 Product ID로 Product 정보 가져오기
        Product product = productService.getProduct(nego.getProductId());

        // 상태가 결제 완료인 경우에만 양도 가능
        if (nego.getStatus() == NegotiationStatus.NEGOTIATION_COMPLETED) {

            product.setProductStatus(ProductStatus.SOLD_OUT);

            // 양도 작업이 완료된 경우에는 양도 정보와 함께 반환
            return HandoverResponse.fromEntity(product, nego);
        } else {
            // 양도 불가능한 상태인 경우 예외 처리
            throw new CustomException("양도가 불가능한 상태입니다.", ErrorCode.COMMON_CANNOT_HANDOVER);
        }
    }

    @Override
    public Optional<Nego> getNego(Long userId, Long productId) {
        return negoRepository.findFirstByUser_IdAndProduct_IdOrderByCreatedAtDesc(userId,
            productId);
    }

    @Override
    public Optional<Nego> getUserNego(Long userId) {
        return negoRepository.findNegoByUser_Id(userId);
    }

    @Override
    public Nego save(Nego nego) {
        return negoRepository.save(nego);
    }

    /***
     * 네고 가능 여부 조회
     * @param userId 회원 ID
     * @param productId 상품 ID
     * @return 네고 가능 여부 응답 DTO (네고 가능 여부, 네고 가능 시, 채팅방 ID)
     */
    public NegoAvailableResponse isAvailableNego(Long userId, Long productId) {
        Boolean negoAvailable = true;
        Long chatRoomId = -1L;
        Product product = productService.getProduct(productId);
        //본인이 판매하는 상품이면 네고 불가
        if (product.getUserId().equals(userId)) {
            negoAvailable = false;
        }
        //판매중인 상품인지 확인: 판매중이 아니면 네고 불가
        if (!product.getProductStatus().equals(ProductStatus.SELLING)) {
            negoAvailable = false;

        } else {
            if (!negoRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
                //네고 이력 없는 경우 : 채팅방 생성 + 네고 가능
                if (!chatService.existsChatRoomByUserIdAndProductId(userId, productId)) {
                    ChatRoom chatRoom = chatService.createChatRoom(userId, productId);
                    negoAvailable = true;
                    chatRoomId = chatRoom.getId();
                    System.out.println(chatRoomId);
                }
            } else {
                //네고 이력 있는 경우 : 2차 네고(거절 혹은 승인) OR 재결제 -> 네고 불가
                List<Nego> negoList = negoRepository.findAllByUser_IdAndProduct_Id(userId,
                    productId);
                for (Nego nego : negoList) {
                    if (nego.getCount().equals(2) || nego.getStatus()
                        .equals(NegotiationStatus.NEGOTIATION_TIMEOUT)) {
                        negoAvailable = false;
                        break;
                    }
                }
            }
        }
        if (negoAvailable) {
            chatRoomId = chatService.getChatRoomByBuyerIdAndProductId(userId, productId).getId();
        }
        return NegoAvailableResponse.builder()
            .negoAvailable(negoAvailable)
            .chatRoomId(chatRoomId)
            .build();
    }
}
