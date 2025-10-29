-- =========================================================
--  BASE DE DATOS: SFP (Simulador Finanzas Personales)
-- =========================================================
DROP DATABASE IF EXISTS SFP;
CREATE DATABASE SFP CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE SFP;

-- =========================================================
--  LIMPIEZA (ORDEN FK)
-- =========================================================
DROP TABLE IF EXISTS LOG_EVENTO;
DROP TABLE IF EXISTS TRANSACCION;
DROP TABLE IF EXISTS CATEGORIA;
DROP TABLE IF EXISTS CUENTA;
DROP TABLE IF EXISTS CONFIGURACION_USUARIO;
DROP TABLE IF EXISTS USUARIO;
DROP TABLE IF EXISTS ROL;

-- =========================================================
--  TABLAS BÁSICAS
-- =========================================================

CREATE TABLE ROL (
                     id_rol BIGINT NOT NULL AUTO_INCREMENT,
                     nombre VARCHAR(50) NOT NULL UNIQUE,
                     descripcion TEXT,
                     PRIMARY KEY (id_rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE USUARIO (
                         id_usuario BIGINT NOT NULL AUTO_INCREMENT,
                         nombre VARCHAR(100) NOT NULL,
                         apellido_paterno VARCHAR(50),
                         apellido_materno VARCHAR(50),
                         email VARCHAR(120) NOT NULL UNIQUE,
                         contrasena VARCHAR(255) NOT NULL,
                         fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         id_rol BIGINT NOT NULL,
                         PRIMARY KEY (id_usuario),
                         CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol)
                             REFERENCES ROL(id_rol)
                             ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Preferencias/Regla 50/30/20 por usuario (ajustable)
CREATE TABLE CONFIGURACION_USUARIO (
                                       id_config BIGINT NOT NULL AUTO_INCREMENT,
                                       usuario_id BIGINT NOT NULL UNIQUE,
                                       p_necesidades DECIMAL(5,2) NOT NULL DEFAULT 50.00,
                                       p_deseos      DECIMAL(5,2) NOT NULL DEFAULT 30.00,
                                       p_ahorro      DECIMAL(5,2) NOT NULL DEFAULT 20.00,
                                       PRIMARY KEY (id_config),
                                       CONSTRAINT fk_conf_usuario FOREIGN KEY (usuario_id)
                                           REFERENCES USUARIO(id_usuario)
                                           ON UPDATE CASCADE ON DELETE CASCADE,
                                       CONSTRAINT chk_conf_suma CHECK (ROUND(p_necesidades + p_deseos + p_ahorro, 2) = 100.00),
                                       CONSTRAINT chk_conf_rangos CHECK (
                                           p_necesidades BETWEEN 0 AND 100 AND
                                           p_deseos      BETWEEN 0 AND 100 AND
                                           p_ahorro      BETWEEN 0 AND 100
                                           )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
--  CUENTAS
-- =========================================================
CREATE TABLE CUENTA (
                        id_cuenta BIGINT NOT NULL AUTO_INCREMENT,
                        usuario_id BIGINT NOT NULL,         -- dueño de la cuenta
                        nombre VARCHAR(100) NOT NULL,
                        tipo ENUM('EFECTIVO','DEBITO','CREDITO','OTRO') NOT NULL,
                        saldo DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                        moneda CHAR(3) NOT NULL DEFAULT 'MXN',    -- ISO 4217
                        dia_corte TINYINT NULL,                   -- 1..31
                        dia_pago  TINYINT NULL,                   -- 1..31
                        limite_credito DECIMAL(15,2) NULL,
                        created_by BIGINT NULL,
                        updated_by BIGINT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (id_cuenta),
                        CONSTRAINT fk_cuenta_usuario FOREIGN KEY (usuario_id)
                            REFERENCES USUARIO(id_usuario)
                            ON UPDATE CASCADE ON DELETE CASCADE,
                        CONSTRAINT fk_cuenta_created_by FOREIGN KEY (created_by)
                            REFERENCES USUARIO(id_usuario)
                            ON UPDATE CASCADE ON DELETE SET NULL,
                        CONSTRAINT fk_cuenta_updated_by FOREIGN KEY (updated_by)
                            REFERENCES USUARIO(id_usuario)
                            ON UPDATE CASCADE ON DELETE SET NULL,
                        CONSTRAINT chk_cuenta_dias CHECK (
                            (dia_corte IS NULL OR dia_corte BETWEEN 1 AND 31) AND
                            (dia_pago  IS NULL OR dia_pago  BETWEEN 1 AND 31)
                            ),
                        CONSTRAINT chk_limite_no_neg CHECK (limite_credito IS NULL OR limite_credito >= 0),
                        UNIQUE KEY uq_cuenta_usuario_nombre (usuario_id, nombre),
                        INDEX idx_cuenta_tipo (tipo),
                        INDEX idx_cuenta_moneda (moneda)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
--  CATEGORÍAS Y TRANSACCIONES
-- =========================================================
-- CATEGORIA: puede ser global (usuario_id NULL) o del usuario
CREATE TABLE CATEGORIA (
                           id_categoria BIGINT NOT NULL AUTO_INCREMENT,
                           usuario_id BIGINT NULL,                 -- NULL => global/por defecto
                           nombre VARCHAR(80) NOT NULL,
                           tipo ENUM('INGRESO','EGRESO') NOT NULL,
                           clasificacion ENUM('NECESIDAD','DESEO','AHORRO') NULL, -- solo aplica a EGRESO
                           PRIMARY KEY (id_categoria),
                           CONSTRAINT fk_cat_usuario FOREIGN KEY (usuario_id)
                               REFERENCES USUARIO(id_usuario)
                               ON UPDATE CASCADE ON DELETE SET NULL,
                           CONSTRAINT uq_categoria UNIQUE (usuario_id, nombre),
                           CONSTRAINT chk_cat_clasif CHECK (
                               (tipo='INGRESO' AND clasificacion IS NULL)
                                   OR
                               (tipo='EGRESO' AND clasificacion IN ('NECESIDAD','DESEO','AHORRO'))
                               )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE TRANSACCION (
                             id_transaccion BIGINT NOT NULL AUTO_INCREMENT,
                             usuario_id BIGINT NOT NULL,   -- quien registra (útil en cuentas compartidas)
                             cuenta_id BIGINT NOT NULL,
                             categoria_id BIGINT NOT NULL,
                             fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             monto DECIMAL(15,2) NOT NULL, -- signo validado por trigger segun categoria.tipo
                             descripcion TEXT NULL,
                             PRIMARY KEY (id_transaccion),
                             CONSTRAINT fk_tx_usuario FOREIGN KEY (usuario_id)
                                 REFERENCES USUARIO(id_usuario)
                                 ON UPDATE CASCADE ON DELETE RESTRICT,
                             CONSTRAINT fk_tx_cuenta FOREIGN KEY (cuenta_id)
                                 REFERENCES CUENTA(id_cuenta)
                                 ON UPDATE CASCADE ON DELETE RESTRICT,
                             CONSTRAINT fk_tx_categoria FOREIGN KEY (categoria_id)
                                 REFERENCES CATEGORIA(id_categoria)
                                 ON UPDATE CASCADE ON DELETE RESTRICT,
                             CONSTRAINT chk_tx_monto_no_cero CHECK (monto <> 0),
                             INDEX idx_tx_cuenta_fecha (cuenta_id, fecha),
                             INDEX idx_tx_categoria_fecha (categoria_id, fecha),
                             INDEX idx_tx_usuario_fecha (usuario_id, fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
--  LOGS / AUDITORÍA
-- =========================================================
CREATE TABLE LOG_EVENTO (
                            id_log BIGINT NOT NULL AUTO_INCREMENT,
                            usuario_id BIGINT NULL,
                            fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            tipo_evento VARCHAR(80) NOT NULL, -- p.ej. LOGIN_EXITO, NUEVO_MOVIMIENTO, etc.
                            detalle TEXT NULL,
                            PRIMARY KEY (id_log),
                            INDEX idx_log_usuario_fecha (usuario_id, fecha),
                            CONSTRAINT fk_log_usuario FOREIGN KEY (usuario_id)
                                REFERENCES USUARIO(id_usuario)
                                ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================
--  TRIGGERS: VALIDACIÓN Y SALDOS
-- =========================================================
DELIMITER $$

-- Valida signo del monto según categoria.tipo
CREATE TRIGGER trg_tx_validate_sign_before_ins
    BEFORE INSERT ON TRANSACCION
    FOR EACH ROW
BEGIN
    DECLARE v_tipo VARCHAR(10);
    SELECT tipo INTO v_tipo FROM CATEGORIA WHERE id_categoria = NEW.categoria_id;
    IF v_tipo IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Categoria no existe';
    ELSEIF v_tipo = 'EGRESO' AND NEW.monto >= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'EGRESO debe ser monto negativo';
    ELSEIF v_tipo = 'INGRESO' AND NEW.monto <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'INGRESO debe ser monto positivo';
    END IF;
END$$

CREATE TRIGGER trg_tx_validate_sign_before_upd
    BEFORE UPDATE ON TRANSACCION
    FOR EACH ROW
BEGIN
    DECLARE v_tipo VARCHAR(10);
    SELECT tipo INTO v_tipo FROM CATEGORIA WHERE id_categoria = NEW.categoria_id;
    IF v_tipo IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Categoria no existe';
    ELSEIF v_tipo = 'EGRESO' AND NEW.monto >= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'EGRESO debe ser monto negativo';
    ELSEIF v_tipo = 'INGRESO' AND NEW.monto <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'INGRESO debe ser monto positivo';
    END IF;
END$$

-- Actualiza saldo de la cuenta en insert/update/delete
CREATE TRIGGER trg_tx_after_insert
    AFTER INSERT ON TRANSACCION
    FOR EACH ROW
BEGIN
    UPDATE CUENTA SET saldo = saldo + NEW.monto WHERE id_cuenta = NEW.cuenta_id;
END$$

CREATE TRIGGER trg_tx_after_update
    AFTER UPDATE ON TRANSACCION
    FOR EACH ROW
BEGIN
    IF NEW.cuenta_id = OLD.cuenta_id THEN
        UPDATE CUENTA
        SET saldo = saldo + (NEW.monto - OLD.monto)
        WHERE id_cuenta = NEW.cuenta_id;
    ELSE
        -- Reversa en cuenta anterior
        UPDATE CUENTA SET saldo = saldo - OLD.monto
        WHERE id_cuenta = OLD.cuenta_id;
        -- Aplica en nueva cuenta
        UPDATE CUENTA SET saldo = saldo + NEW.monto
        WHERE id_cuenta = NEW.cuenta_id;
    END IF;
END$$

CREATE TRIGGER trg_tx_after_delete
    AFTER DELETE ON TRANSACCION
    FOR EACH ROW
BEGIN
    UPDATE CUENTA SET saldo = saldo - OLD.monto WHERE id_cuenta = OLD.cuenta_id;
END$$

DELIMITER ;

-- =========================================================
--  VISTAS PARA DASHBOARD (mes como DATE - primer día de mes)
-- =========================================================

CREATE OR REPLACE VIEW v_resumen_mensual AS
SELECT
    t.usuario_id,
    STR_TO_DATE(DATE_FORMAT(t.fecha, '%Y-%m-01'), '%Y-%m-%d') AS mes,
    SUM(CASE WHEN c.tipo='INGRESO' THEN t.monto ELSE 0 END) AS ingresos,
    SUM(CASE WHEN c.tipo='EGRESO' THEN -t.monto ELSE 0 END) AS egresos, -- egresos positivos
    SUM(t.monto) AS neto
FROM TRANSACCION t
         JOIN CATEGORIA c ON c.id_categoria = t.categoria_id
GROUP BY t.usuario_id, STR_TO_DATE(DATE_FORMAT(t.fecha, '%Y-%m-01'), '%Y-%m-%d');

CREATE OR REPLACE VIEW v_503020_mensual AS
SELECT
    t.usuario_id,
    STR_TO_DATE(DATE_FORMAT(t.fecha, '%Y-%m-01'), '%Y-%m-%d') AS mes,
    SUM(CASE WHEN c.clasificacion='NECESIDAD' THEN -t.monto ELSE 0 END) AS necesidades,
    SUM(CASE WHEN c.clasificacion='DESEO'      THEN -t.monto ELSE 0 END) AS deseos,
    SUM(CASE WHEN c.clasificacion='AHORRO'     THEN -t.monto ELSE 0 END) AS ahorro
FROM TRANSACCION t
         JOIN CATEGORIA c ON c.id_categoria = t.categoria_id
WHERE c.tipo='EGRESO'
GROUP BY t.usuario_id, STR_TO_DATE(DATE_FORMAT(t.fecha, '%Y-%m-01'), '%Y-%m-%d');

CREATE OR REPLACE VIEW v_gasto_categoria_mes AS
SELECT
    t.usuario_id,
    DATE_FORMAT(t.fecha, '%Y-%m') AS mes,
    c.nombre AS categoria,
    SUM(CASE WHEN c.tipo='EGRESO' THEN -t.monto ELSE 0 END) AS total_gastado
FROM TRANSACCION t
         JOIN CATEGORIA c ON c.id_categoria = t.categoria_id
WHERE c.tipo='EGRESO'
GROUP BY t.usuario_id, DATE_FORMAT(t.fecha, '%Y-%m'), c.nombre;

-- =========================================================
--  SEEDS BÁSICOS
-- =========================================================
INSERT INTO ROL (nombre, descripcion) VALUES
                                          ('ADMIN',   'Administrador del sistema'),
                                          ('USUARIO', 'Usuario estándar');

-- Categorías globales por defecto (usuario_id = NULL)
INSERT INTO CATEGORIA (usuario_id, nombre, tipo, clasificacion) VALUES
                                                                    (NULL, 'Salario',        'INGRESO', NULL),
                                                                    (NULL, 'Venta',          'INGRESO', NULL),
                                                                    (NULL, 'Comida',         'EGRESO',  'NECESIDAD'),
                                                                    (NULL, 'Renta',          'EGRESO',  'NECESIDAD'),
                                                                    (NULL, 'Transporte',     'EGRESO',  'NECESIDAD'),
                                                                    (NULL, 'Entretenimiento','EGRESO',  'DESEO'),
                                                                    (NULL, 'Vacaciones',     'EGRESO',  'DESEO'),
                                                                    (NULL, 'Ahorro',         'EGRESO',  'AHORRO');

-- Usuario demo (hash de ejemplo; reemplazar por BCrypt real)
INSERT INTO USUARIO (nombre, email, contrasena, id_rol)
VALUES ('Demo', 'demo@ejemplo.com', '$2y$12$HASH_DE_EJEMPLO_NO_REAL', (SELECT id_rol FROM ROL WHERE nombre='USUARIO'));

INSERT INTO CONFIGURACION_USUARIO (usuario_id, p_necesidades, p_deseos, p_ahorro)
SELECT id_usuario, 50.00, 30.00, 20.00 FROM USUARIO WHERE email='demo@ejemplo.com';
