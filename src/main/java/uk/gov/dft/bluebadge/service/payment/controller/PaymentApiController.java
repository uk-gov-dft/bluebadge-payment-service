package uk.gov.dft.bluebadge.service.payment.controller;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
}
