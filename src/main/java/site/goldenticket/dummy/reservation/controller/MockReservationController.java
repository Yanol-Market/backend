package site.goldenticket.dummy.reservation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.dummy.reservation.constants.ReservationType;
import site.goldenticket.dummy.reservation.dto.ReservationDetailsResponse;
import site.goldenticket.dummy.reservation.dto.YanoljaProductResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@RestController
public class MockReservationController {

    @GetMapping(
            path = "/test/dummy/reservations/{yaUserId}"
    )
    public List<YanoljaProductResponse> getReservations(
            @PathVariable Long yaUserId
    ) {
        return Arrays.asList(
                new YanoljaProductResponse(
                        1L,
                        "숙소명1",
                        ReservationType.STAY,
                        "객실명1",
                        2,
                        4,
                        LocalDate.of(2024, 2, 1),
                        LocalDate.of(2024, 2, 7),
                        LocalTime.of(14, 0),
                        LocalTime.of(12, 0),
                        6,
                        LocalDate.now(),
                        200000,
                        180000
                ),
                new YanoljaProductResponse(
                        2L,
                        "숙소명2",
                        ReservationType.STAY,
                        "객실명2",
                        3,
                        6,
                        LocalDate.of(2024, 2, 2),
                        LocalDate.of(2024, 2, 8),
                        LocalTime.of(15, 0),
                        LocalTime.of(11, 0),
                        6,
                        LocalDate.now(),
                        250000,
                        220000
                )
        );
    }

    @GetMapping(
            path = "/test/dummy/reservation/{reservationId}"
    )
    public ReservationDetailsResponse getReservationDetails(
            @PathVariable Long reservationId
    ) {
        return new ReservationDetailsResponse(
                1L,
                AreaCode.SEOUL,
                "숙소 이미지",
                "숙소명",
                "숙소 주소",
                ReservationType.STAY,
                "객실명",
                2,
                4,
                LocalTime.of(14, 0),
                LocalTime.of(12, 0),
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 2, 7),
                LocalDate.now(),
                200000,
                180000
        );
    }
}
