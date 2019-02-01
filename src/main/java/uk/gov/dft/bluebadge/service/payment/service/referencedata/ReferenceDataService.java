package uk.gov.dft.bluebadge.service.payment.service.referencedata;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.dft.bluebadge.service.payment.client.referencedataservice.ReferenceDataApiClient;
import uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model.LocalAuthorityRefData;
import uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model.ReferenceData;

@Component
@Slf4j
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReferenceDataService {

  private final HashMap<String, LocalAuthorityRefData> authorities = new HashMap<>();
  private final ReferenceDataApiClient referenceDataApiClient;
  private AtomicBoolean isLoaded = new AtomicBoolean(false);

  @Autowired
  public ReferenceDataService(@Validated ReferenceDataApiClient referenceDataApiClient) {
    this.referenceDataApiClient = referenceDataApiClient;
  }

  public LocalAuthorityRefData getLocalAuthority(String code) {
    init();
    Assert.notNull(code, "Local authority code is null");
    return authorities.get(code);
  }

  /**
   * Load the ref data first time required. Chose not to do PostConstruct so that can start service
   * if ref data service is still starting. Not done on startup. Else would be start order
   * dependency between services.
   */
  private void init() {
    if (!isLoaded.getAndSet(true)) {

      log.info("Loading reference data.");
      List<ReferenceData> referenceDataList = referenceDataApiClient.retrieveReferenceData("APP");
      if (!referenceDataList.isEmpty()) {
        // Store valid authority ids.
        for (ReferenceData item : referenceDataList) {
          if (RefDataGroupEnum.LOCAL_AUTHORITY.getGroupKey().equals(item.getGroupShortCode())) {
            authorities.put(item.getShortCode(), (LocalAuthorityRefData) item);
          }
        }
        log.info("Reference data loaded.");
      }
    }
  }
}
