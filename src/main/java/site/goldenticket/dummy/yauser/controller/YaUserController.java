package site.goldenticket.dummy.yauser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.dummy.yauser.dto.YaUserRequest;
import site.goldenticket.dummy.yauser.dto.YaUserResponse;
import site.goldenticket.dummy.yauser.service.YaUserService;

@RestController
@RequestMapping("/dummy")
@RequiredArgsConstructor
public class YaUserController {

    private final YaUserService yaUserService;

    @PostMapping("/yauser")
    public YaUserResponse getYaUser(@RequestBody YaUserRequest yaUserRequest) {
        return yaUserService.findYaUser(yaUserRequest);
    }
}
