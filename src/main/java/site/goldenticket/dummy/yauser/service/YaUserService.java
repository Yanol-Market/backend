package site.goldenticket.dummy.yauser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.goldenticket.dummy.yauser.repository.YaUserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class YaUserService {

    private final YaUserRepository yaUserRepository;

}
