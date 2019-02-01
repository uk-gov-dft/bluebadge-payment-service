package uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model;

import java.math.BigDecimal;
import uk.gov.dft.bluebadge.service.payment.service.referencedata.RefDataGroupEnum;

public class TestLocalAuthorityRefData {
  public static LocalAuthorityRefData localAuthorityRefData() {
    LocalAuthorityRefData result = new LocalAuthorityRefData();
    result.setShortCode("LA_1");
    result.setDescription("test LA");
    result.setGroupShortCode(RefDataGroupEnum.LOCAL_AUTHORITY.name());

    LocalAuthorityRefData.LocalAuthorityMetaData metaData =
        new LocalAuthorityRefData.LocalAuthorityMetaData();
    metaData.setBadgeCost(new BigDecimal(123L));
    metaData.setPaymentsEnabled(Boolean.TRUE);

    result.setLocalAuthorityMetaData(metaData);
    return result;
  }
}
