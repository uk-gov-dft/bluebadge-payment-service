package uk.gov.dft.bluebadge.service.payment.client.govpay;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;

public class CreatePaymentRequestTest {

  private CreatePaymentRequest.CreatePaymentRequestBuilder paymentRequestBuilder;

  @Before
  public void setUp() throws Exception {
    paymentRequestBuilder =
        CreatePaymentRequest.builder()
            .language("lang")
            .returnUrl("return url")
            .reference("ref")
            .description("desc");
  }

  @Test
  public void amountWholeNumber() {
    CreatePaymentRequest paymentRequest =
        paymentRequestBuilder.amount(new BigDecimal("100")).build();
    assertThat(paymentRequest.getAmount()).isEqualTo(10000);
  }

  @Test
  public void amountWithPence() {
    CreatePaymentRequest paymentRequest =
        paymentRequestBuilder.amount(new BigDecimal("100.12")).build();
    assertThat(paymentRequest.getAmount()).isEqualTo(10012);
  }

  @Test
  public void amountWithTooManyDecimalPlaces() {
    CreatePaymentRequest paymentRequest =
        paymentRequestBuilder.amount(new BigDecimal("100.12345")).build();
    assertThat(paymentRequest.getAmount()).isEqualTo(10012);
  }

  @Test
  public void amountWithTooManyDecimalPlacesGreaterThanHalf() {
    CreatePaymentRequest paymentRequest =
        paymentRequestBuilder.amount(new BigDecimal("100.1277")).build();
    assertThat(paymentRequest.getAmount()).isEqualTo(10012);
  }
}
