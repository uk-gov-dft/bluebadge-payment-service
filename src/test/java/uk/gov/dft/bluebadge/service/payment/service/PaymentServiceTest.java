package uk.gov.dft.bluebadge.service.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model.TestLocalAuthorityRefData.localAuthorityRefData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import uk.gov.dft.bluebadge.common.service.exception.BadRequestException;
import uk.gov.dft.bluebadge.common.service.exception.ServiceUnavailableException;
import uk.gov.dft.bluebadge.service.payment.client.govpay.CreatePaymentRequest;
import uk.gov.dft.bluebadge.service.payment.client.govpay.CreatePaymentResponse;
import uk.gov.dft.bluebadge.service.payment.client.govpay.GovPayClient;
import uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model.LocalAuthorityRefData;
import uk.gov.dft.bluebadge.service.payment.controller.model.NewPaymentRequest;
import uk.gov.dft.bluebadge.service.payment.controller.model.NewPaymentResponse;
import uk.gov.dft.bluebadge.service.payment.repository.PaymentRepository;
import uk.gov.dft.bluebadge.service.payment.repository.domain.PaymentEntity;
import uk.gov.dft.bluebadge.service.payment.service.referencedata.ReferenceDataService;

public class PaymentServiceTest {

  private static final String TEST_LA = "TEST_LA";
  private static final String RETURN_URL = "http://test/return";
  private static final String GOV_PAY_API_KEY = "gov_pay_api_key";
  private static final BigDecimal BADGE_COST = new BigDecimal(435L);
  private static final Integer BADGE_COST_IN_PENCE = 43500;

  private PaymentService paymentService;
  @Mock private SecretsManager mockSecretsManager;
  @Mock private GovPayClient mockGovPayClient;
  @Mock private PaymentRepository mockPaymentRepo;
  @Mock private ReferenceDataService mockDataRefService;

  private NewPaymentRequest testPaymentDetails;
  private LocalAuthorityRefData testLa;
  private CreatePaymentResponse govPayResponse;
  private GovPayProfile govPayProfile;

  @Before
  public void setup() {
    initMocks(this);
    paymentService =
        new PaymentService(
            mockSecretsManager, mockGovPayClient, mockPaymentRepo, mockDataRefService);

    testPaymentDetails = new NewPaymentRequest();
    testPaymentDetails.setLaShortCode(TEST_LA);
    testPaymentDetails.setReturnUrl(RETURN_URL);
    testPaymentDetails.setPaymentMessage("Test Blue badge payment");

    testLa = localAuthorityRefData();
    testLa.getLocalAuthorityMetaData().get().setBadgeCost(BADGE_COST);
    govPayResponse =
        CreatePaymentResponse.builder()
            .createdDate(LocalDateTime.now())
            .nextUrl("http://govpaynext")
            .paymentId("gov_pay_is")
            .build();
    govPayProfile = GovPayProfile.builder().apiKey(GOV_PAY_API_KEY).build();
  }

  @Test
  public void createPayment() {

    when(mockDataRefService.getLocalAuthority(TEST_LA)).thenReturn(testLa);
    when(mockSecretsManager.retrieveLAGovPayProfile(TEST_LA)).thenReturn(govPayProfile);
    when(mockGovPayClient.createPayment(any(), any())).thenReturn(govPayResponse);

    NewPaymentResponse result = paymentService.createPayment(testPaymentDetails);

    assertThat(result).isNotNull();
    assertThat(result.getPaymentJourneyUuid()).isNotNull();
    assertThat(result.getNextUrl()).isEqualTo("http://govpaynext");

    verify(mockSecretsManager).retrieveLAGovPayProfile(TEST_LA);
    verify(mockDataRefService).getLocalAuthority(TEST_LA);

    ArgumentCaptor<CreatePaymentRequest> govPayPayment =
        ArgumentCaptor.forClass(CreatePaymentRequest.class);
    verify(mockGovPayClient).createPayment(eq(GOV_PAY_API_KEY), govPayPayment.capture());
    CreatePaymentRequest paymentReq = govPayPayment.getValue();
    assertThat(paymentReq).isNotNull();
    assertThat(paymentReq.getReturnUrl()).isEqualTo(RETURN_URL);
    assertThat(paymentReq.getAmount()).isEqualTo(BADGE_COST_IN_PENCE);
    assertThat(paymentReq.getDescription()).isEqualTo("Test Blue badge payment");
    assertThat(paymentReq.getLanguage()).isEqualTo("en");

    ArgumentCaptor<PaymentEntity> paymentEntityCapture =
        ArgumentCaptor.forClass(PaymentEntity.class);
    verify(mockPaymentRepo).createPayment(paymentEntityCapture.capture());
    PaymentEntity paymentEntity = paymentEntityCapture.getValue();
    assertThat(paymentEntity).isNotNull();
    assertThat(paymentEntity.getCost()).isEqualTo(BADGE_COST);
  }

  @Test
  public void createPayment_welsh() {

    when(mockDataRefService.getLocalAuthority(TEST_LA)).thenReturn(testLa);
    when(mockSecretsManager.retrieveLAGovPayProfile(TEST_LA)).thenReturn(govPayProfile);
    when(mockGovPayClient.createPayment(any(), any())).thenReturn(govPayResponse);

    testPaymentDetails.setLanguage("cy");

    NewPaymentResponse result = paymentService.createPayment(testPaymentDetails);

    assertThat(result).isNotNull();
    assertThat(result.getPaymentJourneyUuid()).isNotNull();
    assertThat(result.getNextUrl()).isEqualTo("http://govpaynext");

    verify(mockSecretsManager).retrieveLAGovPayProfile(TEST_LA);
    verify(mockDataRefService).getLocalAuthority(TEST_LA);

    ArgumentCaptor<CreatePaymentRequest> govPayPayment =
        ArgumentCaptor.forClass(CreatePaymentRequest.class);
    verify(mockGovPayClient).createPayment(eq(GOV_PAY_API_KEY), govPayPayment.capture());
    CreatePaymentRequest paymentReq = govPayPayment.getValue();
    assertThat(paymentReq).isNotNull();
    assertThat(paymentReq.getReturnUrl()).isEqualTo(RETURN_URL);
    assertThat(paymentReq.getAmount()).isEqualTo(BADGE_COST_IN_PENCE);
    assertThat(paymentReq.getDescription()).isEqualTo("Test Blue badge payment");
    assertThat(paymentReq.getLanguage()).isEqualTo("cy");

    ArgumentCaptor<PaymentEntity> paymentEntityCapture =
        ArgumentCaptor.forClass(PaymentEntity.class);
    verify(mockPaymentRepo).createPayment(paymentEntityCapture.capture());
    PaymentEntity paymentEntity = paymentEntityCapture.getValue();
    assertThat(paymentEntity).isNotNull();
    assertThat(paymentEntity.getCost()).isEqualTo(BADGE_COST);
  }

  @Test
  public void createPayment_whenInvalidLA_thenException() {
    when(mockDataRefService.getLocalAuthority(TEST_LA)).thenReturn(null);

    try {
      paymentService.createPayment(testPaymentDetails);
      fail("No exception thrown");
    } catch (BadRequestException e) {
    }

    verifyZeroInteractions(mockSecretsManager);
    verifyZeroInteractions(mockGovPayClient);
    verifyZeroInteractions(mockPaymentRepo);
  }

  @Test
  public void createPayment_whenLAHasntEnabledPayments_thenException() {
    testLa.getLocalAuthorityMetaData().get().setPaymentsEnabled(false);
    when(mockDataRefService.getLocalAuthority(TEST_LA)).thenReturn(testLa);

    try {
      paymentService.createPayment(testPaymentDetails);
      fail("No exception thrown");
    } catch (ServiceUnavailableException e) {

    }

    verifyZeroInteractions(mockSecretsManager);
    verifyZeroInteractions(mockGovPayClient);
    verifyZeroInteractions(mockPaymentRepo);
  }

  @Test
  public void createPayment_whenLAHasNoBadgeCost_thenException() {
    testLa.getLocalAuthorityMetaData().get().setBadgeCost(null);
    when(mockDataRefService.getLocalAuthority(TEST_LA)).thenReturn(testLa);

    try {
      paymentService.createPayment(testPaymentDetails);
      fail("No exception thrown");
    } catch (ServiceUnavailableException e) {
    }

    verifyZeroInteractions(mockSecretsManager);
    verifyZeroInteractions(mockGovPayClient);
    verifyZeroInteractions(mockPaymentRepo);
  }

  @Test
  public void createPayment_whenLAHasInvalidBadgeCost_thenException() {
    testLa.getLocalAuthorityMetaData().get().setBadgeCost(new BigDecimal(0));
    when(mockDataRefService.getLocalAuthority(TEST_LA)).thenReturn(testLa);

    try {
      paymentService.createPayment(testPaymentDetails);
      fail("No exception thrown");
    } catch (ServiceUnavailableException e) {
    }

    verifyZeroInteractions(mockSecretsManager);
    verifyZeroInteractions(mockGovPayClient);
    verifyZeroInteractions(mockPaymentRepo);
  }

  @Test
  public void createPayment_whenLADoesNotHaveGovPaySecret_thenException() {
    when(mockDataRefService.getLocalAuthority(TEST_LA)).thenReturn(testLa);
    when(mockSecretsManager.retrieveLAGovPayProfile(TEST_LA)).thenReturn(null);

    try {
      paymentService.createPayment(testPaymentDetails);
      fail("No exception thrown");
    } catch (ServiceUnavailableException e) {
      assertThat(e.getMessage()).startsWith("No GOV Pay profile found for LA");
    }

    verify(mockSecretsManager).retrieveLAGovPayProfile(TEST_LA);
    verifyZeroInteractions(mockGovPayClient);
    verifyZeroInteractions(mockPaymentRepo);
  }
}
