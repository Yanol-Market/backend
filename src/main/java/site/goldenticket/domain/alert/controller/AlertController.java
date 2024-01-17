package site.goldenticket.domain.alert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.alert.dto.AlertListResponse;
import site.goldenticket.domain.alert.dto.AlertUnSeenResponse;
import site.goldenticket.domain.alert.service.AlertService;
import site.goldenticket.domain.security.PrincipalDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/unseen")
    public ResponseEntity<CommonResponse<AlertUnSeenResponse>> getAlertUnSeen(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(
            CommonResponse.ok("안읽은 알림 존재 여부가 조회되었습니다.",
                alertService.getExistsNewAlert(principalDetails.getUserId())));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<AlertListResponse>> getAlertList(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(
            CommonResponse.ok("알림 목록이 조회되었습니다.",
                alertService.getAlertListByUserId(principalDetails.getUserId())));
    }
}
