package site.goldenticket.domain.product.service;

import org.springframework.stereotype.Component;

@Component
public class ApiUrlProperties {

    public String getYanoljaUrl() {
        return System.getProperty("yanolja.url");
    }
}
