package uk.gov.dft.bluebadge.service.payment.client.govpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentResponse {
  @JsonProperty("payment_id")
  private String paymentId;

  @JsonProperty("return_url")
  private String returnUrl;

  @JsonProperty("created_date")
  private LocalDateTime createdDate;

  private String nextUrl;

  @SuppressWarnings("unchecked")
  @JsonProperty("_links")
  private void unpackNested(Map<String, Object> links) {
    Map<String, String> nextLink = (Map<String, String>) links.get("next_url");
    this.nextUrl = nextLink.get("href");
  }
}
