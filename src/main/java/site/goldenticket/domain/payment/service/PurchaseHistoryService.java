package site.goldenticket.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.service.NegoService;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.repository.OrderRepository;
import site.goldenticket.domain.security.PrincipalDetails;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseHistoryService {

    private final OrderRepository orderRepository;
    private final NegoService negoService;
    public String getPurchaseProgressHistory(PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUserId();
        //결제 햇는지 확인
        List<Order> orders = orderRepository.findByUserId(userId);
        if (!orders.isEmpty()) {
            for (Order order : orders) {

            }
        }

        //네고 중인지 확인
        Optional<Nego> nego = negoService.getUserNego(userId);


    }
}
