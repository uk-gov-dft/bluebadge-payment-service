package uk.gov.dft.bluebadge.service.payment.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import uk.gov.dft.bluebadge.common.api.model.CommonResponse;

@Builder
@Getter
public class NewPaymentResponse extends CommonResponse {
  @JsonIgnore @NonNull private UUID paymentJourneyUuid;
  @JsonIgnore @NonNull private String nextUrl;

  @JsonProperty("data")
  Map<String, String> getData() {
    return ImmutableMap.of("paymentJourneyUuid", paymentJourneyUuid.toString(), "nextUrl", nextUrl);
  }
}
