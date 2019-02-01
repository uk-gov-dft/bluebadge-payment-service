package uk.gov.dft.bluebadge.service.payment.service;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.AWSSecretsManagerException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Transactional
@Slf4j
public class SecretsManager {

  private static final String LA_GOVPAY_KEY = "%s/gov_pay/%s";

  private final AWSSecretsManager awsSecretsManager;
  private final String secretEnv;
  private final ObjectMapper objectMapper;

  @Autowired
  SecretsManager(
      AWSSecretsManager awsSecretsManager,
      @Value("${blue-badge.payment.secretEnv}") String secretEnv) {
    this.awsSecretsManager = awsSecretsManager;
    this.secretEnv = secretEnv;
    objectMapper = new ObjectMapper();
  }

  public GovPayProfile retrieveLAGovPayProfile(String la) {
    Assert.hasText(la, "LA short code is not set");
    return getSecret(String.format(LA_GOVPAY_KEY, secretEnv, la));
  }

  @SneakyThrows
  private GovPayProfile getSecret(String secretName) {
    log.debug("Retrieving secret {}, from aws", secretName);
    String secret;
    GetSecretValueRequest getSecretValueRequest =
        new GetSecretValueRequest().withSecretId(secretName);
    GetSecretValueResult getSecretValueResult = null;
    try {
      getSecretValueResult = awsSecretsManager.getSecretValue(getSecretValueRequest);
    } catch (ResourceNotFoundException e) {
      log.debug("The requested secret " + secretName + " was not found");
    } catch (InvalidRequestException e) {
      log.error("The request was invalid due to: " + e.getMessage());
    } catch (InvalidParameterException e) {
      log.error("The request had invalid params: " + e.getMessage());
    } catch (AWSSecretsManagerException e) {
      log.error("AWS request resulted in exception: {}", e.getMessage());
    }

    if (getSecretValueResult == null) {
      log.debug("The requested secret {}, is null", secretName);
      return null;
    }

    if (getSecretValueResult.getSecretString() != null) {
      secret = getSecretValueResult.getSecretString();
      log.debug("Secret for key:{}, {}", secretName, secret);
      GovPayProfile laGovPayProfile = objectMapper.readValue(secret, GovPayProfile.class);
      log.debug("GovPay Profile for key:{}, {}", secretName, laGovPayProfile);
      return laGovPayProfile;
    }

    log.error("Retrieved value from AWS Secret Manager is not a string.");
    return null;
  }
}
