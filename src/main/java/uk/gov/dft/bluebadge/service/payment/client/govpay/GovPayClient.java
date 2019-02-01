package uk.gov.dft.bluebadge.service.payment.client.govpay;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GovPayClient {
  private static final String PAYMENTS_ENDPOINT = "/payments";
  private final RestTemplate restTemplate;

  @Autowired
  public GovPayClient(@Qualifier("govPayRestTemplate") RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public CreatePaymentResponse createPayment(
      String apiKey, CreatePaymentRequest createPaymentRequest) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(ImmutableList.of(MediaType.APPLICATION_JSON));
    headers.set("Authorization", "Bearer " + apiKey);

    HttpEntity<CreatePaymentRequest> request = new HttpEntity<>(createPaymentRequest, headers);

    return restTemplate.postForObject(PAYMENTS_ENDPOINT, request, CreatePaymentResponse.class);
  }
}
