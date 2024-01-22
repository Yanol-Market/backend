package site.goldenticket.common.api;

import java.util.List;
import java.util.Optional;

public interface RestTemplateService {
    <T> Optional<T> get(String url, Class<T> type);
    <T> List<T> getList(String url, Class<T[]> type);
    <T, R> Optional<T> post(String url, R request, Class<T> type);
}
