package uk.gov.dft.bluebadge.service.payment.client.govpay;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GovPayClient {
  private static final String PAYMENTS_ENDPOINT = "/payments";
  private static final String RETRIEVE_ENDPOINT = PAYMENTS_ENDPOINT + "/{payment_id}";
  private final RestTemplate restTemplate;

  @Autowired
  public GovPayClient(@Qualifier("govPayRestTemplate") RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public CreatePaymentResponse createPayment(
      String apiKey, CreatePaymentRequest createPaymentRequest) {
    HttpHeaders headers = getHttpHeaders(apiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<CreatePaymentRequest> request = new HttpEntity<>(createPaymentRequest, headers);

    return restTemplate.postForObject(PAYMENTS_ENDPOINT, request, CreatePaymentResponse.class);
  }

  private HttpHeaders getHttpHeaders(String apiKey) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(ImmutableList.of(MediaType.APPLICATION_JSON));
    headers.set("Authorization", "Bearer " + apiKey);
    return headers;
  }

  public PaymentResponse retrievePayment(String apiKey, String paymentId) {
    HttpHeaders headers = getHttpHeaders(apiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<CreatePaymentRequest> request = new HttpEntity<>(null, headers);

    ResponseEntity<PaymentResponse> responseEntity =
        restTemplate.exchange(
            RETRIEVE_ENDPOINT, HttpMethod.GET, request, PaymentResponse.class, paymentId);
    return responseEntity.getBody();
  }
}
