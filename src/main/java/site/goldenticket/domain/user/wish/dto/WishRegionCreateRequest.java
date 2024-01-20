package site.goldenticket.domain.user.wish.dto;

import jakarta.validation.constraints.NotNull;
import site.goldenticket.common.constants.AreaCode;

public record WishRegionCreateRequest(
    @NotNull(message = "지역코드를 필수로 입력하셔야 합니다.")
    AreaCode areaCode
) {

}
