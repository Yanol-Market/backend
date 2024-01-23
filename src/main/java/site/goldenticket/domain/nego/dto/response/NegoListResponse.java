package site.goldenticket.domain.nego.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record NegoListResponse(
    List<NegoResponse> negoResponseList
) {

}
