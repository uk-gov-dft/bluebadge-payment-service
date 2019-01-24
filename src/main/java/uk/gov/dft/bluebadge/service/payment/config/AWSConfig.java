package uk.gov.dft.bluebadge.service.payment.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
  @Bean
  AWSSecretsManager awsSecretsManager() {
    return AWSSecretsManagerClientBuilder.defaultClient();
  }
}
