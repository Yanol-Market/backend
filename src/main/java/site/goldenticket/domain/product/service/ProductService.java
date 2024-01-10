package site.goldenticket.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.constants.ReservationStatus;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.product.dto.ProductRequest;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.ProductRepository;
import site.goldenticket.domain.reservation.model.Reservation;
import site.goldenticket.domain.reservation.service.ReservationService;

import static site.goldenticket.common.response.ErrorCode.PRODUCT_ALREADY_EXISTS;
import static site.goldenticket.common.response.ErrorCode.PRODUCT_NOT_FOUND;

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

    @Transactional
    public Long updateProduct(ProductRequest productRequest, Long productId) {
        Product product = getProduct(productId);
        product.update(productRequest.getGoldenPrice(), productRequest.getContent());
        return productRepository.save(product).getId();
    }

    @Transactional
    public Long deleteProduct(Long productId) {
        Product product = getProduct(productId);
        productRepository.delete(product);

        Reservation reservation = reservationService.getReservation(product.getReservationId());
        reservation.setReservationStatus(ReservationStatus.NOT_REGISTERED);
        reservationService.saveReservation(reservation);

        return productId;
    }

    private void checkReservationStatus(ReservationStatus reservationStatus) {
        if (ReservationStatus.REGISTERED.equals(reservationStatus)) {
            throw new CustomException(PRODUCT_ALREADY_EXISTS);
        }
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }
}
