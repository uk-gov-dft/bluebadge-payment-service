package uk.gov.dft.bluebadge.service.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dft.bluebadge.common.controller.AbstractController;

@RestController
@RequestMapping("payment")
public class PaymentApiController extends AbstractController {
  @PostMapping("/create")
  public String startPayment() {
    return "{\n" + "  \"hello\":\"world\"\n" + "}";
  }
}
