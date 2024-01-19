package site.goldenticket.domain.alert.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.alert.dto.AlertListResponse;
import site.goldenticket.domain.alert.dto.AlertRequest;
import site.goldenticket.domain.alert.dto.AlertResponse;
import site.goldenticket.domain.alert.dto.AlertUnSeenResponse;
import site.goldenticket.domain.alert.service.AlertService;
import site.goldenticket.domain.security.PrincipalDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    @PostMapping("/test")
    public ResponseEntity<CommonResponse<AlertResponse>> createAlertForTest(
        @Valid @RequestBody AlertRequest alertRequest) {
        return ResponseEntity.ok(
            CommonResponse.ok("테스트용 알림이 등록되었습니다.",
                alertService.createAlertForTest(alertRequest)));
    }

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
