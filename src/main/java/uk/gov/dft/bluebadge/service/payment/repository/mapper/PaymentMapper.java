package uk.gov.dft.bluebadge.service.payment.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import uk.gov.dft.bluebadge.service.payment.repository.domain.PaymentEntity;

@Mapper
public interface PaymentMapper {
  int createPayment(PaymentEntity messageEntity);

  int updatePayment(PaymentEntity messageEntity);

  PaymentEntity selectPaymentByUuid(@Param("paymentJourneyUuid") String paymentJourneyUuid);
}
