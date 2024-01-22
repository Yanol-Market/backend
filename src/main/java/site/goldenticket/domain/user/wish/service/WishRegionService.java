package site.goldenticket.domain.user.wish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;
import site.goldenticket.domain.user.wish.repository.WishRegionRepository;
import site.goldenticket.domain.user.wish.dto.WishRegionRegisterRequest;
import site.goldenticket.domain.user.wish.entity.WishRegion;

import java.util.List;

import static site.goldenticket.common.response.ErrorCode.ALREADY_REGISTER_YANOLJA_ID;
import static site.goldenticket.common.response.ErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishRegionService {

    public static final int MAXIMUM_REGION_SIZE = 3;

    private final WishRegionRepository wishRegionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerWishRegion(Long userId, WishRegionRegisterRequest wishRegionRegisterRequest) {
        User user = findByIdWithWishRegion(userId);
        log.info("User ID [{}] Register Regions = {}", userId, wishRegionRegisterRequest.regions());

        List<WishRegion> wishRegions = wishRegionRegisterRequest.toEntity();
        user.registerWishRegions(wishRegions);

        if (user.isValidRegionSize(MAXIMUM_REGION_SIZE)) {
            throw new CustomException(ALREADY_REGISTER_YANOLJA_ID);
        }
    }

    public List<WishRegion> findWishRegion(Long userId) {
        return wishRegionRepository.findByUserId(userId);
    }

    private User findByIdWithWishRegion(Long userId) {
        return userRepository.findByIdFetchWishRegion(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
