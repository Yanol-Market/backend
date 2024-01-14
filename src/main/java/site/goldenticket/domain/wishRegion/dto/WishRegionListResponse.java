package site.goldenticket.domain.wishRegion.dto;

import java.util.List;
import lombok.Builder;
import site.goldenticket.common.constants.AreaCode;

@Builder
public record WishRegionListResponse(
    Long userId,
    List<AreaCode> areaCodeList
) {

}
