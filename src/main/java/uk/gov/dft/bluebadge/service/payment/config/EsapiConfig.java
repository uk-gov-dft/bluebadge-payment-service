package uk.gov.dft.bluebadge.service.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dft.bluebadge.common.esapi.EsapiFilter;

@Configuration
public class EsapiConfig {

  @Bean
  public EsapiFilter getEsapiFilter() {
    return new EsapiFilter();
  }
}
