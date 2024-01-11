package site.goldenticket.payment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("iamportApi.properties")
@Getter
@Configuration
public class IamportApiProperty {
    @Value("${imp_key}")
    private String impKey;
    @Value("${imp_secret}")
    private String impSecret;
}