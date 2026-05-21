-- =========================================
-- LIMPIEZA INICIAL
-- =========================================

DROP TABLE IF EXISTS registros_clinicos;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS ruta_tecnicos;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS cita;
DROP TABLE IF EXISTS slot_cita;
DROP TABLE IF EXISTS paradas;
DROP TABLE IF EXISTS rutas;
DROP TABLE IF EXISTS trailers;
DROP TABLE IF EXISTS user_profiles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

-- =========================================
-- TABLA: users
-- Autenticación y seguridad
-- =========================================

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(500) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    last_password_change DATETIME NULL,
    password_expires_at DATETIME NULL,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    must_change_password BOOLEAN NOT NULL DEFAULT FALSE
);

-- =========================================
-- TABLA: user_profiles
-- Datos personales compartidos por todos los usuarios
-- Relación 1:1 con users
-- =========================================

CREATE TABLE user_profiles (
    user_id BIGINT NOT NULL,

    first_name VARCHAR(60) NULL,
    last_name VARCHAR(80) NULL,
    phone_number VARCHAR(30) NULL,
    profile_image VARCHAR(255) NULL,
    bio VARCHAR(500) NULL,
    locale VARCHAR(10) NULL,

    dni VARCHAR(20) NULL UNIQUE,
    date_of_birth DATE NULL,
    address VARCHAR(150) NULL,
    city VARCHAR(50) NULL,
    province VARCHAR(50) NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_profiles PRIMARY KEY (user_id),

    CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- =========================================
-- TABLA: roles
-- Roles del sistema
-- =========================================

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL
);

CREATE TABLE trailers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    descripcion VARCHAR(255) NULL
);

CREATE TABLE rutas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    origen VARCHAR(100) NOT NULL,
    destino VARCHAR(100) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    trailer_id BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    descripcion VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_ruta_trailer
        FOREIGN KEY (trailer_id)
        REFERENCES trailers(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT chk_ruta_fechas CHECK (fecha_inicio <= fecha_fin),

    INDEX idx_ruta_trailer_id (trailer_id),
    INDEX idx_ruta_activa (activa)
);

CREATE TABLE ruta_tecnicos (
    ruta_id BIGINT NOT NULL,
    tecnico_id BIGINT NOT NULL,

    CONSTRAINT pk_ruta_tecnicos PRIMARY KEY (ruta_id, tecnico_id),
    CONSTRAINT fk_ruta_tecnicos_ruta
        FOREIGN KEY (ruta_id)
        REFERENCES rutas(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_ruta_tecnicos_tecnico
        FOREIGN KEY (tecnico_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    INDEX idx_ruta_tecnicos_tecnico_id (tecnico_id)
);

CREATE TABLE paradas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ruta_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    municipio VARCHAR(100) NOT NULL,
    provincia VARCHAR(100) NOT NULL,
    direccion VARCHAR(150) NULL,
    latitud DECIMAL(9,6) NOT NULL,
    longitud DECIMAL(9,6) NOT NULL,
    orden_parada INT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    capacidad_maxima INT NOT NULL DEFAULT 1,
    activa BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_parada_ruta
        FOREIGN KEY (ruta_id)
        REFERENCES rutas(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_parada_ruta_fecha_orden UNIQUE (ruta_id, fecha, orden_parada),
    CONSTRAINT chk_parada_capacidad_tecnicos CHECK (capacidad_maxima BETWEEN 1 AND 2),
    CONSTRAINT chk_parada_latitud CHECK (latitud BETWEEN -90 AND 90),
    CONSTRAINT chk_parada_longitud CHECK (longitud BETWEEN -180 AND 180),
    CONSTRAINT chk_parada_horas_validas CHECK (hora_inicio < hora_fin),

    INDEX idx_parada_activa_fecha_hora (activa, fecha, hora_inicio),
    INDEX idx_parada_activa_fecha_ruta_orden (activa, fecha, ruta_id, orden_parada)
);

CREATE TABLE slot_cita (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parada_id BIGINT NOT NULL,
    fecha_hora_inicio DATETIME NOT NULL,
    fecha_hora_fin DATETIME NOT NULL,
    cupo_numero INT NOT NULL,
    estado ENUM('DISPONIBLE','RESERVADO','NO_DISPONIBLE') NOT NULL DEFAULT 'DISPONIBLE',

    CONSTRAINT fk_slot_parada
        FOREIGN KEY (parada_id)
        REFERENCES paradas(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT uq_slot_parada_inicio_cupo UNIQUE (parada_id, fecha_hora_inicio, cupo_numero),
    CONSTRAINT chk_slot_fechas CHECK (fecha_hora_inicio < fecha_hora_fin),
    CONSTRAINT chk_slot_cupo_numero CHECK (cupo_numero BETWEEN 1 AND 2),

    INDEX idx_slot_estado_inicio (estado, fecha_hora_inicio)
);

-- =========================================
-- TABLA: user_roles
-- Relación N:M entre users y roles
-- =========================================

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- =========================================
-- TABLA: password_reset_tokens
-- Tokens para recuperación de contraseña
-- =========================================

CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    request_ip VARCHAR(45) NULL,
    user_agent VARCHAR(255) NULL,

    CONSTRAINT fk_prt_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    INDEX idx_prt_user_id (user_id),
    INDEX idx_prt_token_hash (token_hash),
    INDEX idx_prt_expires_at (expires_at)
);

-- =========================================
-- TABLA: cita
-- Relaciona paciente, técnico y doctor con la cita
-- =========================================

CREATE TABLE cita (
    id_cita BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_prueba VARCHAR(50) NOT NULL,
    estado ENUM('PENDIENTE','CONFIRMADA','RESULTADOS_SUBIDOS','RESULTADOS_APROBADOS','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',

    paciente_id BIGINT NOT NULL,
    tecnico_id BIGINT NULL,
    doctor_id BIGINT NULL,
    slot_id BIGINT NOT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_cita_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_cita_tecnico
        FOREIGN KEY (tecnico_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_cita_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_cita_slot
        FOREIGN KEY (slot_id)
        REFERENCES slot_cita(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    INDEX idx_cita_estado (estado),
    INDEX idx_cita_paciente_id (paciente_id),
    INDEX idx_cita_tecnico_id (tecnico_id),
    INDEX idx_cita_doctor_id (doctor_id),
    INDEX idx_cita_slot_id (slot_id)
);

CREATE TABLE registros_clinicos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cita_id BIGINT NOT NULL,
    paciente_id BIGINT NOT NULL,
    tecnico_id BIGINT NOT NULL,
    medico_id BIGINT NULL,
    tipo_prueba VARCHAR(50) NOT NULL,
    resultado TEXT NOT NULL,
    observaciones_tecnico TEXT NULL,
    observaciones_medico TEXT NULL,
    receta_o_solucion TEXT NULL,
    estado ENUM('BORRADOR','PENDIENTE_REVISION','CONFIRMADO','RECHAZADO') NOT NULL DEFAULT 'BORRADOR',
    confirmed_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_registro_cita
        FOREIGN KEY (cita_id)
        REFERENCES cita(id_cita)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_registro_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_registro_tecnico
        FOREIGN KEY (tecnico_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_registro_medico
        FOREIGN KEY (medico_id)
        REFERENCES users(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    INDEX idx_registro_cita_id (cita_id),
    INDEX idx_registro_paciente_id (paciente_id),
    INDEX idx_registro_tecnico_id (tecnico_id),
    INDEX idx_registro_medico_id (medico_id),
    INDEX idx_registro_estado (estado),
    INDEX idx_registro_confirmed_at (confirmed_at)
);
