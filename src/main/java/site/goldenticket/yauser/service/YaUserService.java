package site.goldenticket.yauser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.goldenticket.yauser.repository.YaUserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class YaUserService {
    private final YaUserRepository yaUserRepository;
}