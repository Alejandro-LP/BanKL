-- BanKL demo administrator account.
-- Run after creating the `bankl` schema and applying the application tables.
-- These are sample credentials. Change them before using this database beyond a demo.
USE bankl;

START TRANSACTION;

INSERT INTO cliente
    (nombres, apellidos, id, direccion, telefono, email, usuario_is, contrasena, pin_seguridad)
VALUES
    ('Admin', 'BanKL', '00000000', 'Sede principal', '0000000000', 'admin@bankl.com', 'admin', 'Admin123', 1234)
ON DUPLICATE KEY UPDATE id_db = LAST_INSERT_ID(id_db);

SET @bankl_admin_id = LAST_INSERT_ID();

INSERT INTO cliente_admin (id_db)
VALUES (@bankl_admin_id)
ON DUPLICATE KEY UPDATE id_db = VALUES(id_db);

COMMIT;