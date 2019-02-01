package uk.gov.dft.bluebadge.service.payment.controller;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewPaymentDetails {
  @NotBlank private String laShortCode;
  @NotBlank private String returnUrl;
  @NotBlank private String paymentMessage;
  private String language;
}
