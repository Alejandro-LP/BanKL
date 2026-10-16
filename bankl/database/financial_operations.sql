-- BanKL financial operations hardening (MySQL 8.0.16+).
-- Run once against the existing bankl schema before deploying the application.
-- Existing JPA Transaccion rows store simulated consignments and cash-advance charges.

ALTER TABLE transaccion
    ADD CONSTRAINT chk_transaccion_valor_positivo CHECK (valor > 0);

CREATE INDEX idx_transaccion_cuenta_fecha
    ON transaccion (cuenta_id, fecha);

