package uk.gov.dft.bluebadge.service.payment.client.govpay;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import uk.gov.dft.bluebadge.common.api.model.Error;
import uk.gov.dft.bluebadge.common.service.exception.ServiceUnavailableException;

@Slf4j
@Component
public class GovPayResponseErrorHandler extends DefaultResponseErrorHandler {

  private final ObjectMapper om;

  @Autowired
  public GovPayResponseErrorHandler(ObjectMapper objectMapper) {
    this.om = objectMapper;
  }

  @Override
  public void handleError(ClientHttpResponse httpResponse) throws IOException {
    try {
      if (httpResponse.getStatusCode().equals(UNAUTHORIZED)) {
        throw new ServiceUnavailableException("GOV Pay api key not accepted. 401 - Unauthorized.");
      } else {
        Map<String, String> govPayErrorBody = om.readValue(httpResponse.getBody(), Map.class);
        String description = govPayErrorBody.get("description");
        String message = "Gov Pay responded with error. " + httpResponse.getStatusCode();
        throw new ServiceUnavailableException(
            new Error().message(message).reason(description), message + " - " + description, null);
      }
    } catch (IOException e) {
      log.debug(
          "Could not parse {} response. Falling back to default handling",
          httpResponse.getStatusCode(),
          e);
    }

    super.handleError(httpResponse);
  }
}
