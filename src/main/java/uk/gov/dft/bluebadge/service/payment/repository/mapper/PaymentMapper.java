package uk.gov.dft.bluebadge.service.payment.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import uk.gov.dft.bluebadge.service.payment.repository.domain.PaymentEntity;

@Mapper
@SuppressWarnings("unused")
public interface PaymentMapper {
  void createPayment(PaymentEntity messageEntity);
}
