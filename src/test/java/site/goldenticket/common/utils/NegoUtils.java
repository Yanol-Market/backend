package site.goldenticket.common.utils;

import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;

import java.time.LocalDateTime;

import static site.goldenticket.domain.nego.status.NegotiationStatus.NEGOTIATING;

public class NegoUtils {
    private static final Integer price = 50000;
    private static final Integer count = 1;
    private static final NegotiationStatus status = NEGOTIATING;
    private static final boolean consent = false;
    private static final LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(20);
    private static final LocalDateTime createdAt = LocalDateTime.now();
    private static final LocalDateTime updatedAt = LocalDateTime.now();

    private NegoUtils() {}

    public static Nego createNego(Product product, User user) {
        Nego nego = Nego.builder()
                .price(price)
                .count(count)
                .status(NEGOTIATING)
                .consent(false)
                .expirationTime(expirationTime)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        nego.setProduct(product);
        nego.setUser(user);

        return nego;
    }
}
