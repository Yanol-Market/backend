package site.goldenticket.yauser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.yauser.service.YaUserService;

@RestController
@RequestMapping("yausers")
@RequiredArgsConstructor
public class YaUserController {
    private final YaUserService yaUserService;
}