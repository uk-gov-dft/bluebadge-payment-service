package uk.gov.dft.bluebadge.service.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import uk.gov.dft.bluebadge.service.payment.client.govpay.GovPayResponseErrorHandler;

@Configuration
public class GeneralConfig {
  @Value("${blue-badge.payment.govPayBaseUrl}")
  private String govPayUrl;

  @Bean("govPayRestTemplate")
  public RestTemplate govPayRestTemplate(GovPayResponseErrorHandler govPayResponseErrorHandler) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(govPayUrl));
    restTemplate.setErrorHandler(govPayResponseErrorHandler);
    return restTemplate;
  }
}
