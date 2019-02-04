package uk.gov.dft.bluebadge.service.payment.service;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.dft.bluebadge.common.service.exception.BadRequestException;
import uk.gov.dft.bluebadge.common.service.exception.NotFoundException;
import uk.gov.dft.bluebadge.common.service.exception.ServiceUnavailableException;
import uk.gov.dft.bluebadge.service.payment.client.govpay.CreatePaymentRequest;
import uk.gov.dft.bluebadge.service.payment.client.govpay.GovPayClient;
import uk.gov.dft.bluebadge.service.payment.client.govpay.PaymentResponse;
import uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model.LocalAuthorityRefData;
import uk.gov.dft.bluebadge.service.payment.controller.NewPaymentDetails;
import uk.gov.dft.bluebadge.service.payment.controller.NewPaymentResponse;
import uk.gov.dft.bluebadge.service.payment.controller.PaymentStatusResponse;
import uk.gov.dft.bluebadge.service.payment.repository.PaymentRepository;
import uk.gov.dft.bluebadge.service.payment.repository.domain.PaymentEntity;
import uk.gov.dft.bluebadge.service.payment.service.referencedata.ReferenceDataService;

@Service
@Transactional
@Slf4j
public class PaymentService {
  private static final char[] REF_CHARS = "23456789ABCDEFGHJKLMNPRSTUVWXY".toCharArray();

  private static final String WELSH = "cy";
  private static final String ENGLISH = Locale.ENGLISH.getLanguage();
  private final SecretsManager secretsManager;
  private final GovPayClient govPayClient;
  private final PaymentRepository paymentRepository;
  private final ReferenceDataService referenceDataService;

  @Autowired
  PaymentService(
      SecretsManager secretsManager,
      GovPayClient govPayClient,
      PaymentRepository paymentRepository,
      ReferenceDataService referenceDataService) {
    this.secretsManager = secretsManager;
    this.govPayClient = govPayClient;
    this.paymentRepository = paymentRepository;
    this.referenceDataService = referenceDataService;
  }

  public NewPaymentResponse createPayment(NewPaymentDetails newPaymentDetails) {
    String laShortCode = newPaymentDetails.getLaShortCode();
    LocalAuthorityRefData localAuthority = referenceDataService.getLocalAuthority(laShortCode);
    if (null == localAuthority) {
      throw new BadRequestException(
          "laShortCode", "Invalid LA short code", "Local authority not found for: " + laShortCode);
    }
    if (!localAuthority.getPaymentsEnabled()) {
      throw new ServiceUnavailableException("Local authority does not have payments enabled.");
    }
    if (null == localAuthority.getBadgeCost() || localAuthority.getBadgeCost().longValue() <= 0) {
      throw new ServiceUnavailableException(
          "Local authority has an invalid badge cost. Cost: " + localAuthority.getBadgeCost());
    }

    GovPayProfile govPayProfile = secretsManager.retrieveLAGovPayProfile(laShortCode);
    if (null == govPayProfile) {
      throw new ServiceUnavailableException("No GOV Pay profile found for LA: " + laShortCode);
    }

    String reference = obtainPaymentReference();

    String language = WELSH.equals(newPaymentDetails.getLanguage()) ? WELSH : ENGLISH;

    CreatePaymentRequest createPaymentReq =
        CreatePaymentRequest.builder()
            .amount(localAuthority.getBadgeCost())
            .description(newPaymentDetails.getPaymentMessage())
            .reference(reference)
            .returnUrl(newPaymentDetails.getReturnUrl())
            .language(language)
            .build();
    PaymentResponse paymentResponse =
        govPayClient.createPayment(govPayProfile.getApiKey(), createPaymentReq);

    UUID paymentUUID =
        persistPayment(laShortCode, localAuthority.getBadgeCost(), reference, paymentResponse);
    return NewPaymentResponse.builder()
        .nextUrl(paymentResponse.getNextUrl())
        .paymentJourneyUuid(paymentUUID)
        .build();
  }

  private static String obtainPaymentReference() {
    return "P" + RandomStringUtils.random(11, REF_CHARS);
  }

  private UUID persistPayment(
      String laShortCode, BigDecimal badgeCost, String reference, PaymentResponse paymentResponse) {
    PaymentEntity paymentEntity =
        PaymentEntity.builder()
            .paymentJourneyUuid(UUID.randomUUID())
            .laShortCode(laShortCode)
            .cost(badgeCost)
            .reference(reference)
            .paymentId(paymentResponse.getPaymentId())
            .status(paymentResponse.getStatus())
            .build();
    paymentRepository.createPayment(paymentEntity);
    return paymentEntity.getPaymentJourneyUuid();
  }

  public PaymentStatusResponse retrievePaymentStatus(UUID paymentJourneyUuid) {
    PaymentEntity paymentEntity =
        paymentRepository.selectPaymentByUuid(paymentJourneyUuid.toString());

    if (null == paymentEntity) {
      throw new NotFoundException("Payment", NotFoundException.Operation.RETRIEVE);
    }

    GovPayProfile govPayProfile =
        secretsManager.retrieveLAGovPayProfile(paymentEntity.getLaShortCode());
    if (null == govPayProfile) {
      throw new ServiceUnavailableException(
          "No GOV Pay profile found for LA: " + paymentEntity.getLaShortCode());
    }

    PaymentResponse paymentResponse =
        govPayClient.retrievePayment(govPayProfile.getApiKey(), paymentEntity.getPaymentId());

    paymentEntity.setStatus(paymentResponse.getStatus());
    paymentRepository.updatePayment(paymentEntity);

    return PaymentStatusResponse.builder()
        .paymentJourneyUuid(paymentJourneyUuid)
        .reference(paymentEntity.getReference())
        .status(paymentResponse.getStatus())
        .build();
  }
}
