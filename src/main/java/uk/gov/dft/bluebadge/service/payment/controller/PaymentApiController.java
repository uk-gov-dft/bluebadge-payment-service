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
  public NewPaymentResponse createPayment(@Valid @RequestBody NewPaymentDetails newPaymentDetails) {
    return paymentService.createPayment(newPaymentDetails);
  }

  @GetMapping("/{paymentJourneyUuid}")
  public PaymentStatusResponse retrievePaymentStatus(@PathVariable UUID paymentJourneyUuid) {
    return paymentService.retrievePaymentStatus(paymentJourneyUuid);
  }
}
