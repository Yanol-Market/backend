package site.goldenticket.domain.wishRegion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.domain.wishRegion.dto.WishRegionCreateRequest;
import site.goldenticket.domain.wishRegion.dto.WishRegionResponse;
import site.goldenticket.domain.wishRegion.entity.WishRegion;
import site.goldenticket.domain.wishRegion.repository.WishRegionRepository;

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
}
