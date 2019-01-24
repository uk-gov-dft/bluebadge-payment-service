package uk.gov.dft.bluebadge.service.payment.repository;

import lombok.Getter;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

@Component
public class MessageRepository {
  @Getter private SqlSession session;

  public MessageRepository(SqlSession session) {
    this.session = session;
  }

  //  public int createMessage(PaymentEntity messageEntity) {
  //    return session.insert("createMessage", messageEntity);
  //  }
}
