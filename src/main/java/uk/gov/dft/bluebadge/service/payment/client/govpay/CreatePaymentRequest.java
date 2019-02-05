package uk.gov.dft.bluebadge.service.payment.client.govpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class CreatePaymentRequest {
  @NonNull private final Integer amount;
  @NonNull private final String reference;
  @NonNull private final String description;
  @NonNull private final String language;

  @NonNull
  @JsonProperty("return_url")
  private final String returnUrl;

  public static class CreatePaymentRequestBuilder {
    public CreatePaymentRequestBuilder amount(Integer amount) {
      this.amount = amount;
      return this;
    }

    public CreatePaymentRequestBuilder amount(BigDecimal amount) {
      this.amount = amount.movePointRight(2).intValue();
      return this;
    }
  }
}
