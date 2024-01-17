package site.goldenticket.domain.alert.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record AlertListResponse(
    List<AlertResponse> alertResponses
) {

}
