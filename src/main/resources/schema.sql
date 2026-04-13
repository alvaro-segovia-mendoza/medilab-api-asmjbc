-- =========================================
-- LIMPIEZA INICIAL
-- =========================================

DROP TABLE IF EXISTS registros_clinicos;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS cita;
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

    first_name VARCHAR(60) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    phone_number VARCHAR(30) NULL,
    profile_image VARCHAR(255) NULL,
    bio VARCHAR(500) NULL,
    locale VARCHAR(10) NULL,

    dni VARCHAR(20) NOT NULL UNIQUE,
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

    CONSTRAINT fk_ruta_trailer
        FOREIGN KEY (trailer_id)
        REFERENCES trailers(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    INDEX idx_ruta_trailer_id (trailer_id),
    INDEX idx_ruta_activa (activa)
);

CREATE TABLE paradas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ruta_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    municipio VARCHAR(100) NOT NULL,
    direccion VARCHAR(150) NULL,
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

    INDEX idx_parada_ruta_id (ruta_id),
    INDEX idx_parada_fecha (fecha),
    INDEX idx_parada_activa (activa)
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
    fecha_hora DATETIME NOT NULL,
    tipo_prueba VARCHAR(50) NOT NULL,
    estado ENUM('PENDIENTE','CONFIRMADA','RESULTADOS_SUBIDOS','RESULTADOS_APROBADOS','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',

    paciente_id BIGINT NOT NULL,
    tecnico_id BIGINT NULL,
    doctor_id BIGINT NULL,
    parada_id BIGINT NOT NULL,

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

    CONSTRAINT fk_cita_parada
        FOREIGN KEY (parada_id)
        REFERENCES paradas(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    INDEX idx_cita_fecha_hora (fecha_hora),
    INDEX idx_cita_estado (estado),
    INDEX idx_cita_paciente_id (paciente_id),
    INDEX idx_cita_tecnico_id (tecnico_id),
    INDEX idx_cita_doctor_id (doctor_id),
    INDEX idx_cita_parada_id (parada_id)
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
