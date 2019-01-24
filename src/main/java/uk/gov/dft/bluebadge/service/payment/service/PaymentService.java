package uk.gov.dft.bluebadge.service.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class PaymentService {

  private final SecretsManager secretsManager;

  @Autowired
  PaymentService(SecretsManager secretsManager) {
    this.secretsManager = secretsManager;
  }
}
