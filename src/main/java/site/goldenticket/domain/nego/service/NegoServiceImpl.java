package site.goldenticket.domain.nego.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.goldenticket.domain.nego.dto.buyer.request.PricePurposeRequest;
import site.goldenticket.domain.nego.dto.buyer.response.PricePurposeResponse;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.nego.status.NegotiationStatus;

@Service
@AllArgsConstructor
public class NegoServiceImpl implements NegoService{

    private NegoRepository negoRepository;
    @Override
    public void confirmPrice() {

    }

    @Override
    public void denyPrice() {

    }

    @Override
    public void modifyPrice() {

    }

    @Override
    public PricePurposeResponse proposePrice(PricePurposeRequest request) {
        request.increaseCount();

        Nego nego = request.toEntity();

        nego.setStatus(NegotiationStatus.NEGOTIATING);

        // 네고 엔티티 저장
        negoRepository.save(nego);

        // 저장된 네고 엔티티를 응답 DTO로 변환
        return PricePurposeResponse.fromEntity(nego);
    }


    @Override
    public void pay() {

    }

    @Override
    public void payOriginPrice() {

    }
}
