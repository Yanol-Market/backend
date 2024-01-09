package site.goldenticket.domain.yauser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.domain.yauser.service.YaUserService;

@RestController
@RequestMapping("/yausers")
@RequiredArgsConstructor
public class YaUserController {
    private final YaUserService yaUserService;
}