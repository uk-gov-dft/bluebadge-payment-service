package uk.gov.dft.bluebadge.service.payment.repository;

import lombok.Getter;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;
import uk.gov.dft.bluebadge.service.payment.repository.domain.PaymentEntity;

@Component
public class PaymentRepository {
  @Getter private SqlSession session;

  public PaymentRepository(SqlSession session) {
    this.session = session;
  }

  public int createPayment(PaymentEntity paymentEntity) {
    return session.insert("createPayment", paymentEntity);
  }

  public PaymentEntity selectPaymentByUuid(String paymentJourneyUuid) {
    return session.selectOne("selectPaymentByUuid", paymentJourneyUuid);
  }
}
