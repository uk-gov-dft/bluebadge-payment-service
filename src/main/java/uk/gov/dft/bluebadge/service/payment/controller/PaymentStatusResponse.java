package uk.gov.dft.bluebadge.service.payment.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import uk.gov.dft.bluebadge.common.api.model.CommonResponse;

@Builder
@Getter
@EqualsAndHashCode
public class PaymentStatusResponse extends CommonResponse {
  @JsonIgnore @NonNull private UUID paymentJourneyUuid;
  @JsonIgnore @NonNull private String status;
  @JsonIgnore @NonNull private String reference;

  @JsonProperty("data")
  Map<String, String> getData() {
    return ImmutableMap.of(
        "paymentJourneyUuid",
        paymentJourneyUuid.toString(),
        "status",
        status,
        "reference",
        reference);
  }
}
