package uk.gov.dft.bluebadge.service.payment.client.govpay;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

public class GovPayClientTest {
  public static final String TEST_URI = "http://justtesting:7878/test";
  public static final String TEST_API_KEY = "test api key";

  GovPayClient govPayClient;
  MockRestServiceServer mockServer;
  private ClassPathResource createJsonResponse;

  @Before
  public void setup() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(TEST_URI));
    mockServer = MockRestServiceServer.bindTo(restTemplate).build();

    govPayClient = new GovPayClient(restTemplate);

    createJsonResponse = new ClassPathResource("testdata/govpay/createResponse.json");
  }

  @Test
  @SneakyThrows
  public void createSuccess() {
    mockServer
        .expect(once(), requestTo(TEST_URI + "/payments"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(jsonPath("amount", equalTo(150)))
        .andExpect(jsonPath("reference", equalTo("TEST 123")))
        .andExpect(jsonPath("description", equalTo("Testing")))
        .andExpect(jsonPath("return_url", equalTo("http://return")))
        .andExpect(jsonPath("language", equalTo("en")))
        .andExpect(header(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + TEST_API_KEY)))
        .andRespond(withSuccess(createJsonResponse, MediaType.APPLICATION_JSON));

    CreatePaymentRequest paymentRequest =
        CreatePaymentRequest.builder()
            .amount(new BigDecimal(150L))
            .description("Testing")
            .reference("TEST 123")
            .returnUrl("http://return")
            .language("en")
            .build();

    CreatePaymentResponse paymentResponse =
        govPayClient.createPayment(TEST_API_KEY, paymentRequest);

    assertThat(paymentResponse).isNotNull();
    assertThat(paymentResponse.getPaymentId()).isEqualTo("j3c7364jk8cbbopscdlfkt2o88");
    assertThat(paymentResponse.getNextUrl())
        .isEqualTo(
            "https://www.payments.service.gov.uk/secure/3d77de51-c137-4c01-b0dc-5f012cb1ec8d");
  }
}
