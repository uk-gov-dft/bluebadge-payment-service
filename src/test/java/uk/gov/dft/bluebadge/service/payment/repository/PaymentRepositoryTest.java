package uk.gov.dft.bluebadge.service.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
            .cost(999L)
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
    assertThat(persisted.getCost()).isEqualTo(999L);
  }
}
