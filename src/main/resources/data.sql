
-- Inserta si no existe (ignora duplicados por UNIQUE username o id)
INSERT IGNORE INTO users (
   id, email, password_hash, active, account_non_locked,
   last_password_change, password_expires_at, failed_login_attempts,
   email_verified, must_change_password
) VALUES
(1, 'admin@app.local',        '$2a$12$6rcp8VNQQPXUSLamkPikNOkWV2zEq5LTlmZH91SSR7x.3QmiJ5ScW', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE),
(2, 'user@app.local',         '$2a$12$6rcp8VNQQPXUSLamkPikNOkWV2zEq5LTlmZH91SSR7x.3QmiJ5ScW', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE),
(3, 'doctor@app.local',       '$2a$12$6rcp8VNQQPXUSLamkPikNOkWV2zEq5LTlmZH91SSR7x.3QmiJ5ScW', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE),
(4, 'blockeduser@app.local',  '$2a$12$6rcp8VNQQPXUSLamkPikNOkWV2zEq5LTlmZH91SSR7x.3QmiJ5ScW', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE),
(5, 'alvaro-segovia-mendoza', '$2a$12$6rcp8VNQQPXUSLamkPikNOkWV2zEq5LTlmZH91SSR7x.3QmiJ5ScW', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE),
(6, 'alvarosm01006@gmail.com','$2a$12$6rcp8VNQQPXUSLamkPikNOkWV2zEq5LTlmZH91SSR7x.3QmiJ5ScW', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE);


-- Insertar los roles
INSERT IGNORE INTO roles (id, name, display_name, description) VALUES
(1, 'ROLE_ADMIN', 'Administrator', 'Acceso total a todas las funcionalidades del sistema'),
(2, 'ROLE_MEDICO', 'Medico', 'Usuario médico'),
(3, 'ROLE_TECNICO', 'Tecnico', 'Usuario técnico'),
(4, 'ROLE_PACIENTE', 'Paciente', 'Usuario paciente');

INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
-- ADMIN
(1, 1),

-- paciente
(2, 4),

-- DOCTOR
(3, 2),

-- paciente adicional
(4, 4),

-- paciente
(5, 4),

-- técnico
(6, 3);

INSERT IGNORE INTO trailers (id, codigo, nombre, activo, descripcion) VALUES
(1, 'TRL-001', 'Trailer Sierra Sur', TRUE, 'Unidad móvil de pruebas médicas');

INSERT IGNORE INTO rutas (id, nombre, origen, destino, activa, trailer_id) VALUES
(1, 'Sevilla-Granada', 'Sevilla', 'Granada', TRUE, 1);

INSERT IGNORE INTO paradas (id, ruta_id, nombre, municipio, direccion, orden_parada, fecha, hora_inicio, hora_fin, capacidad_maxima, activa) VALUES
(1, 1, 'Pueblo 1', 'Osuna', 'Plaza Mayor, s/n', 1, '2026-04-01', '09:00:00', '15:00:00', 2, TRUE),
(2, 1, 'Pueblo 2', 'Loja', 'Avenida de la Estación, 12', 2, '2026-04-02', '09:00:00', '15:00:00', 2, TRUE),
(3, 1, 'Pueblo 3', 'Guadix', 'Recinto ferial', 3, '2026-03-20', '09:00:00', '15:00:00', 2, TRUE),
(4, 1, 'Pueblo 4', 'Écija', 'Centro de salud móvil', 4, '2026-03-25', '09:00:00', '15:00:00', 2, TRUE),
(5, 1, 'Pueblo 5', 'Estepa', 'Calle Real, 8', 5, '2026-04-03', '09:00:00', '15:00:00', 2, TRUE),
(6, 1, 'Pueblo 6', 'Antequera', 'Aparcamiento municipal', 6, '2026-04-04', '09:00:00', '15:00:00', 2, TRUE),
(7, 1, 'Pueblo 7', 'Marchena', 'Plaza del Ayuntamiento', 7, '2026-03-10', '09:00:00', '15:00:00', 2, TRUE);

INSERT IGNORE INTO cita (
    id_cita,
    fecha_hora,
    tipo_prueba,
    estado,
    paciente_id,
    tecnico_id,
    doctor_id,
    parada_id,
    created_at,
    updated_at
) VALUES
-- cita reservada por paciente y asignada automáticamente a técnico
(1, '2026-04-01 10:00:00', 'Analítica', 'PENDIENTE', 5, 6, NULL, 1, NOW(), NOW()),

-- cita confirmada por el técnico
(2, '2026-04-02 11:30:00', 'Radiografía', 'CONFIRMADA', 5, 6, NULL, 2, NOW(), NOW()),

-- cita ya validada por el médico
(3, '2026-03-20 09:00:00', 'Resonancia', 'RESULTADOS_APROBADOS', 5, 6, 3, 3, NOW(), NOW()),

-- cita con resultados subidos pendiente de revisión médica
(4, '2026-03-25 12:00:00', 'Ecografía', 'RESULTADOS_SUBIDOS', 2, 6, NULL, 4, NOW(), NOW()),

-- cita cancelada tras haber sido asignada
(5, '2026-04-03 09:30:00', 'Hemograma', 'CANCELADA', 4, 6, NULL, 5, NOW(), NOW()),

-- cita confirmada con borrador técnico aún no enviado
(6, '2026-04-04 10:30:00', 'Perfil lipídico', 'CONFIRMADA', 5, 6, NULL, 6, NOW(), NOW()),

-- segunda cita aprobada para enriquecer el historial del paciente
(7, '2026-03-10 11:00:00', 'Glucosa', 'RESULTADOS_APROBADOS', 5, 6, 3, 7, NOW(), NOW());

INSERT IGNORE INTO registros_clinicos (
    id, cita_id, paciente_id, tecnico_id, medico_id, tipo_prueba, resultado,
    observaciones_tecnico, observaciones_medico, receta_o_solucion,
    estado, confirmed_at, created_at, updated_at
) VALUES
(1, 3, 5, 6, 3, 'Resonancia', 'Sin hallazgos patológicos relevantes.',
 'Exploración completada correctamente.', 'Resultados validados.', 'Seguimiento rutinario.',
 'CONFIRMADO', NOW(), NOW(), NOW()),
(2, 4, 2, 6, NULL, 'Ecografía', 'Imagen compatible con inflamación leve.',
 'Se remite a valoración médica.', NULL, NULL,
 'PENDIENTE_REVISION', NULL, NOW(), NOW()),
(3, 6, 5, 6, NULL, 'Perfil lipídico', 'Colesterol total elevado pendiente de validación.',
 'Borrador inicial del técnico.', NULL, NULL,
 'BORRADOR', NULL, NOW(), NOW()),
(4, 7, 5, 6, 3, 'Glucosa', 'Glucemia basal ligeramente elevada.',
 'Muestra procesada sin incidencias.', 'Se recomienda control dietético.',
 'Revisión en consulta en 3 meses.',
 'CONFIRMADO', NOW(), NOW(), NOW());

INSERT IGNORE INTO user_profiles (
    user_id, first_name, last_name, phone_number,
    dni, date_of_birth, address, city, province
) VALUES
(1, 'Admin', 'System', '600000001', 'DNI0001', '1980-01-01', 'Calle Admin 1', 'Madrid', 'Madrid'),
(2, 'John', 'Doe', '600000002', 'DNI0002', '1992-04-10', 'Calle Falsa 123', 'Barcelona', 'Barcelona'),
(3, 'Maria', 'Lopez', '600000003', 'DNI0003', '1988-09-21', 'Avenida Central 45', 'Valencia', 'Valencia'),
(4, 'Blocked', 'User', '600000004', 'DNI0004', '1995-02-11', 'Calle Oscura 9', 'Sevilla', 'Sevilla'),
(5, 'Alvaro', 'Segovia Mendoza', '600000005', 'DNI0005', '1998-06-15', 'Calle Desarrollo 7', 'Malaga', 'Malaga'),
(6, 'Alvaro', 'SM', '600000006', 'DNI0006', '2001-01-06', 'Calle Backend 22', 'Granada', 'Granada');
