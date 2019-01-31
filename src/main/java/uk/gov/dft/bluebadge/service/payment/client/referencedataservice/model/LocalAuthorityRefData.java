package uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LocalAuthorityRefData extends ReferenceData {

  @JsonProperty("metaData")
  private LocalAuthorityMetaData localAuthorityMetaData = null;

  @JsonIgnore
  public Long getBadgeCost() {
    return getLocalAuthorityMetaData()
        .map(LocalAuthorityMetaData::getBadgeCost)
        .orElse(888L); //TODO null);
  }

  public Optional<LocalAuthorityMetaData> getLocalAuthorityMetaData() {
    return Optional.ofNullable(localAuthorityMetaData);
  }

  public boolean getPaymentsEnabled() {
    return getLocalAuthorityMetaData()
        .map(LocalAuthorityMetaData::getPaymentsEnabled)
        .orElse(true); // TODO false);
  }

  @Data
  public static class LocalAuthorityMetaData implements Serializable {
    private String issuingAuthorityShortCode;
    private String issuingAuthorityName;
    private String contactUrl;
    private String differentServiceSignpostUrl;
    private Long badgeCost;
    private Boolean paymentsEnabled;
  }
}
