package site.goldenticket.dummy.yauser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.dummy.yauser.dto.YaUserRequest;
import site.goldenticket.dummy.yauser.dto.YaUserResponse;
import site.goldenticket.dummy.yauser.model.YaUser;
import site.goldenticket.dummy.yauser.repository.YaUserRepository;

import static site.goldenticket.common.response.ErrorCode.LOGIN_FAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class YaUserService {

    private final YaUserRepository yaUserRepository;

    public YaUserResponse findYaUser(YaUserRequest yaUserRequest) {
        YaUser yaUser = yaUserRepository.findByEmail(yaUserRequest.email())
                .orElseThrow(() -> new CustomException(LOGIN_FAIL));

        if (!yaUser.getPassword().equals(yaUserRequest.password())) {
            throw new CustomException(LOGIN_FAIL);
        }

        return YaUserResponse.of(yaUser);
    }
}
