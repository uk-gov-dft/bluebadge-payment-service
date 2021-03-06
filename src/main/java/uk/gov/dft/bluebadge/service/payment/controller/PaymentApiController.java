package uk.gov.dft.bluebadge.service.payment.controller;

import java.util.UUID;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dft.bluebadge.common.service.exception.BadRequestException;
import uk.gov.dft.bluebadge.service.payment.controller.model.NewPaymentRequest;
import uk.gov.dft.bluebadge.service.payment.controller.model.NewPaymentResponse;
import uk.gov.dft.bluebadge.service.payment.service.PaymentService;

@RestController
@RequestMapping("payments")
@Slf4j
public class PaymentApiController {
  private final PaymentService paymentService;

  public PaymentApiController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping()
  public NewPaymentResponse createPayment(@Valid @RequestBody NewPaymentRequest newPaymentRequest) {
    return paymentService.createPayment(newPaymentRequest);
  }

  @GetMapping("/{paymentJourneyUuid}")
  public PaymentStatusResponse retrievePaymentStatus(@PathVariable String paymentJourneyUuid) {
    UUID uuid;
    try {
      uuid = UUID.fromString(paymentJourneyUuid);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(null, "Invalid payment journey UUID", e.getMessage());
    }
    return paymentService.retrievePaymentStatus(uuid);
  }
}
