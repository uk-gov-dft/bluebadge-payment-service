package uk.gov.dft.bluebadge.service.payment.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.dft.bluebadge.common.api.CommonResponseEntityExceptionHandler;
import uk.gov.dft.bluebadge.common.api.model.CommonResponse;
import uk.gov.dft.bluebadge.common.api.model.Error;
import uk.gov.dft.bluebadge.common.service.exception.BadRequestException;
import uk.gov.dft.bluebadge.common.service.exception.ServiceException;

@ControllerAdvice
@Slf4j
public class CommonResponseControllerAdvice extends CommonResponseEntityExceptionHandler {

  private static final String PAYMENT_SERVICE_EXCEPTION = "Payment Service Exception. {}";

  @SuppressWarnings("unused")
  @ExceptionHandler({ServiceException.class})
  public ResponseEntity<CommonResponse> handleServiceException(ServiceException e) {
    HttpStatus statusCode = e.getResponse().getStatusCode();
    if (statusCode.is4xxClientError()) {
      log.error("Payment Service Client Exception. {}, {}", e.getMessage(), e.getResponse());
    } else {
      log.error(PAYMENT_SERVICE_EXCEPTION, e.getResponse(), e);
    }
    return e.getResponse();
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<CommonResponse> handleException(Exception e) {
    log.error(PAYMENT_SERVICE_EXCEPTION, e.getMessage(), e);
    CommonResponse commonResponse = new CommonResponse();
    Error error = new Error();
    error.setMessage("Unexpected exception: " + e.toString());
    error.setReason(e.getMessage());
    commonResponse.setError(error);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonResponse);
  }

  @SuppressWarnings("unused")
  @ExceptionHandler({InvalidFormatException.class})
  public ResponseEntity<CommonResponse> handleInvalidFormatException(InvalidFormatException e) {
    log.error(PAYMENT_SERVICE_EXCEPTION, e.getMessage());
    Error error = new Error();

    error.setReason(parseInvalidFormatReason(e.getMessage()));
    error.setMessage("InvalidFormat." + e.getTargetType().getSimpleName());
    BadRequestException e1 = new BadRequestException(error);
    return e1.getResponse();
  }

  static String parseInvalidFormatReason(String reason) {
    // The error for an invalid enum has class inside backticks.
    // Possibly construed to be a security risk.
    // Of no use to consumer of API anyway, so remove.
    return StringUtils.removePattern(reason, "`.*`");
  }
}
