package uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonTypeInfo(
  defaultImpl = ReferenceData.class,
  use = JsonTypeInfo.Id.NAME,
  property = "groupShortCode",
  visible = true
)
@JsonSubTypes({@JsonSubTypes.Type(value = LocalAuthorityRefData.class, name = "LA")})
public class ReferenceData {
  private String shortCode;
  private String description;
  private String groupShortCode;
  private String groupDescription;
  private String subgroupShortCode;
  private String subgroupDescription;
  private Integer displayOrder;

  protected ReferenceData() {}
}
