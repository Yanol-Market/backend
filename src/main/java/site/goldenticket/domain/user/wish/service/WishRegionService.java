package site.goldenticket.domain.user.wish.service;

import static site.goldenticket.common.response.ErrorCode.USER_NOT_FOUND;
import static site.goldenticket.common.response.ErrorCode.WISH_REGION_OVER_MAXIMUM;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;
import site.goldenticket.domain.user.wish.dto.WishRegionRegisterRequest;
import site.goldenticket.domain.user.wish.entity.WishRegion;
import site.goldenticket.domain.user.wish.repository.WishRegionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishRegionService {

    public static final int MAXIMUM_REGION_SIZE = 3;

    private final WishRegionRepository wishRegionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerWishRegion(Long userId,
        WishRegionRegisterRequest wishRegionRegisterRequest) {
        User user = findByIdWithWishRegion(userId);
        log.info("User ID [{}] Register Regions = {}", userId, wishRegionRegisterRequest.regions());

        List<WishRegion> wishRegions = wishRegionRegisterRequest.toEntity();
        user.registerWishRegions(wishRegions);

        if (user.isValidRegionSize(MAXIMUM_REGION_SIZE)) {
            throw new CustomException(WISH_REGION_OVER_MAXIMUM);
        }
    }

    public List<WishRegion> findWishRegion(Long userId) {
        return wishRegionRepository.findByUserId(userId);
    }

    private User findByIdWithWishRegion(Long userId) {
        return userRepository.findByIdFetchWishRegion(userId)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    /***
     * 특정 지역을 관심 지역으로 등록한 회원 ID 목록 조회
     * @param areaCode 관심 지역
     * @return 회원 ID List
     */
    public List<Long> findUserIdByRegion(AreaCode areaCode) {
        List<WishRegion> wishRegionList = wishRegionRepository.findByRegion(areaCode);
        List<Long> userIdList = new ArrayList<>();
        for (WishRegion wishRegion : wishRegionList) {
            userIdList.add(wishRegion.getUser().getId());
        }
        return userIdList;
    }
}
