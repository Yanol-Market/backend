package site.goldenticket.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.goldenticket.reservation.repository.ReservationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;
}
