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

    @Scheduled(fixedDelay = 1000)
    public void changeStatus() {
        LocalDateTime currentTime = LocalDateTime.now();

        List<Nego> pendingNegos = negoRepository.findByStatus(PAYMENT_PENDING);
       // List<Nego> completedNegos = negoRepository.findByStatus(NEGOTIATION_COMPLETED);
        List<Nego> transferNegos = negoRepository.findByStatus(TRANSFER_PENDING);

        for (Nego nego : pendingNegos) {
            LocalDateTime updatedAt = nego.getUpdatedAt();
            if (updatedAt != null && currentTime.isAfter(updatedAt.plusSeconds(10))) {
                nego.setStatus(NEGOTIATION_TIMEOUT);
                nego.setUpdatedAt(currentTime);
            }
        }

        for(Nego transferNego : transferNegos){
            Product product = productService.getProduct(transferNego.getProductId());
            LocalDateTime updatedAt = transferNego.getUpdatedAt();
            if (updatedAt != null && currentTime.isAfter(updatedAt.plusSeconds(10))) {
                transferNego.setStatus(NEGOTIATION_COMPLETED);
                transferNego.setUpdatedAt(currentTime);
                product.setProductStatus(ProductStatus.SOLD_OUT);
            }
        }

//        for (Nego completedNego : completedNegos){
//            Long productId = completedNego.getProductId();
//            List<Nego> relatedNegos = negoRepository.findByProductIdAndStatusNot(productId, NEGOTIATION_COMPLETED);
//            for (Nego relatedNego : relatedNegos) {
//                relatedNego.setStatus(NEGOTIATION_COMPLETED);
//            }
//        }
//        negoRepository.saveAll(completedNegos);

        negoRepository.saveAll(transferNegos);
        negoRepository.saveAll(pendingNegos);
    }

}
