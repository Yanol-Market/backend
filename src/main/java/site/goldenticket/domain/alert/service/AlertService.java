package site.goldenticket.domain.alert.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.domain.alert.dto.AlertListResponse;
import site.goldenticket.domain.alert.dto.AlertResponse;
import site.goldenticket.domain.alert.entity.Alert;
import site.goldenticket.domain.alert.repository.AlertRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertListResponse getAlertListByUserId(Long userId) {
        List<Alert> alerts = alertRepository.findAllByUserId(userId);
        List<AlertResponse> alertResponses = new ArrayList<>();
        for (Alert alert : alerts) {
            alertResponses.add(
                AlertResponse.builder()
                    .alertId(alert.getId())
                    .content(alert.getContent())
                    .createdAt(alert.getCreatedAt())
                    .build()
            );
        }
        return AlertListResponse.builder().alertResponses(alertResponses).build();
    }
}
