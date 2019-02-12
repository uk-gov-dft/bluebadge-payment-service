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
public class PaymentResponse {
  @JsonProperty("payment_id")
  private String paymentId;

  @JsonProperty("created_date")
  private LocalDateTime createdDate;

  private State state;
  private String nextUrl;

  @SuppressWarnings("unchecked")
  @JsonProperty("_links")
  private void unpackNested(Map<String, Object> links) {
    Map<String, String> nextLink = (Map<String, String>) links.get("next_url");
    if (null != nextLink) {
      this.nextUrl = nextLink.get("href");
    }
  }

  public String getStatus() {
    return null == state ? null : state.status;
  }

  public Boolean getFinished() {
    return null == state ? null : state.finished;
  }

  @NoArgsConstructor
  @Getter
  private class State {
    private String status;
    private Boolean finished;
  }
}
