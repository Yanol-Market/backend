package site.goldenticket.domain.alert.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.domain.alert.dto.AlertListResponse;
import site.goldenticket.domain.alert.dto.AlertResponse;
import site.goldenticket.domain.alert.dto.AlertUnSeenResponse;
import site.goldenticket.domain.alert.entity.Alert;
import site.goldenticket.domain.alert.repository.AlertRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertUnSeenResponse getExistsNewAlert(Long userId) {
        return AlertUnSeenResponse.builder()
            .existsNewAlert(alertRepository.existsByUserIdAndViewed(userId, false))
            .build();
    }

    public AlertListResponse getAlertListByUserId(Long userId) {
        List<Alert> alerts = alertRepository.findAllByUserId(userId);
        List<AlertResponse> alertResponses = new ArrayList<>();
        for (Alert alert : alerts) {
            alertResponses.add(
                AlertResponse.builder()
                    .alertId(alert.getId())
                    .content(alert.getContent())
                    .viewed(alert.getViewed())
                    .createdAt(alert.getCreatedAt())
                    .build()
            );
            if (!alert.getViewed()) {
                alert.updateAlertViewed();
                alertRepository.save(alert);
            }
        }

        Collections.sort(alertResponses,
            Comparator.comparing(AlertResponse::createdAt).reversed());
        return AlertListResponse.builder().alertResponses(alertResponses).build();
    }
}
