package site.goldenticket.dummy.yauser.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.goldenticket.dummy.yauser.service.YaUserService;

@RestController
@RequestMapping("/dummy")
@RequiredArgsConstructor
public class YaUserController {

    private final YaUserService yaUserService;
}
