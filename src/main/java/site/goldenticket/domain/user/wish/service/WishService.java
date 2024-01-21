package site.goldenticket.domain.user.wish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.user.wish.dto.WishRegionRegisterRequest;
import site.goldenticket.domain.user.wish.dto.WishRegionListResponse;
import site.goldenticket.domain.user.wish.dto.WishRegionResponse;
import site.goldenticket.domain.user.wish.entity.WishRegion;
import site.goldenticket.domain.user.repository.WishRegionRepository;

import java.util.ArrayList;
import java.util.List;

//@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishService {

    private final WishRegionRepository wishRegionRepository;

    public WishRegionResponse createWishRegion(Long userId, WishRegionRegisterRequest wishRegionRegisterRequest) {
//        WishRegion wishRegion = WishRegion.builder()
//            .userId(userId)
//            .areaCode(wishRegionCreateRequest.areaCode())
//            .build();
//
//        wishRegionRepository.save(wishRegion);
//
//        return WishRegionResponse.builder()
//            .wishRegionId(wishRegion.getId())
//            .userId(userId)
//            .areaCode(wishRegion.getRegion()).build();
        return null;
    }

    public WishRegionListResponse getWishRegionList(Long userId) {
        List<WishRegion> wishRegionList = wishRegionRepository.findByUserId(userId);
        List<AreaCode> areaCodeList = new ArrayList<>();
        for(WishRegion wishRegion : wishRegionList) {
            areaCodeList.add(wishRegion.getRegion());
        }
        return WishRegionListResponse.builder()
            .userId(userId)
            .areaCodeList(areaCodeList)
            .build();
    }
}
