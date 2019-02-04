package uk.gov.dft.bluebadge.service.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.dft.bluebadge.service.payment.client.referencedataservice.model.TestLocalAuthorityRefData.localAuthorityRefData;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
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

  private NewPaymentDetails testPaymentDetails;
  private LocalAuthorityRefData testLa;
  private PaymentResponse govPayResponse;
  private PaymentResponse govPayResponse2;
  private GovPayProfile govPayProfile;

  @Before
  @SneakyThrows
  public void setup() {
    initMocks(this);
    paymentService =
        new PaymentService(
            mockSecretsManager, mockGovPayClient, mockPaymentRepo, mockDataRefService);

    testPaymentDetails = new NewPaymentDetails();
    testPaymentDetails.setLaShortCode(TEST_LA);
    testPaymentDetails.setReturnUrl(RETURN_URL);
    testPaymentDetails.setPaymentMessage("Test Blue badge payment");

    testLa = localAuthorityRefData();
    testLa.getLocalAuthorityMetaData().get().setBadgeCost(BADGE_COST);

    ObjectMapper om = new ObjectMapper();
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    om.registerModule(new JavaTimeModule());
    ClassPathResource jsonResponse =
        new ClassPathResource("testdata/govpay/retrieveResponse_created.json");
    govPayResponse = om.readValue(jsonResponse.getInputStream(), PaymentResponse.class);
    jsonResponse = new ClassPathResource("testdata/govpay/retrieveResponse_success.json");
    govPayResponse2 = om.readValue(jsonResponse.getInputStream(), PaymentResponse.class);

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
    assertThat(result.getNextUrl())
        .isEqualTo(
            "https://www.payments.service.gov.uk/secure/7fa29192-171c-49ba-ac12-06f74eea966b");

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
    assertThat(result.getNextUrl())
        .isEqualTo(
            "https://www.payments.service.gov.uk/secure/7fa29192-171c-49ba-ac12-06f74eea966b");

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

  @Test
  public void retrievePayment() {
    UUID paymentJourneyUuid = UUID.randomUUID();

    PaymentEntity persistedPayment =
        PaymentEntity.builder()
            .paymentJourneyUuid(paymentJourneyUuid)
            .status("created")
            .laShortCode("TEST LA1")
            .paymentId("pay id")
            .reference("test ref")
            .cost(BADGE_COST)
            .build();
    when(mockPaymentRepo.selectPaymentByUuid(paymentJourneyUuid.toString()))
        .thenReturn(persistedPayment);
    when(mockSecretsManager.retrieveLAGovPayProfile("TEST LA1")).thenReturn(govPayProfile);
    when(mockGovPayClient.retrievePayment(govPayProfile.getApiKey(), "pay id"))
        .thenReturn(govPayResponse2);

    PaymentStatusResponse paymentStatusResponse =
        paymentService.retrievePaymentStatus(paymentJourneyUuid);

    assertThat(paymentStatusResponse).isNotNull();

    verify(mockGovPayClient).retrievePayment(govPayProfile.getApiKey(), "pay id");
    verify(mockSecretsManager).retrieveLAGovPayProfile("TEST LA1");

    ArgumentCaptor<PaymentEntity> paymentEntityCapture =
        ArgumentCaptor.forClass(PaymentEntity.class);
    verify(mockPaymentRepo).updatePayment(paymentEntityCapture.capture());
    PaymentEntity paymentEntity = paymentEntityCapture.getValue();
    assertThat(paymentEntity).isNotNull();
    assertThat(paymentEntity.getCost()).isEqualTo(BADGE_COST);
    assertThat(paymentEntity.getStatus()).isEqualTo("success");
    assertThat(paymentEntity.getLaShortCode()).isEqualTo("TEST LA1");
  }

  @Test
  public void retrievePayment_notFound() {
    UUID paymentJourneyUuid = UUID.randomUUID();

    when(mockPaymentRepo.selectPaymentByUuid(paymentJourneyUuid.toString())).thenReturn(null);

    try {
      paymentService.retrievePaymentStatus(paymentJourneyUuid);
      fail("No exception thrown");
    } catch (NotFoundException e) {
      assertThat(e.getMessage()).isNotBlank();
    }

    verifyZeroInteractions(mockGovPayClient);
    verifyZeroInteractions(mockSecretsManager);
    verify(mockPaymentRepo, never()).updatePayment(any());
  }

  @Test
  public void retrievePayment_whenLADoesNotHaveGovPaySecret_thenException() {
    UUID paymentJourneyUuid = UUID.randomUUID();
    PaymentEntity persistedPayment =
        PaymentEntity.builder()
            .paymentJourneyUuid(paymentJourneyUuid)
            .status("created")
            .laShortCode("TEST LA1")
            .paymentId("pay id")
            .reference("test ref")
            .cost(BADGE_COST)
            .build();
    when(mockPaymentRepo.selectPaymentByUuid(paymentJourneyUuid.toString()))
        .thenReturn(persistedPayment);
    when(mockSecretsManager.retrieveLAGovPayProfile("TEST LA1")).thenReturn(null);

    try {
      paymentService.retrievePaymentStatus(paymentJourneyUuid);
      fail("No exception thrown");
    } catch (ServiceUnavailableException e) {
      assertThat(e.getMessage()).startsWith("No GOV Pay profile found for LA");
    }

    verify(mockSecretsManager).retrieveLAGovPayProfile("TEST LA1");
    verifyZeroInteractions(mockGovPayClient);
    verify(mockPaymentRepo, never()).updatePayment(any());
  }
}
