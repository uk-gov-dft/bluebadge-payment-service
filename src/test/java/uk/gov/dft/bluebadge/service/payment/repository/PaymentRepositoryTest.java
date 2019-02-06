package uk.gov.dft.bluebadge.service.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.dft.bluebadge.service.payment.ApplicationContextTests;
import uk.gov.dft.bluebadge.service.payment.repository.domain.PaymentEntity;

@RunWith(SpringRunner.class)
@Transactional
public class PaymentRepositoryTest extends ApplicationContextTests {

  @Autowired private PaymentRepository paymentRepository;

  @Test
  public void create() {
    UUID paymentJourneyUuid = UUID.randomUUID();
    PaymentEntity paymentEntity =
        PaymentEntity.builder()
            .reference("ref repo test")
            .paymentId("pay id")
            .laShortCode("TEST")
            .paymentJourneyUuid(paymentJourneyUuid)
            .cost(new BigDecimal(999))
            .status("created")
            .build();

    int insertCount = paymentRepository.createPayment(paymentEntity);
    assertThat(insertCount).isEqualTo(1);

    PaymentEntity persisted = paymentRepository.selectPaymentByUuid(paymentJourneyUuid.toString());
    assertThat(persisted).isNotNull();
    assertThat(persisted).isNotSameAs(paymentEntity);
    assertThat(persisted.getPaymentJourneyUuid()).isEqualTo(paymentJourneyUuid);
    assertThat(persisted.getReference()).isEqualTo("ref repo test");
    assertThat(persisted.getPaymentId()).isEqualTo("pay id");
    assertThat(persisted.getLaShortCode()).isEqualTo("TEST");
    assertThat(persisted.getCost()).isEqualTo(new BigDecimal("999.00"));
    assertThat(persisted.getStatus()).isEqualTo("created");
  }

  @Test
  public void updatePayment() {
    UUID paymentJourneyUuid = UUID.randomUUID();
    {
      PaymentEntity paymentEntity =
          PaymentEntity.builder()
              .reference("ref repo test")
              .paymentId("pay id")
              .laShortCode("TEST")
              .paymentJourneyUuid(paymentJourneyUuid)
              .cost(new BigDecimal(999))
              .status("created")
              .build();

      paymentRepository.createPayment(paymentEntity);
    }

    PaymentEntity paymentEntityToUpdate =
        PaymentEntity.builder()
            .reference("not updated")
            .paymentId("not updated")
            .laShortCode("not updated")
            .paymentJourneyUuid(paymentJourneyUuid)
            .cost(new BigDecimal(987987))
            .status("created")
            .build();

    paymentEntityToUpdate.setStatus("success");
    paymentRepository.updatePayment(paymentEntityToUpdate);

    PaymentEntity persisted = paymentRepository.selectPaymentByUuid(paymentJourneyUuid.toString());
    assertThat(persisted).isNotNull();
    assertThat(persisted).isNotSameAs(paymentEntityToUpdate);
    assertThat(persisted.getPaymentJourneyUuid()).isEqualTo(paymentJourneyUuid);
    assertThat(persisted.getReference()).isEqualTo("ref repo test");
    assertThat(persisted.getPaymentId()).isEqualTo("pay id");
    assertThat(persisted.getLaShortCode()).isEqualTo("TEST");
    assertThat(persisted.getCost()).isEqualTo(new BigDecimal("999.00"));

    assertThat(persisted.getStatus()).isEqualTo("success");
  }
}
