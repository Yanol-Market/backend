package site.goldenticket.common.api;

import java.util.Optional;

public interface RestTemplateService {

    <T, R> Optional<T> get(String url, R request, Class<T> type);
}
