package site.goldenticket.domain.nego.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.alert.service.AlertService;
import site.goldenticket.domain.chat.service.ChatService;
import site.goldenticket.domain.nego.dto.request.PriceProposeRequest;
import site.goldenticket.domain.nego.dto.response.*;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NegoServiceImpl implements NegoService {

    private final NegoRepository negoRepository;
    private final ProductService productService;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final AlertService alertService;

    @Override
    public NegoResponse confirmPrice(Long negoId, PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Nego nego = negoRepository.findById(negoId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + negoId));

        Product product = productService.getProduct(nego.getProductId());

        if (!(product.getProductStatus() == ProductStatus.SELLING)) {
            throw new CustomException("승인한 다른 네고가 있어 승인할수 없습니다.", ErrorCode.NEGO_ALREADY_CONFIRMED);
        }

        if (nego.getStatus() == NegotiationStatus.NEGOTIATING) {
            nego.confirmNego(LocalDateTime.now(), NegotiationStatus.PAYMENT_PENDING,
                LocalDateTime.now().plusMinutes(20), Boolean.TRUE);
            product.setProductStatus(ProductStatus.RESERVED);
            productService.updateProductForNego(product);
            negoRepository.save(nego);

            //구매자에게 네고 승인 및 결제 안내 알림 전송
            alertService.createAlert(nego.getUser().getId(),
                "'" + nego.getProduct().getAccommodationName() +
                    "(" + nego.getProduct().getRoomName() + ")'상품에 대한 네고 요청이 승인되었습니다. 해당상품을 "
                    + nego.getExpirationTime() + "까지 결제를 완료해주세요.");
        } else {
            // 다른 상태의 네고는 가격 승낙을 처리할 수 없음
            throw new CustomException("네고를 승인할수 없습니다.", ErrorCode.CANNOT_CONFIRM_NEGO);
        }
        if (nego.getCount() > 3) {
            throw new CustomException("네고를 3회이상 제안하실수 없습니다.", ErrorCode.NEGO_COUNT_OVER);
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

            // 네고 취소 상태로 변경
            if (nego.getCount() == 2) {
                nego.setStatus(NegotiationStatus.NEGOTIATION_CANCELLED);
            }

            negoRepository.save(nego);  // 네고 업데이트

            //구매자에게 네고 거절 알림 전송
            alertService.createAlert(nego.getUser().getId(),
                "'" + nego.getProduct().getAccommodationName() +
                    "(" + nego.getProduct().getRoomName() + ")'상품에 대한 네고 요청이 거절되었습니다.");

            return NegoResponse.fromEntity(nego);
        } else {
            // NEGOTIATING 상태가 아닌 경우 거절 처리 불가
            throw new CustomException("네고 중인 경우에만 거절할 수 있습니다.",
                ErrorCode.ONLY_CAN_DENY_WHEN_NEGOTIATING);
        }
    }

    public PriceProposeResponse proposePrice(Long productId, PriceProposeRequest request,
        PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Product product = productService.getProduct(productId);

        List<Nego> allNegosForProduct = negoRepository.findAllByProduct(product);
        for (Nego nego : allNegosForProduct) {
            if (nego != null && nego.getProduct().getProductStatus() == ProductStatus.SOLD_OUT) {
                throw new CustomException("다른 유저가 네고를 성공해 제안할수 없습니다.", ErrorCode.NEGO_COMPLETED);
            }
            if (nego != null && nego.getStatus() == NegotiationStatus.TRANSFER_PENDING) {
                throw new CustomException("다른 유저가 네고를 성공해 제안할수 없습니다.", ErrorCode.NEGO_COMPLETED);
            }   //OK

            if (nego != null && nego.getStatus() == NegotiationStatus.PAYMENT_PENDING) {
                throw new CustomException("승인된 네고는 가격 제안을 할 수 없습니다.",
                    ErrorCode.NEGO_ALREADY_APPROVED);
            }   //OK
        }

        // 사용자별 상품에 대한 네고 조회
        Nego userNego = negoRepository.findByUserAndProduct(user, product)
            .orElse(new Nego(user, product)); // 네고가 없으면 새로 생성

        if (userNego.getProduct().getProductStatus() == ProductStatus.SOLD_OUT) {
            throw new CustomException("승인된 네고는 가격 제안을 할 수 없습니다.", ErrorCode.NEGO_ALREADY_APPROVED);
        }

        if (userNego.getStatus() == NegotiationStatus.NEGOTIATION_CANCELLED) {
            throw new CustomException("취소된 네고는 가격 제안을 할 수 없습니다.", ErrorCode.CANNOT_NEGOTIATE);
        }

        if (userNego.getStatus() == NegotiationStatus.NEGOTIATION_TIMEOUT) {
            throw new CustomException("20분이 지나 제안할수 없습니다.", ErrorCode.CANNOT_PROPOSE_NEGO);
        }

        // count 증가
        int newCount = userNego.getCount() + 1;

        // 여기서 count가 3인 경우 예외 처리
        if (newCount > 2) {
            throw new CustomException("더 이상 네고할 수 없습니다.", ErrorCode.CANNOT_NEGOTIATE);
        }

        // 네고 엔터티 업데이트
        userNego.updateNego(newCount, request.price(), NegotiationStatus.NEGOTIATING,
            LocalDateTime.now(), LocalDateTime.now(), Boolean.FALSE);

        // 네고 저장
        negoRepository.save(userNego);

        //판매자에게 네고 제안 알림 전송
        alertService.createAlert(product.getUserId(),
            "'" + product.getAccommodationName() + "(" + product.getRoomName()
                + ")'상품에 대한 네고 요청이 들어왔습니다.");

        return PriceProposeResponse.fromEntity(userNego);
    }

    @Override
    public PayResponse pay(Long negoId, PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Nego nego = negoRepository.findById(negoId)
            .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        Product product = productService.getProduct(nego.getProductId());

        if (nego.getStatus() == NegotiationStatus.NEGOTIATION_COMPLETED
            || nego.getStatus() == NegotiationStatus.TRANSFER_PENDING) {
            throw new CustomException("다른 유저가 네고를 성공해 제안할수 없습니다.", ErrorCode.NEGO_COMPLETED);
        }

        if (nego.getConsent()) {
            product.setProductStatus(ProductStatus.RESERVED);
            productService.updateProductForNego(product);
            nego.setStatus(NegotiationStatus.TRANSFER_PENDING);
            nego.setUpdatedAt(LocalDateTime.now());
            negoRepository.save(nego);

            return PayResponse.fromEntity(nego);
        } else {
            throw new CustomException("네고 승인이 필요합니다.", ErrorCode.NEGO_APPROVAL_REQUIRED);
        }
    }

    @Override
    public HandoverResponse handOverProduct(Long negoId, PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + userId));

        Nego nego = negoRepository.findById(negoId)
            .orElseThrow(() -> new NoSuchElementException("해당 ID의 네고를 찾을 수 없습니다: " + negoId));

        // Nego의 Product ID로 Product 정보 가져오기
        Product product = productService.getProduct(nego.getProductId());

        // 상태가 양도 대기인 경우에만 양도 가능
        List<Nego> allNegosForProduct = negoRepository.findAllByProduct(product);

        // 상태가 양도 대기인 경우에만 양도 가능
        if (nego.getStatus() == NegotiationStatus.TRANSFER_PENDING) {
            nego.setUpdatedAt(LocalDateTime.now());
            nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
            product.setProductStatus(ProductStatus.SOLD_OUT);
            product.setGoldenPrice(nego.getPrice());
            productService.updateProductForNego(product);

            // 해당 Product에 대한 모든 네고 상태를 변경
            for (Nego otherNego : allNegosForProduct) {
                // 다른 네고는 양도 완료 상태로 변경
                otherNego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
                negoRepository.save(otherNego);
            }

            //구매자에게 양도 완료 알림 전송
            alertService.createAlert(nego.getUser().getId(),
                "'" + product.getAccommodationName() + "(" + product.getRoomName()
                    + ")'상품 양도가 완료되었습니다. "
                    + "양도 완료에 따른 체크인 정보는 '마이페이지 > 구매내역 > 구매 완료'에서 확인하실 수 있습니다.");
            //판매자에게 정산 요청 알림 전송
            alertService.createAlert(product.getUserId(),
                "'" + product.getAccommodationName() + "(" + product.getRoomName()
                    + ")'상품 양도가 완료되었습니다. 영업일 1일 이내 등록한 계좌 정보로 정산 금액이 입금됩니다."
                    + "원활한 정산 진행을 위해 '마이페이지 - 나의 계좌'정보를 다시 한번 확인해주세요.");

            // 양도 작업이 완료된 경우에는 양도 정보와 함께 반환
            return HandoverResponse.fromEntity(product, nego);
        } else {
            // 양도 불가능한 상태인 경우 예외 처리
            throw new CustomException("양도가 불가능한 상태입니다.", ErrorCode.CANNOT_HANDOVER);
        }
    }

    @Override
    public NegoResponse denyHandoverProduct(Long negoId, PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUserId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        Nego nego = negoRepository.findById(negoId)
            .orElseThrow(() -> new NoSuchElementException("Nego not found with id: " + negoId));

        Product product = productService.getProduct(nego.getProductId());

        if (nego.getStatus() == NegotiationStatus.TRANSFER_PENDING) {
            if (nego.getCount() < 2) {
                nego.setStatus(NegotiationStatus.NEGOTIATING);
            } else {
                nego.setStatus(NegotiationStatus.NEGOTIATION_COMPLETED);
            }
            nego.setConsent(Boolean.FALSE);
            nego.setExpirationTime(LocalDateTime.now());
            product.setProductStatus(ProductStatus.SELLING);
            productService.updateProductForNego(product);
            negoRepository.save(nego);
            return NegoResponse.fromEntity(nego);
        } else {
            throw new CustomException("Nego not in completed status.",
                ErrorCode.NEGO_NOT_COMPLETED);
        }
    }


    @Override
    public Optional<Nego> getNego(Long userId, Long productId) {
        return negoRepository.findFirstByUser_IdAndProduct_IdOrderByCreatedAtDesc(userId,
            productId);
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
                if (!chatService.existsChatRoomByBuyerIdAndProductId(userId, productId)) {
                    chatService.createChatRoom(userId, productId);
                    negoAvailable = true;
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
