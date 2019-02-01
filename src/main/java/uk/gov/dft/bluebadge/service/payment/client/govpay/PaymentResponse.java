package uk.gov.dft.bluebadge.service.payment.client.govpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
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

  public String getStatus() {
    return state.status;
  }

  public Boolean getFinished() {
    return state.finished;
  }

  @NoArgsConstructor
  @Getter
  private class State {
    private String status;
    private Boolean finished;
  }
}
