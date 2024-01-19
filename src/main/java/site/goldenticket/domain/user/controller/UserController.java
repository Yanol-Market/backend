package site.goldenticket.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.common.security.authentication.dto.LoginRequest;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.dto.AccountResponse;
import site.goldenticket.domain.user.dto.RegisterAccountRequest;
import site.goldenticket.domain.user.dto.JoinRequest;
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
        boolean response = userService.isExistEmail(email);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @GetMapping("/check/nickname")
    public ResponseEntity<CommonResponse<Boolean>> duplicateNickname(@RequestParam String nickname) {
        boolean response = userService.isExistNickname(nickname);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<Long>> join(@RequestBody @Validated JoinRequest joinRequest) {
        Long response = userService.join(joinRequest);
        return new ResponseEntity<>(CommonResponse.ok(response), CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserResponse>> getUserInfo(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        User user = userService.findById(principalDetails.getUserId());
        return ResponseEntity.ok(CommonResponse.ok(UserResponse.of(user)));
    }

    @GetMapping("/account")
    public ResponseEntity<CommonResponse<AccountResponse>> getAccount(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        User user = userService.findById(principalDetails.getUserId());
        return ResponseEntity.ok(CommonResponse.ok(AccountResponse.of(user)));
    }

    @PatchMapping("/account")
    public ResponseEntity<CommonResponse<Void>> registerAccount(
            @RequestBody @Validated RegisterAccountRequest registerAccountRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        userService.registerAccount(principalDetails.getUserId(), registerAccountRequest);
        return ResponseEntity.ok(CommonResponse.ok());
    }

    @PostMapping("/yanolja-login")
    public ResponseEntity<CommonResponse<Long>> yanoljaLogin(
            @RequestBody LoginRequest loginRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long response = userService.yanoljaLogin(loginRequest, principalDetails.getUserId());
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
}
