
-- Inserta si no existe (ignora duplicados por UNIQUE username o id)
INSERT IGNORE INTO users (
   id, email, password_hash, active, account_non_locked,
   last_password_change, password_expires_at, failed_login_attempts,
   email_verified, must_change_password
) VALUES
(1, 'admin@app.local',        '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE),
(2, 'paciente@app.local',         '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE),
(3, 'doctor@app.local',       '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE),
(4, 'blockeduser@app.local',  '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE),
(5, 'alvaro-segovia-mendoza', '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE),
(6, 'tecnico@gmail.com','$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 0, TRUE, FALSE);


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
(1, 'TRL-001', 'Trailer Aljarafe y Sierra Norte', TRUE, 'Unidad movil de pruebas medicas');

INSERT IGNORE INTO rutas (id, nombre, origen, destino, activa, trailer_id) VALUES
(1, 'Ruta Sierra Norte Cordobesa desde Aljarafe', 'Castilleja de la Cuesta', 'Cardena', TRUE, 1);

-- Regla de negocio: un trailer no puede tener 2 paradas en el mismo dia.
-- Cada parada ocupa todo el dia (09:00-15:00), por lo que cada parada es en un dia distinto.
INSERT IGNORE INTO paradas (id, ruta_id, nombre, municipio, provincia, direccion, orden_parada, fecha, hora_inicio, hora_fin, capacidad_maxima, activa) VALUES
(1, 1, 'Centro de Salud de Castilleja de la Cuesta', 'Castilleja de la Cuesta', 'Sevilla',        'Av. Juan Carlos I, s/n',      1, '2026-04-21', '09:00:00', '15:00:00', 2, TRUE),
(2, 1, 'Recinto sanitario movil de Bormujos',        'Bormujos',                'Sevilla',        'Zona anexa al hospital',      2, '2026-04-22', '09:00:00', '15:00:00', 2, TRUE),
(3, 1, 'Plaza de Andalucia de Espiel',               'Espiel',                  'Cordoba',        'Plaza de Andalucia',          3, '2026-04-23', '09:00:00', '15:00:00', 2, TRUE),
(4, 1, 'Consultorio local de Villaviciosa de Cordoba','Villaviciosa de Cordoba', 'Cordoba',        'Calle Nueva, 4',              4, '2026-04-24', '09:00:00', '15:00:00', 2, TRUE),
(5, 1, 'Centro de Salud de Cardena',                 'Cardena',                 'Cordoba',        'Calle del Consultorio, 2',    5, '2026-04-25', '09:00:00', '15:00:00', 2, TRUE);

-- Slots generados: 12 intervalos de 30 min (09:00-15:00) x 2 cupos = 24 slots por parada, 120 en total.
-- Slots ocupados por citas activas marcados como RESERVADO.
INSERT IGNORE INTO slot_cita (id, parada_id, fecha_hora_inicio, fecha_hora_fin, cupo_numero, estado) VALUES
-- Parada 1 - 2026-04-21 (slots 1-24)
(1,  1, '2026-04-21 09:00:00', '2026-04-21 09:30:00', 1, 'DISPONIBLE'),
(2,  1, '2026-04-21 09:00:00', '2026-04-21 09:30:00', 2, 'DISPONIBLE'),
(3,  1, '2026-04-21 09:30:00', '2026-04-21 10:00:00', 1, 'DISPONIBLE'),
(4,  1, '2026-04-21 09:30:00', '2026-04-21 10:00:00', 2, 'DISPONIBLE'),
(5,  1, '2026-04-21 10:00:00', '2026-04-21 10:30:00', 1, 'DISPONIBLE'),
(6,  1, '2026-04-21 10:00:00', '2026-04-21 10:30:00', 2, 'DISPONIBLE'),
(7,  1, '2026-04-21 10:30:00', '2026-04-21 11:00:00', 1, 'DISPONIBLE'),
(8,  1, '2026-04-21 10:30:00', '2026-04-21 11:00:00', 2, 'DISPONIBLE'),
(9,  1, '2026-04-21 11:00:00', '2026-04-21 11:30:00', 1, 'DISPONIBLE'),
(10, 1, '2026-04-21 11:00:00', '2026-04-21 11:30:00', 2, 'DISPONIBLE'),
(11, 1, '2026-04-21 11:30:00', '2026-04-21 12:00:00', 1, 'DISPONIBLE'),
(12, 1, '2026-04-21 11:30:00', '2026-04-21 12:00:00', 2, 'DISPONIBLE'),
(13, 1, '2026-04-21 12:00:00', '2026-04-21 12:30:00', 1, 'DISPONIBLE'),
(14, 1, '2026-04-21 12:00:00', '2026-04-21 12:30:00', 2, 'DISPONIBLE'),
(15, 1, '2026-04-21 12:30:00', '2026-04-21 13:00:00', 1, 'DISPONIBLE'),
(16, 1, '2026-04-21 12:30:00', '2026-04-21 13:00:00', 2, 'DISPONIBLE'),
(17, 1, '2026-04-21 13:00:00', '2026-04-21 13:30:00', 1, 'DISPONIBLE'),
(18, 1, '2026-04-21 13:00:00', '2026-04-21 13:30:00', 2, 'DISPONIBLE'),
(19, 1, '2026-04-21 13:30:00', '2026-04-21 14:00:00', 1, 'DISPONIBLE'),
(20, 1, '2026-04-21 13:30:00', '2026-04-21 14:00:00', 2, 'DISPONIBLE'),
(21, 1, '2026-04-21 14:00:00', '2026-04-21 14:30:00', 1, 'DISPONIBLE'),
(22, 1, '2026-04-21 14:00:00', '2026-04-21 14:30:00', 2, 'DISPONIBLE'),
(23, 1, '2026-04-21 14:30:00', '2026-04-21 15:00:00', 1, 'DISPONIBLE'),
(24, 1, '2026-04-21 14:30:00', '2026-04-21 15:00:00', 2, 'DISPONIBLE'),
-- Parada 2 - 2026-04-22 (slots 25-48)
(25, 2, '2026-04-22 09:00:00', '2026-04-22 09:30:00', 1, 'DISPONIBLE'),
(26, 2, '2026-04-22 09:00:00', '2026-04-22 09:30:00', 2, 'DISPONIBLE'),
(27, 2, '2026-04-22 09:30:00', '2026-04-22 10:00:00', 1, 'DISPONIBLE'),
(28, 2, '2026-04-22 09:30:00', '2026-04-22 10:00:00', 2, 'DISPONIBLE'),
(29, 2, '2026-04-22 10:00:00', '2026-04-22 10:30:00', 1, 'RESERVADO'),  -- cita 1 (PENDIENTE)
(30, 2, '2026-04-22 10:00:00', '2026-04-22 10:30:00', 2, 'DISPONIBLE'),
(31, 2, '2026-04-22 10:30:00', '2026-04-22 11:00:00', 1, 'RESERVADO'),  -- cita 2 (CONFIRMADA)
(32, 2, '2026-04-22 10:30:00', '2026-04-22 11:00:00', 2, 'DISPONIBLE'),
(33, 2, '2026-04-22 11:00:00', '2026-04-22 11:30:00', 1, 'DISPONIBLE'),
(34, 2, '2026-04-22 11:00:00', '2026-04-22 11:30:00', 2, 'DISPONIBLE'),
(35, 2, '2026-04-22 11:30:00', '2026-04-22 12:00:00', 1, 'DISPONIBLE'),
(36, 2, '2026-04-22 11:30:00', '2026-04-22 12:00:00', 2, 'DISPONIBLE'),
(37, 2, '2026-04-22 12:00:00', '2026-04-22 12:30:00', 1, 'DISPONIBLE'),
(38, 2, '2026-04-22 12:00:00', '2026-04-22 12:30:00', 2, 'DISPONIBLE'),
(39, 2, '2026-04-22 12:30:00', '2026-04-22 13:00:00', 1, 'DISPONIBLE'),
(40, 2, '2026-04-22 12:30:00', '2026-04-22 13:00:00', 2, 'DISPONIBLE'),
(41, 2, '2026-04-22 13:00:00', '2026-04-22 13:30:00', 1, 'DISPONIBLE'),
(42, 2, '2026-04-22 13:00:00', '2026-04-22 13:30:00', 2, 'DISPONIBLE'),
(43, 2, '2026-04-22 13:30:00', '2026-04-22 14:00:00', 1, 'DISPONIBLE'),
(44, 2, '2026-04-22 13:30:00', '2026-04-22 14:00:00', 2, 'DISPONIBLE'),
(45, 2, '2026-04-22 14:00:00', '2026-04-22 14:30:00', 1, 'DISPONIBLE'),
(46, 2, '2026-04-22 14:00:00', '2026-04-22 14:30:00', 2, 'DISPONIBLE'),
(47, 2, '2026-04-22 14:30:00', '2026-04-22 15:00:00', 1, 'DISPONIBLE'),
(48, 2, '2026-04-22 14:30:00', '2026-04-22 15:00:00', 2, 'DISPONIBLE'),
-- Parada 3 - 2026-04-23 (slots 49-72)
(49, 3, '2026-04-23 09:00:00', '2026-04-23 09:30:00', 1, 'DISPONIBLE'),
(50, 3, '2026-04-23 09:00:00', '2026-04-23 09:30:00', 2, 'DISPONIBLE'),
(51, 3, '2026-04-23 09:30:00', '2026-04-23 10:00:00', 1, 'DISPONIBLE'),
(52, 3, '2026-04-23 09:30:00', '2026-04-23 10:00:00', 2, 'DISPONIBLE'),
(53, 3, '2026-04-23 10:00:00', '2026-04-23 10:30:00', 1, 'DISPONIBLE'),
(54, 3, '2026-04-23 10:00:00', '2026-04-23 10:30:00', 2, 'DISPONIBLE'),
(55, 3, '2026-04-23 10:30:00', '2026-04-23 11:00:00', 1, 'DISPONIBLE'),
(56, 3, '2026-04-23 10:30:00', '2026-04-23 11:00:00', 2, 'DISPONIBLE'),
(57, 3, '2026-04-23 11:00:00', '2026-04-23 11:30:00', 1, 'DISPONIBLE'),
(58, 3, '2026-04-23 11:00:00', '2026-04-23 11:30:00', 2, 'DISPONIBLE'),
(59, 3, '2026-04-23 11:30:00', '2026-04-23 12:00:00', 1, 'DISPONIBLE'),
(60, 3, '2026-04-23 11:30:00', '2026-04-23 12:00:00', 2, 'DISPONIBLE'),
(61, 3, '2026-04-23 12:00:00', '2026-04-23 12:30:00', 1, 'DISPONIBLE'),
(62, 3, '2026-04-23 12:00:00', '2026-04-23 12:30:00', 2, 'DISPONIBLE'),
(63, 3, '2026-04-23 12:30:00', '2026-04-23 13:00:00', 1, 'DISPONIBLE'),
(64, 3, '2026-04-23 12:30:00', '2026-04-23 13:00:00', 2, 'DISPONIBLE'),
(65, 3, '2026-04-23 13:00:00', '2026-04-23 13:30:00', 1, 'DISPONIBLE'),
(66, 3, '2026-04-23 13:00:00', '2026-04-23 13:30:00', 2, 'DISPONIBLE'),
(67, 3, '2026-04-23 13:30:00', '2026-04-23 14:00:00', 1, 'DISPONIBLE'),
(68, 3, '2026-04-23 13:30:00', '2026-04-23 14:00:00', 2, 'DISPONIBLE'),
(69, 3, '2026-04-23 14:00:00', '2026-04-23 14:30:00', 1, 'DISPONIBLE'),
(70, 3, '2026-04-23 14:00:00', '2026-04-23 14:30:00', 2, 'DISPONIBLE'),
(71, 3, '2026-04-23 14:30:00', '2026-04-23 15:00:00', 1, 'DISPONIBLE'),
(72, 3, '2026-04-23 14:30:00', '2026-04-23 15:00:00', 2, 'DISPONIBLE'),
-- Parada 4 - 2026-04-24 (slots 73-96)
(73, 4, '2026-04-24 09:00:00', '2026-04-24 09:30:00', 1, 'RESERVADO'),  -- cita 3 (RESULTADOS_APROBADOS)
(74, 4, '2026-04-24 09:00:00', '2026-04-24 09:30:00', 2, 'DISPONIBLE'),
(75, 4, '2026-04-24 09:30:00', '2026-04-24 10:00:00', 1, 'RESERVADO'),  -- cita 4 (RESULTADOS_SUBIDOS)
(76, 4, '2026-04-24 09:30:00', '2026-04-24 10:00:00', 2, 'DISPONIBLE'),
(77, 4, '2026-04-24 10:00:00', '2026-04-24 10:30:00', 1, 'DISPONIBLE'),
(78, 4, '2026-04-24 10:00:00', '2026-04-24 10:30:00', 2, 'DISPONIBLE'),
(79, 4, '2026-04-24 10:30:00', '2026-04-24 11:00:00', 1, 'DISPONIBLE'),
(80, 4, '2026-04-24 10:30:00', '2026-04-24 11:00:00', 2, 'DISPONIBLE'),
(81, 4, '2026-04-24 11:00:00', '2026-04-24 11:30:00', 1, 'DISPONIBLE'),
(82, 4, '2026-04-24 11:00:00', '2026-04-24 11:30:00', 2, 'DISPONIBLE'),
(83, 4, '2026-04-24 11:30:00', '2026-04-24 12:00:00', 1, 'DISPONIBLE'),
(84, 4, '2026-04-24 11:30:00', '2026-04-24 12:00:00', 2, 'DISPONIBLE'),
(85, 4, '2026-04-24 12:00:00', '2026-04-24 12:30:00', 1, 'DISPONIBLE'),
(86, 4, '2026-04-24 12:00:00', '2026-04-24 12:30:00', 2, 'DISPONIBLE'),
(87, 4, '2026-04-24 12:30:00', '2026-04-24 13:00:00', 1, 'DISPONIBLE'),
(88, 4, '2026-04-24 12:30:00', '2026-04-24 13:00:00', 2, 'DISPONIBLE'),
(89, 4, '2026-04-24 13:00:00', '2026-04-24 13:30:00', 1, 'DISPONIBLE'),
(90, 4, '2026-04-24 13:00:00', '2026-04-24 13:30:00', 2, 'DISPONIBLE'),
(91, 4, '2026-04-24 13:30:00', '2026-04-24 14:00:00', 1, 'DISPONIBLE'),
(92, 4, '2026-04-24 13:30:00', '2026-04-24 14:00:00', 2, 'DISPONIBLE'),
(93, 4, '2026-04-24 14:00:00', '2026-04-24 14:30:00', 1, 'DISPONIBLE'),
(94, 4, '2026-04-24 14:00:00', '2026-04-24 14:30:00', 2, 'DISPONIBLE'),
(95, 4, '2026-04-24 14:30:00', '2026-04-24 15:00:00', 1, 'DISPONIBLE'),
(96, 4, '2026-04-24 14:30:00', '2026-04-24 15:00:00', 2, 'DISPONIBLE'),
-- Parada 5 - 2026-04-25 (slots 97-120)
(97,  5, '2026-04-25 09:00:00', '2026-04-25 09:30:00', 1, 'RESERVADO'),  -- cita 7 (RESULTADOS_APROBADOS)
(98,  5, '2026-04-25 09:00:00', '2026-04-25 09:30:00', 2, 'DISPONIBLE'), -- cita 5 (CANCELADA -> slot libre)
(99,  5, '2026-04-25 09:30:00', '2026-04-25 10:00:00', 1, 'RESERVADO'),  -- cita 6 (CONFIRMADA)
(100, 5, '2026-04-25 09:30:00', '2026-04-25 10:00:00', 2, 'DISPONIBLE'),
(101, 5, '2026-04-25 10:00:00', '2026-04-25 10:30:00', 1, 'DISPONIBLE'),
(102, 5, '2026-04-25 10:00:00', '2026-04-25 10:30:00', 2, 'DISPONIBLE'),
(103, 5, '2026-04-25 10:30:00', '2026-04-25 11:00:00', 1, 'DISPONIBLE'),
(104, 5, '2026-04-25 10:30:00', '2026-04-25 11:00:00', 2, 'DISPONIBLE'),
(105, 5, '2026-04-25 11:00:00', '2026-04-25 11:30:00', 1, 'DISPONIBLE'),
(106, 5, '2026-04-25 11:00:00', '2026-04-25 11:30:00', 2, 'DISPONIBLE'),
(107, 5, '2026-04-25 11:30:00', '2026-04-25 12:00:00', 1, 'DISPONIBLE'),
(108, 5, '2026-04-25 11:30:00', '2026-04-25 12:00:00', 2, 'DISPONIBLE'),
(109, 5, '2026-04-25 12:00:00', '2026-04-25 12:30:00', 1, 'DISPONIBLE'),
(110, 5, '2026-04-25 12:00:00', '2026-04-25 12:30:00', 2, 'DISPONIBLE'),
(111, 5, '2026-04-25 12:30:00', '2026-04-25 13:00:00', 1, 'DISPONIBLE'),
(112, 5, '2026-04-25 12:30:00', '2026-04-25 13:00:00', 2, 'DISPONIBLE'),
(113, 5, '2026-04-25 13:00:00', '2026-04-25 13:30:00', 1, 'DISPONIBLE'),
(114, 5, '2026-04-25 13:00:00', '2026-04-25 13:30:00', 2, 'DISPONIBLE'),
(115, 5, '2026-04-25 13:30:00', '2026-04-25 14:00:00', 1, 'DISPONIBLE'),
(116, 5, '2026-04-25 13:30:00', '2026-04-25 14:00:00', 2, 'DISPONIBLE'),
(117, 5, '2026-04-25 14:00:00', '2026-04-25 14:30:00', 1, 'DISPONIBLE'),
(118, 5, '2026-04-25 14:00:00', '2026-04-25 14:30:00', 2, 'DISPONIBLE'),
(119, 5, '2026-04-25 14:30:00', '2026-04-25 15:00:00', 1, 'DISPONIBLE'),
(120, 5, '2026-04-25 14:30:00', '2026-04-25 15:00:00', 2, 'DISPONIBLE');

INSERT IGNORE INTO cita (
    id_cita,
    tipo_prueba,
    estado,
    paciente_id,
    tecnico_id,
    doctor_id,
    slot_id,
    created_at,
    updated_at
) VALUES
-- cita reservada por paciente y asignada automáticamente a técnico (parada 2, 2026-04-22 10:00)
(1, 'Analitica', 'PENDIENTE', 5, 6, NULL, 29, NOW(), NOW()),

-- cita confirmada por el técnico (parada 2, 2026-04-22 10:30)
(2, 'Radiografia', 'CONFIRMADA', 5, 6, NULL, 31, NOW(), NOW()),

-- cita ya validada por el médico (parada 4, 2026-04-24 09:00)
(3, 'Resonancia', 'RESULTADOS_APROBADOS', 5, 6, 3, 73, NOW(), NOW()),

-- cita con resultados subidos pendiente de revisión médica (parada 4, 2026-04-24 09:30)
(4, 'Ecografia', 'RESULTADOS_SUBIDOS', 2, 6, NULL, 75, NOW(), NOW()),

-- cita cancelada tras haber sido asignada (parada 5, 2026-04-25 09:00 cupo 2)
(5, 'Hemograma', 'CANCELADA', 4, 6, NULL, 98, NOW(), NOW()),

-- cita confirmada con borrador técnico aún no enviado (parada 5, 2026-04-25 09:30)
(6, 'Perfil lipidico', 'CONFIRMADA', 5, 6, NULL, 99, NOW(), NOW()),

-- segunda cita aprobada para enriquecer el historial del paciente (parada 5, 2026-04-25 09:00 cupo 1)
(7, 'Glucosa', 'RESULTADOS_APROBADOS', 5, 6, 3, 97, NOW(), NOW());

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
