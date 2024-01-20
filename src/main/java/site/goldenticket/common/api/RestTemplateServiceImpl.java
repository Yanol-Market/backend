package site.goldenticket.common.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import site.goldenticket.common.exception.CustomException;

import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static site.goldenticket.common.response.ErrorCode.COMMON_SYSTEM_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateServiceImpl implements RestTemplateService {

    private final RestTemplate restTemplate;

    @Override
    public <T> Optional<T> get(String url, Class<T> type) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(url, type));
        } catch (HttpClientErrorException e) {
            log.error("RestTemplate Get Exception Message = {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("RestTemplate Get Exception", e);
            throw new CustomException(COMMON_SYSTEM_ERROR);
        }
    }

    @Override
    public <T> List<T> getList(String url, Class<T[]> type) {
        try {
            return Arrays.asList(restTemplate.getForObject(url, type));
        } catch (HttpClientErrorException e) {
            log.error("RestTemplate Get List Exception Message = {}", e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("RestTemplate Get List Exception", e);
            throw new CustomException(COMMON_SYSTEM_ERROR);
        }
    }

    @Override
    public <T, R> Optional<T> post(String url, R request, Class<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<R> entity = new HttpEntity<>(request, headers);

        try {
            return Optional.ofNullable(restTemplate.postForObject(url, entity, type));
        } catch (HttpClientErrorException e) {
            log.error("RestTemplate Post Exception Message = {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("RestTemplate Post Exception", e);
            throw new CustomException(COMMON_SYSTEM_ERROR);
        }
    }

    @Override
    public <R> void put(String url, R request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<R> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.put(url, entity);
        } catch (HttpClientErrorException e) {
            log.error("RestTemplate Put Exception Message = {}", e.getMessage());
            throw new CustomException(COMMON_SYSTEM_ERROR);
        } catch (Exception e) {
            log.error("RestTemplate Put Exception", e);
            throw new CustomException(COMMON_SYSTEM_ERROR);
        }
    }
}
