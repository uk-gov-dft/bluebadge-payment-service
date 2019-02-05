package uk.gov.dft.bluebadge.service.payment.controller.model;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewPaymentRequest {
  @NotBlank private String laShortCode;
  @NotBlank private String returnUrl;
  @NotBlank private String paymentMessage;
  private String language;
}
