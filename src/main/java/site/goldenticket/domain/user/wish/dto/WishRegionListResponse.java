package site.goldenticket.domain.user.wish.dto;

import lombok.Builder;
import site.goldenticket.domain.product.constants.AreaCode;

import java.util.List;

@Builder
public record WishRegionListResponse(
    Long userId,
    List<AreaCode> areaCodeList
) {

}
