package uk.gov.dft.bluebadge.service.payment.service.referencedata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RefDataGroupEnum {
  LOCAL_AUTHORITY("LA", null);

  private final String groupKey;
  private final Class<? extends Enum<?>> enumClass;
}
