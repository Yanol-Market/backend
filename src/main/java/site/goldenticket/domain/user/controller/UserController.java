package site.goldenticket.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.goldenticket.common.response.CommonResponse;
import site.goldenticket.common.security.authentication.dto.LoginRequest;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.user.dto.*;
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
        return ResponseEntity.ok(CommonResponse.ok(UserResponse.from(user)));
    }

    @PatchMapping("/password")
    public ResponseEntity<CommonResponse<Void>> changePassword(
            @RequestBody @Validated ChangePasswordRequest changePasswordRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long userId = principalDetails.getUserId();
        userService.updatePassword(userId, changePasswordRequest);
        return ResponseEntity.ok(CommonResponse.ok());
    }

    @GetMapping("/account")
    public ResponseEntity<CommonResponse<AccountResponse>> getAccount(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        User user = userService.findById(principalDetails.getUserId());
        return ResponseEntity.ok(CommonResponse.ok(AccountResponse.from(user)));
    }

    @PatchMapping("/account")
    public ResponseEntity<CommonResponse<Void>> registerAccount(
            @RequestBody @Validated RegisterAccountRequest registerAccountRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        userService.registerAccount(principalDetails.getUserId(), registerAccountRequest);
        return ResponseEntity.ok(CommonResponse.ok());
    }

    @DeleteMapping("/account")
    public ResponseEntity<CommonResponse<Void>> removeAccount(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        userService.removeAccount(principalDetails.getUserId());
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

    @PostMapping("/reset-password")
    public ResponseEntity<CommonResponse<ResetPasswordResponse>> resetPassword(
            @RequestBody
            @Validated ResetPasswordRequest resetPasswordRequest
    ){
        userService.resetPasswordAndSendEmail(resetPasswordRequest);
        return ResponseEntity.ok(CommonResponse.ok(new ResetPasswordResponse()));
    }

}
