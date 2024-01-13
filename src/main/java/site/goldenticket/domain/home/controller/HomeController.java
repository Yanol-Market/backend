package site.goldenticket.domain.home.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.home.dto.HomeResponse;
import site.goldenticket.domain.home.service.HomeService;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<CommonResponse<HomeResponse>> getHome() {
       return ResponseEntity.ok(CommonResponse.ok("홈 화면 조회 완료", homeService.getHome()));
    }
}
