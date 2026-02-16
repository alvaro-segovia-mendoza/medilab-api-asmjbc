-- INSERTS para la tabla tecnico
INSERT INTO tecnico (nombre, apellidos, email, contrasena, telefono, activo, dni, fecha_nacimiento, direccion, localidad, provincia)
VALUES
    ('Juan', 'Pérez García', 'juan.perez@example.com', '1234abcd', '600123456', TRUE, '12345678A', '1980-05-12', 'Calle Mayor 1', 'Sevilla', 'Sevilla'),
    ('Ana', 'López Martínez', 'ana.lopez@example.com', 'abcd1234', '600654321', TRUE, '87654321B', '1990-10-20', 'Avenida de la Constitución 10', 'Sevilla', 'Sevilla'),
    ('Carlos', 'Sánchez Ruiz', 'carlos.sanchez@example.com', 'pass1234', '600987654', FALSE, '11223344C', '1985-07-05', 'Calle Feria 15', 'Sevilla', 'Sevilla');


-- Inserta si no existe (ignora duplicados por UNIQUE username o id)
INSERT IGNORE INTO users (
   id, email, password_hash, active, account_non_locked,
   last_password_change, password_expires_at, failed_login_attempts,
   email_verified, must_change_password
) VALUES
(1, 'admin@app.local',        '$2a$10$fqQKWrboFqmabvtVozNho.fEawXBw61s764RxUhd4cMwEJWnt.V9O',  TRUE,  TRUE,  NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE,  FALSE),
(2, 'jdoe@app.local',         '$2a$10$fqQKWrboFqmabvtVozNho.fEawXBw61s764RxUhd4cMwEJWnt.V9O',      TRUE,  TRUE,  NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 1, FALSE, FALSE),
(3, 'maria@app.local',        '$2a$10$fqQKWrboFqmabvtVozNho.fEawXBw61s764RxUhd4cMwEJWnt.V9O',  TRUE,  TRUE,  NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE,  TRUE ),
(4, 'blockeduser@app.local',  '$2a$10$fqQKWrboFqmabvtVozNho.fEawXBw61s764RxUhd4cMwEJWnt.V9O',    FALSE, FALSE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 5, FALSE, FALSE),
(5, 'alvaro-segovia-mendoza',  '$2a$10$fqQKWrboFqmabvtVozNho.fEawXBw61s764RxUhd4cMwEJWnt.V9O',    FALSE, FALSE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 5, FALSE, FALSE),
(6, 'alvarosm01006@gmail.com',  '$2a$10$fqQKWrboFqmabvtVozNho.fEawXBw61s764RxUhd4cMwEJWnt.V9O',    FALSE, FALSE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 5, FALSE, FALSE);


       -- Insertar los roles
INSERT IGNORE INTO roles (id, name, display_name, description) VALUES
(1, 'ROLE_ADMIN', 'Administrator', 'Acceso total a todas las funcionalidades del sistema'),
(2, 'ROLE_USER', 'User', 'Usuario estándar'),
(3, 'ROLE_MANAGER', 'Manager', 'Usuario gestor de la aplicación tiene acceso a las funcionalidades de gestión de datos');


INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
-- Usuario 1: admin completo
(1, 1),  -- ROLE_ADMIN
(1, 2),  -- ROLE_USER
-- Usuario 2: usuario estándar
(2, 2),  -- ROLE_USER
-- Usuario 3: manager con permisos de usuario
(3, 3),  -- ROLE_MANAGER
(3, 2),  -- ROLE_USER
(5,1),
(5,2),
(5,3),
(6,1),
(6,2),
(6,3);
-- INSERTS para la tabla cita
INSERT INTO cita (codigo, fecha_hora, tipo_prueba, estado, id_tecnico)
VALUES
    ('CITA-001','2025-12-15 10:00:00', 'Revisión general', 'PENDIENTE', 1),
    ('CITA-002','2025-12-16 14:30:00', 'Prueba eléctrica', 'ACEPTADA', 2),
    ('CITA-003','2025-12-17 09:00:00', 'Mantenimiento', 'CANCELADA', 1),
    ('CITA-004','2025-12-18 11:00:00', 'Revisión general', 'FINALIZADA', 3);
