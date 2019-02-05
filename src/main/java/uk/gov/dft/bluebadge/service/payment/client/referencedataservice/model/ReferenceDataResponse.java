package uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.EqualsAndHashCode;
import uk.gov.dft.bluebadge.common.api.model.CommonResponse;

@EqualsAndHashCode(callSuper = true)
public class ReferenceDataResponse extends CommonResponse {

  @JsonProperty("data")
  private List<ReferenceData> data = null;

  public List<ReferenceData> getData() {
    return data;
  }

  public void setData(List<ReferenceData> data) {
    this.data = data;
  }
}
