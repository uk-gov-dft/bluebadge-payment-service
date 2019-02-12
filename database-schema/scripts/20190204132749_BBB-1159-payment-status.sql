
-- // BBB-1159-payment-status

alter table payment.payment add column status VARCHAR(50) not null default 'created';

-- //@UNDO

alter table payment.payment drop column status;

