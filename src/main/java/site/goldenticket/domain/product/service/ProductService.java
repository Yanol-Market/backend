package site.goldenticket.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.constants.ReservationStatus;
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;
import site.goldenticket.domain.reservation.model.Reservation;
import site.goldenticket.domain.reservation.service.ReservationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ReservationService reservationService;

    @Transactional
    public Long createProduct(ProductRequest productRequest, Long reservationId) {
        Reservation reservation = getReservation(reservationId);

        Product product = productRequest.toEntity(reservation, reservationId);
        Product savedProduct = saveProduct(product);

        setReservationStatus(reservation, ReservationStatus.REGISTERED);
        saveReservation(reservation);

        return savedProduct.getId();
    }

    private Reservation getReservation(Long reservationId) { return reservationService.getReservation(reservationId); }

    private Product saveProduct(Product product) { return productRepository.save(product); }

    private void setReservationStatus(Reservation reservation,ReservationStatus reservationStatus) { reservation.setReservationStatus(reservationStatus); }

    private void saveReservation(Reservation reservation) { reservationService.saveReservation(reservation); }

}
