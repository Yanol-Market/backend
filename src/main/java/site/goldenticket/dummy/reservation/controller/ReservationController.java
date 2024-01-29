package site.goldenticket.dummy.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.dummy.reservation.dto.ReservationDetailsResponse;
import site.goldenticket.dummy.reservation.dto.YanoljaProductResponse;
import site.goldenticket.dummy.reservation.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/dummy")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/reservations/{yaUserId}")
    public List<YanoljaProductResponse> getReservations(@PathVariable Long yaUserId) {
        return reservationService.getReservations(yaUserId);
    }

    @GetMapping("/reservation/{reservationId}")
    public ReservationDetailsResponse getReservationDetails(@PathVariable Long reservationId) {
        return reservationService.getReservationDetails(reservationId);
    }
}
