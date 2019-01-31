package uk.gov.dft.bluebadge.service.payment.repository.domain;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

import lombok.NonNull;

@Builder
@Data
public class PaymentEntity {
  @NonNull private UUID paymentJourneyUuid;
  @NonNull private String paymentId;
  @NonNull private String laShortCode;
  @NonNull private String reference;
  @NonNull private Long cost;
}
