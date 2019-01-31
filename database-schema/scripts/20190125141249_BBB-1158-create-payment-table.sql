-- // BBB-1158-create-payment-table

DROP TABLE IF EXISTS payment.payment;

CREATE TABLE payment.payment(
  payment_journey_uuid UUID NOT NULL,
  payment_id                VARCHAR (150) NOT NULL,
  la_short_code             VARCHAR(10) NOT NULL,
  reference                 VARCHAR(100) NOT NULL,
  cost                      INTEGER NOT NULL,
  created_on                TIMESTAMP NOT NULL DEFAULT now(),
  PRIMARY KEY (payment_journey_uuid)
);


-- //@UNDO

DROP TABLE IF EXISTS payment.payment;

