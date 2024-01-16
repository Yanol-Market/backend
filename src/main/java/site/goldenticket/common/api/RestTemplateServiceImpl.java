package site.goldenticket.common.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import site.goldenticket.common.exception.CustomException;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static site.goldenticket.common.response.ErrorCode.COMMON_SYSTEM_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateServiceImpl implements RestTemplateService {

    private final RestTemplate restTemplate;

    @Override
    public <T, R> Optional<T> get(String url, R request, Class<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<R> entity = new HttpEntity<>(request, headers);

        try {
            return Optional.ofNullable(restTemplate.postForObject(url, entity, type));
        } catch (HttpClientErrorException e) {
            log.error("RestTemplate Get Exception Message = {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("RestTemplate Get Exception", e);
            throw new CustomException(COMMON_SYSTEM_ERROR);
        }
    }
}
