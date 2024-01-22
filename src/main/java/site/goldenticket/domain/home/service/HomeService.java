package site.goldenticket.domain.home.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.domain.home.dto.HomeResponse;
import site.goldenticket.domain.product.dto.HomeProductResponse;
import site.goldenticket.domain.product.service.ProductService;
import site.goldenticket.domain.security.PrincipalDetails;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final ProductService productService;

    @Transactional(readOnly = true)
    public HomeResponse getHome(PrincipalDetails principalDetails) {

        HomeProductResponse homeProductResponse = productService.getHomeProduct(principalDetails);

        return HomeResponse.builder()
                .homeProductResponse(homeProductResponse)
                .build();
    }
}
