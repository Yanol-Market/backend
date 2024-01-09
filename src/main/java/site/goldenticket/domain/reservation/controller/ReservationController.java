package site.goldenticket.domain.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.reservation.dto.ReservationResponse;
import site.goldenticket.domain.reservation.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{yaUserId}")
    public ResponseEntity<CommonResponse<List<ReservationResponse>>> getReservations(@PathVariable Long yaUserId) {
        return ResponseEntity.ok(CommonResponse.ok("예약 목록을 성공적으로 조회했습니다.", reservationService.getReservations(yaUserId)));
    }
}
