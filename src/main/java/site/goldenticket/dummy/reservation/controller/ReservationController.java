package site.goldenticket.dummy.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.dummy.reservation.dto.ReservationDetailsResponse;
import site.goldenticket.dummy.reservation.dto.ReservationResponse;
import site.goldenticket.dummy.reservation.dto.UpdateReservationStatusRequest;
import site.goldenticket.dummy.reservation.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/dummy")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/reservations/{yaUserId}")
    public List<ReservationResponse> getReservations(@PathVariable Long yaUserId) {
        return reservationService.getReservations(yaUserId);
    }

    @GetMapping("/reservation/{reservationId}")
    public ReservationDetailsResponse getReservationDetails(@PathVariable Long reservationId) {
        return reservationService.getReservationDetails(reservationId);
    }

    @PutMapping("/reservation/update-status/{reservationId}")
    public void updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestBody @Validated UpdateReservationStatusRequest updateReservationStatusRequest) {
        reservationService.updateReservationStatus(reservationId, updateReservationStatusRequest);
    }
}
