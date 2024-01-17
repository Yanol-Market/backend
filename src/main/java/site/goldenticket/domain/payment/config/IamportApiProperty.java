package site.goldenticket.domain.payment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:/secret.properties")
public class IamportApiProperty {
    @Value("${imp_key}")
    private String impKey;
    @Value("${imp_secret}")
    private String impSecret;
}
