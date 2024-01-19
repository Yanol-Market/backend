package site.goldenticket.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.common.security.authentication.dto.LoginRequest;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.dto.JoinRequest;
import site.goldenticket.domain.user.dto.JoinResponse;
import site.goldenticket.domain.user.dto.UserResponse;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.service.UserService;

import static org.springframework.http.HttpStatus.CREATED;

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

    @PostMapping
    public ResponseEntity<CommonResponse<JoinResponse>> join(@RequestBody @Validated JoinRequest joinRequest) {
        JoinResponse response = userService.join(joinRequest);
        return new ResponseEntity<>(CommonResponse.ok(response), CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserResponse>> getUserInfo(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        User user = userService.findById(principalDetails.getUserId());
        return ResponseEntity.ok(CommonResponse.ok(UserResponse.of(user)));
    }

    @PostMapping("/yanolja-login")
    public ResponseEntity<CommonResponse<Long>> yanoljaLogin(
            @RequestBody LoginRequest loginRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return ResponseEntity.ok(CommonResponse.ok(userService.yanoljaLogin(loginRequest, principalDetails.getUserId())));
    }
}
