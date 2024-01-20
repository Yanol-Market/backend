package site.goldenticket.domain.user.wish.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.constants.AreaCode;
import site.goldenticket.domain.user.wish.dto.WishRegionCreateRequest;
import site.goldenticket.domain.user.wish.dto.WishRegionListResponse;
import site.goldenticket.domain.user.wish.dto.WishRegionResponse;
import site.goldenticket.domain.user.wish.entity.WishRegion;
import site.goldenticket.domain.user.wish.repository.WishRegionRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class WishRegionService {

    private final WishRegionRepository wishRegionRepository;

    public WishRegionResponse createWishRegion(Long userId, WishRegionCreateRequest wishRegionCreateRequest) {
        WishRegion wishRegion = WishRegion.builder()
            .userId(userId)
            .areaCode(wishRegionCreateRequest.areaCode())
            .build();

        wishRegionRepository.save(wishRegion);

        return WishRegionResponse.builder()
            .wishRegionId(wishRegion.getId())
            .userId(userId)
            .areaCode(wishRegion.getRegion()).build();
    }

    public WishRegionListResponse getWishRegionList(Long userId) {
        List<WishRegion> wishRegionList = wishRegionRepository.findAllByUserId(userId);
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
