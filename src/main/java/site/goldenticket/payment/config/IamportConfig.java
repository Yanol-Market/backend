package site.goldenticket.payment.config;

import com.siot.IamportRestClient.IamportClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class IamportConfig {
    private final IamportApiProperty iamportApiProperty;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(iamportApiProperty.getImpKey(), iamportApiProperty.getImpSecret());
    }
}
