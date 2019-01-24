package uk.gov.dft.bluebadge.service.payment;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
  classes = PaymentServiceApplication.class,
  properties = {"management.server.port=0"}
)
@ActiveProfiles({"test", "dev"})
public abstract class ApplicationContextTests {}
