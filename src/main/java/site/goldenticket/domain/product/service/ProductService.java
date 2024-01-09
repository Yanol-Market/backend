package site.goldenticket.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.constants.ReservationStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.repository.ProductRepository;
import site.goldenticket.domain.reservation.model.Reservation;
import site.goldenticket.domain.reservation.service.ReservationService;

import static site.goldenticket.common.response.ErrorCode.PRODUCT_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ReservationService reservationService;

    @Transactional
    public Long createProduct(ProductRequest productRequest, Long reservationId) {
        Reservation reservation = reservationService.getReservation(reservationId);
        checkReservationStatus(reservation.getReservationStatus());

        reservation.setReservationStatus(ReservationStatus.REGISTERED);
        reservationService.saveReservation(reservation);

        return productRepository.save(productRequest.toEntity(reservation, reservationId)).getId();
    }

    private void checkReservationStatus(ReservationStatus reservationStatus) {
        if (ReservationStatus.REGISTERED.equals(reservationStatus)) {
            throw new CustomException(PRODUCT_ALREADY_EXISTS);
        }
    }
}
