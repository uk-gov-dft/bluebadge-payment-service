SET search_path TO payment;

SET search_path = referencedata;

UPDATE reference_data SET meta_data = meta_data::JSONB || '{"paymentsEnabled": true, "badgeCost": 42}'::JSONB
WHERE data_group_id = 'LA'
  AND code in ('BIRM', 'ANGL', 'BLACK')
;