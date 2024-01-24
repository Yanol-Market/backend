package site.goldenticket.domain.alert.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.domain.alert.dto.AlertListResponse;
import site.goldenticket.domain.alert.dto.AlertRequest;
import site.goldenticket.domain.alert.dto.AlertResponse;
import site.goldenticket.domain.alert.dto.AlertUnSeenResponse;
import site.goldenticket.domain.alert.entity.Alert;
import site.goldenticket.domain.alert.repository.AlertRepository;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.wish.entity.WishProduct;
import site.goldenticket.domain.product.wish.repository.WishProductRepository;
import site.goldenticket.domain.user.wish.service.WishRegionService;

@Service
@Transactional
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final WishRegionService wishRegionService;
    private final WishProductRepository wishProductRepository;

    public AlertResponse createAlertForTest(AlertRequest alertRequest) {
        Alert alert = Alert.builder()
            .userId(alertRequest.userId())
            .content(alertRequest.content())
            .viewed(false)
            .build();
        alertRepository.save(alert);
        return AlertResponse.builder()
            .alertId(alert.getId())
            .content(alert.getContent())
            .createdAt(alert.getCreatedAt())
            .viewed(alert.getViewed())
            .build();
    }

    public void createAlert(Long userId, String content) {
        alertRepository.save(Alert.builder()
            .userId(userId).content(content)
            .viewed(false).build());
    }

    public AlertUnSeenResponse getExistsNewAlert(Long userId) {
        return AlertUnSeenResponse.builder()
            .existsNewAlert(alertRepository.existsByUserIdAndViewed(userId, false))
            .build();
    }

    public AlertListResponse getAlertListByUserId(Long userId) {
        List<Alert> alerts = alertRepository.findAllByUserId(userId);
        List<AlertResponse> alertResponses = new ArrayList<>();
        for (Alert alert : alerts) {
            alertResponses.add(
                AlertResponse.builder()
                    .alertId(alert.getId())
                    .content(alert.getContent())
                    .viewed(alert.getViewed())
                    .createdAt(alert.getCreatedAt())
                    .build()
            );
            if (!alert.getViewed()) {
                alert.updateAlertViewed();
                alertRepository.save(alert);
            }
        }

        Collections.sort(alertResponses,
            Comparator.comparing(AlertResponse::createdAt).reversed());
        return AlertListResponse.builder().alertResponses(alertResponses).build();
    }

    public void createAlertOfWishRegion(AreaCode areaCode) {
        List<Long> userList = wishRegionService.findUserIdByRegion(areaCode);
        for (Long userId : userList) {
            createAlert(userId,
                "관심있던 '" + areaCode.getAreaName() + "'지역에 새로운 상품이 등록되었습니다! 사라지기 전에 확인해보세요!");
        }
    }

    public void createAlertOfWishProductToSelling(Long productId, String accommodationName,
        String roomName) {
        List<Long> userList = findUserIdListByProductId(productId);
        for (Long userId : userList) {
            createAlert(userId, "찜한 ‘" + accommodationName + "(" + roomName
                    + ")' 상품이 판매중으로 변경되어 다시 구매가 가능합니다. 빠르게 거래를 진행해주세요!");
        }
    }

    /***
     * 특정 상품을 관심 상품으로 등록한 회원 ID 목록 조회
     * @param productId 관심 상품 ID
     * @return 회원 ID List
     */
    public List<Long> findUserIdListByProductId(Long productId) {
        List<WishProduct> wishProductList = wishProductRepository.findByProductId(productId);
        List<Long> userIdList = new ArrayList<>();
        for (WishProduct wishProduct : wishProductList) {
            userIdList.add(wishProduct.getUserId());
        }
        return userIdList;
    }
}
