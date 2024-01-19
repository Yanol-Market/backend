package site.goldenticket.domain.nego.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.service.ProductService;

import java.time.LocalDateTime;
import java.util.List;

import static site.goldenticket.domain.nego.status.NegotiationStatus.*;

@Service
@RequiredArgsConstructor
public class NegoSchedulerService {
    private final NegoRepository negoRepository;
    private final ProductService productService;

    @Scheduled(fixedDelay = 60000)
    public void changeStatus() {
        LocalDateTime currentTime = LocalDateTime.now();

        List<Nego> pendingNegos = negoRepository.findByStatus(PAYMENT_PENDING);
        List<Nego> transferNegos = negoRepository.findByStatus(TRANSFER_PENDING);

        for (Nego nego : pendingNegos) {
            Product product = productService.getProduct(nego.getProductId());
            LocalDateTime updatedAt = nego.getUpdatedAt();
            if (updatedAt != null && currentTime.isAfter(updatedAt.plusMinutes(20))) {
                product.setProductStatus(ProductStatus.SELLING);
                productService.updateProductForNego(product);
                nego.setStatus(NEGOTIATION_TIMEOUT);
                nego.setUpdatedAt(currentTime);
            }
        } //상품 상태 판매중

        for (Nego transferNego : transferNegos) {
            Product product = productService.getProduct(transferNego.getProductId());
            LocalDateTime updatedAt = transferNego.getUpdatedAt();
            if (updatedAt != null && currentTime.isAfter(updatedAt.plusMinutes(20))) {
                transferNego.setStatus(NEGOTIATION_COMPLETED);
                transferNego.setUpdatedAt(currentTime);
                product.setProductStatus(ProductStatus.SOLD_OUT);
                productService.updateProductForNego(product);
            }
        }   // 3시간 뒤 자동양도

        negoRepository.saveAll(transferNegos);
        negoRepository.saveAll(pendingNegos);
    }

}
