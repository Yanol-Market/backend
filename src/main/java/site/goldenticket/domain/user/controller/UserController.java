package site.goldenticket.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.domain.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/check/email")
    public ResponseEntity<CommonResponse<Boolean>> duplicateEmail(@RequestParam String email) {
        return ResponseEntity.ok(CommonResponse.ok(userService.isExistEmail(email)));
    }

    @GetMapping("/check/nickname")
    public ResponseEntity<CommonResponse<Boolean>> duplicateNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(CommonResponse.ok(userService.isExistNickname(nickname)));
    }
}
