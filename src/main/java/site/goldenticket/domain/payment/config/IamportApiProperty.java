package site.goldenticket.domain.payment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Configuration
public class IamportApiProperty {
    @Value("${iamport.imp-key}")
    private String impKey;
    @Value("${iamport.imp-secret}")
    private String impSecret;
}
