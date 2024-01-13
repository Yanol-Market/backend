package site.goldenticket.dummy.yauser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.goldenticket.dummy.yauser.dto.YaUserRequest;
import site.goldenticket.dummy.yauser.dto.YaUserResponse;
import site.goldenticket.dummy.yauser.model.YaUser;
import site.goldenticket.dummy.yauser.repository.YaUserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class YaUserService {

    private final YaUserRepository yaUserRepository;

    public YaUserResponse findYaUser(YaUserRequest yaUserRequest) {
        YaUser yaUser = yaUserRepository.findByEmail(yaUserRequest.email())
                .orElseThrow(() -> new RuntimeException("로그인 실패"));

        if (!yaUser.getPassword().equals(yaUserRequest.password())) {
            throw new RuntimeException("로그인 실패");
        }

        return YaUserResponse.of(yaUser);
    }
}
