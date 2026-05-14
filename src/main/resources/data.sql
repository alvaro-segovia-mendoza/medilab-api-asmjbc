-- Datos semilla coherentes con la fecha de referencia del proyecto: 2026-05-07.
-- Las rutas de abril ya han ocurrido y pueden tener resultados clínicos.
-- Las rutas de junio son futuras: pueden tener citas pendientes, confirmadas o canceladas,
-- pero no resultados subidos ni aprobados.

INSERT IGNORE INTO users (
    id, email, password_hash, active, account_non_locked,
    last_password_change, password_expires_at, failed_login_attempts,
    email_verified, must_change_password
) VALUES
(1, 'admin@app.local', '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, '2026-05-01 09:00:00', '2026-08-01 09:00:00', 0, TRUE, FALSE),
(2, 'paciente@app.local', '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, '2026-05-01 09:00:00', '2026-08-01 09:00:00', 0, TRUE, FALSE),
(3, 'doctor@app.local', '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, '2026-05-01 09:00:00', '2026-08-01 09:00:00', 0, TRUE, FALSE),
(4, 'blockeduser@app.local', '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, '2026-05-01 09:00:00', '2026-08-01 09:00:00', 0, TRUE, FALSE),
(5, 'alvaro.segovia@app.local', '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, '2026-05-01 09:00:00', '2026-08-01 09:00:00', 0, TRUE, FALSE),
(6, 'tecnico@app.local', '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, '2026-05-01 09:00:00', '2026-08-01 09:00:00', 0, TRUE, FALSE),
(7, 'tecnico2@app.local', '$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq', TRUE, TRUE, '2026-05-01 09:00:00', '2026-08-01 09:00:00', 0, TRUE, FALSE);

INSERT IGNORE INTO roles (id, name, display_name, description) VALUES
(1, 'ROLE_ADMIN', 'Administrator', 'Acceso total a todas las funcionalidades del sistema'),
(2, 'ROLE_MEDICO', 'Medico', 'Usuario médico'),
(3, 'ROLE_TECNICO', 'Tecnico', 'Usuario técnico'),
(4, 'ROLE_PACIENTE', 'Paciente', 'Usuario paciente');

INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 4),
(3, 2),
(4, 4),
(5, 4),
(6, 3),
(7, 3);

INSERT IGNORE INTO trailers (id, codigo, nombre, activo, descripcion) VALUES
(1, 'TRL-001', 'Trailer Diagnostico Norte', TRUE, 'Unidad movil para pruebas diagnosticas generales'),
(2, 'TRL-002', 'Trailer Analiticas Sierra', TRUE, 'Unidad movil para analiticas y revisiones preventivas');

INSERT IGNORE INTO rutas (id, nombre, origen, destino, activa, trailer_id, fecha_inicio, fecha_fin, descripcion) VALUES
(1, 'Ruta Vega de Granada - Abril 2026', 'Granada', 'Loja', TRUE, 1, '2026-04-20', '2026-04-25', 'Campaña de diagnóstico móvil en la Vega de Granada'),
(2, 'Ruta Sierra Norte Cordobesa - Junio 2026', 'Castilleja de la Cuesta', 'Cardena', TRUE, 2, '2026-06-09', '2026-06-14', 'Campaña preventiva en la Sierra Norte');

INSERT IGNORE INTO ruta_tecnicos (ruta_id, tecnico_id) VALUES
(1, 6),
(1, 7),
(2, 6),
(2, 7);

INSERT IGNORE INTO paradas (
    id, ruta_id, nombre, municipio, provincia, direccion, latitud, longitud,
    orden_parada, fecha, hora_inicio, hora_fin, capacidad_maxima, activa
) VALUES
-- Ruta pasada: abril de 2026.
(1, 1, 'Centro de Salud de Atarfe', 'Atarfe', 'Granada', 'Avenida de Andalucia, 12', 37.223563, -3.687595, 1, '2026-04-21', '09:00:00', '15:00:00', 2, TRUE),
(2, 1, 'Consultorio de Santa Fe', 'Santa Fe', 'Granada', 'Calle Real, 44', 37.188253, -3.718705, 2, '2026-04-22', '09:00:00', '15:00:00', 2, TRUE),
(3, 1, 'Unidad movil de Loja', 'Loja', 'Granada', 'Avenida de los Angeles, 3', 37.168151, -4.151287, 3, '2026-04-23', '09:00:00', '15:00:00', 2, TRUE),
-- Ruta futura: junio de 2026.
(4, 2, 'Centro de Salud de Castilleja de la Cuesta', 'Castilleja de la Cuesta', 'Sevilla', 'Calle Real, 101', 37.386356, -6.051731, 1, '2026-06-10', '09:00:00', '15:00:00', 2, TRUE),
(5, 2, 'Recinto sanitario movil de Bormujos', 'Bormujos', 'Sevilla', 'Avenida del Aljarafe, 5', 37.373923, -6.070802, 2, '2026-06-11', '09:00:00', '15:00:00', 2, TRUE),
(6, 2, 'Centro de Salud de Cardena', 'Cardena', 'Cordoba', 'Calle Jose Marron, 14', 38.270689, -4.323966, 3, '2026-06-12', '09:00:00', '15:00:00', 2, TRUE);

INSERT IGNORE INTO slot_cita (id, parada_id, fecha_hora_inicio, fecha_hora_fin, cupo_numero, estado) VALUES
-- Abril: slots reservados para citas ya realizadas y un hueco cancelado/liberado.
(1, 1, '2026-04-21 09:00:00', '2026-04-21 09:30:00', 1, 'RESERVADO'),
(2, 1, '2026-04-21 09:00:00', '2026-04-21 09:30:00', 2, 'RESERVADO'),
(3, 1, '2026-04-21 09:30:00', '2026-04-21 10:00:00', 1, 'DISPONIBLE'),
(4, 1, '2026-04-21 09:30:00', '2026-04-21 10:00:00', 2, 'DISPONIBLE'),
(5, 2, '2026-04-22 10:00:00', '2026-04-22 10:30:00', 1, 'RESERVADO'),
(6, 2, '2026-04-22 10:00:00', '2026-04-22 10:30:00', 2, 'RESERVADO'),
(7, 2, '2026-04-22 10:30:00', '2026-04-22 11:00:00', 1, 'DISPONIBLE'),
(8, 2, '2026-04-22 10:30:00', '2026-04-22 11:00:00', 2, 'DISPONIBLE'),
(9, 3, '2026-04-23 11:00:00', '2026-04-23 11:30:00', 1, 'RESERVADO'),
(10, 3, '2026-04-23 11:00:00', '2026-04-23 11:30:00', 2, 'DISPONIBLE'),
(11, 3, '2026-04-23 11:30:00', '2026-04-23 12:00:00', 1, 'DISPONIBLE'),
(12, 3, '2026-04-23 11:30:00', '2026-04-23 12:00:00', 2, 'DISPONIBLE'),
-- Junio: slots futuros con citas pendientes/confirmadas/canceladas y disponibilidad real.
(13, 4, '2026-06-10 09:00:00', '2026-06-10 09:30:00', 1, 'RESERVADO'),
(14, 4, '2026-06-10 09:00:00', '2026-06-10 09:30:00', 2, 'RESERVADO'),
(15, 4, '2026-06-10 09:30:00', '2026-06-10 10:00:00', 1, 'DISPONIBLE'),
(16, 4, '2026-06-10 09:30:00', '2026-06-10 10:00:00', 2, 'DISPONIBLE'),
(17, 5, '2026-06-11 10:00:00', '2026-06-11 10:30:00', 1, 'RESERVADO'),
(18, 5, '2026-06-11 10:00:00', '2026-06-11 10:30:00', 2, 'DISPONIBLE'),
(19, 5, '2026-06-11 10:30:00', '2026-06-11 11:00:00', 1, 'DISPONIBLE'),
(20, 5, '2026-06-11 10:30:00', '2026-06-11 11:00:00', 2, 'DISPONIBLE'),
(21, 6, '2026-06-12 11:00:00', '2026-06-12 11:30:00', 1, 'RESERVADO'),
(22, 6, '2026-06-12 11:00:00', '2026-06-12 11:30:00', 2, 'DISPONIBLE'),
(23, 6, '2026-06-12 11:30:00', '2026-06-12 12:00:00', 1, 'DISPONIBLE'),
(24, 6, '2026-06-12 11:30:00', '2026-06-12 12:00:00', 2, 'DISPONIBLE');

INSERT IGNORE INTO cita (
    id_cita, tipo_prueba, estado, paciente_id, tecnico_id, doctor_id, slot_id, created_at, updated_at
) VALUES
-- Ruta pasada. Estas citas ya ocurrieron antes del 2026-05-07.
(1, 'Analitica general', 'RESULTADOS_APROBADOS', 2, 6, 3, 1, '2026-04-10 10:15:00', '2026-04-22 17:30:00'),
(2, 'Radiografia toracica', 'RESULTADOS_SUBIDOS', 5, 7, NULL, 2, '2026-04-11 11:20:00', '2026-04-21 13:45:00'),
(3, 'Ecografia abdominal', 'RESULTADOS_SUBIDOS', 4, 6, NULL, 5, '2026-04-12 09:40:00', '2026-04-22 10:45:00'),
(4, 'Hemograma', 'CANCELADA', 5, 7, NULL, 7, '2026-04-13 12:00:00', '2026-04-20 08:30:00'),
(5, 'Glucosa basal', 'RESULTADOS_APROBADOS', 5, 6, 3, 9, '2026-04-14 16:20:00', '2026-04-24 12:10:00'),
-- Ruta futura de junio. Aun no puede tener resultados subidos.
(6, 'Analitica preventiva', 'PENDIENTE', 2, 6, NULL, 13, '2026-05-07 09:10:00', '2026-05-07 09:10:00'),
(7, 'Electrocardiograma', 'CONFIRMADA', 5, 7, NULL, 14, '2026-05-07 09:35:00', '2026-05-07 09:50:00'),
(8, 'Perfil lipidico', 'CONFIRMADA', 4, 6, NULL, 17, '2026-05-07 10:05:00', '2026-05-07 10:12:00'),
(9, 'Revision hematologica', 'CANCELADA', 5, 7, NULL, 18, '2026-05-07 10:25:00', '2026-05-07 11:00:00'),
(10, 'Prueba de esfuerzo', 'PENDIENTE', 2, 6, NULL, 21, '2026-05-07 11:20:00', '2026-05-07 11:20:00');

INSERT IGNORE INTO registros_clinicos (
    id, cita_id, paciente_id, tecnico_id, medico_id, tipo_prueba, resultado,
    observaciones_tecnico, observaciones_medico, receta_o_solucion,
    estado, confirmed_at, created_at, updated_at
) VALUES
(1, 1, 2, 6, 3, 'Analitica general', 'Valores dentro de rango para hemograma y bioquimica basica.',
 'Muestra recogida y procesada sin incidencias.', 'Resultados revisados sin hallazgos relevantes.', 'Continuar controles rutinarios.',
 'CONFIRMADO', '2026-04-22 17:30:00', '2026-04-21 09:40:00', '2026-04-22 17:30:00'),
(2, 2, 5, 7, NULL, 'Radiografia toracica', 'Imagen toracica sin signos agudos; pendiente de revision medica.',
 'Exploracion completada correctamente.', NULL, NULL,
 'PENDIENTE_REVISION', NULL, '2026-04-21 13:45:00', '2026-04-21 13:45:00'),
(3, 3, 4, 6, NULL, 'Ecografia abdominal', 'Leve inflamacion compatible con seguimiento ambulatorio.',
 'Informe tecnico subido tras la exploracion.', NULL, NULL,
 'PENDIENTE_REVISION', NULL, '2026-04-22 10:45:00', '2026-04-22 10:45:00'),
(4, 5, 5, 6, 3, 'Glucosa basal', 'Glucemia basal ligeramente elevada.',
 'Muestra procesada sin incidencias.', 'Se recomienda control dietetico y nueva medicion.', 'Revisar en consulta en 3 meses.',
 'CONFIRMADO', '2026-04-24 12:10:00', '2026-04-23 11:45:00', '2026-04-24 12:10:00');

INSERT IGNORE INTO user_profiles (
    user_id, first_name, last_name, phone_number,
    dni, date_of_birth, address, city, province
) VALUES
(1, 'Admin', 'System', '600000001', '12345678Z', '1980-01-01', 'Calle Admin 1', 'Madrid', 'Madrid'),
(2, 'John', 'Doe', '600000002', '23456789H', '1992-04-10', 'Calle Falsa 123', 'Barcelona', 'Barcelona'),
(3, 'Maria', 'Lopez', '600000003', '34567890J', '1988-09-21', 'Avenida Central 45', 'Valencia', 'Valencia'),
(4, 'Carmen', 'Ruiz Ortega', '600000004', '45678901N', '1995-02-11', 'Calle San Jacinto 9', 'Sevilla', 'Sevilla'),
(5, 'Alvaro', 'Segovia Mendoza', '600000005', '56789012W', '1998-06-15', 'Calle Desarrollo 7', 'Malaga', 'Malaga'),
(6, 'Alvaro', 'SM', '600000006', '67890123Q', '2001-01-06', 'Calle Backend 22', 'Granada', 'Granada'),
(7, 'Laura', 'Tecnica Ruta', '600000007', '78901234L', '1999-03-18', 'Calle Laboratorio 12', 'Cordoba', 'Cordoba');
