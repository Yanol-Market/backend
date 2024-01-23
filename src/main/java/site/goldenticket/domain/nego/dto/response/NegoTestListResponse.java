package site.goldenticket.domain.nego.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record NegoTestListResponse(
    List<NegoTestResponse> negoTestResponseList
) {

}
