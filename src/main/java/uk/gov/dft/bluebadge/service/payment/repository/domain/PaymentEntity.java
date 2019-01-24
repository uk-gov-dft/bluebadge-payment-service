package uk.gov.dft.bluebadge.service.payment.repository.domain;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class PaymentEntity {
  @NonNull private UUID govPayReference;
}
