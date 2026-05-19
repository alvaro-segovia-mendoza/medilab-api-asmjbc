# Diagrama de base de datos — MediLab

```mermaid
erDiagram
    users {
        BIGINT id PK
        VARCHAR(100) email UK
        VARCHAR(500) password_hash
        BOOLEAN active
        BOOLEAN account_non_locked
        INTEGER failed_login_attempts
        BOOLEAN email_verified
        BOOLEAN must_change_password
        DATETIME last_password_change
        DATETIME password_expires_at
    }

    user_profiles {
        BIGINT user_id PK FK
        VARCHAR(60) first_name
        VARCHAR(80) last_name
        VARCHAR(30) phone_number
        VARCHAR(255) profile_image
        VARCHAR(500) bio
        VARCHAR(10) locale
        VARCHAR(20) dni UK
        DATE date_of_birth
        VARCHAR(150) address
        VARCHAR(50) city
        VARCHAR(50) province
        DATETIME created_at
        DATETIME updated_at
    }

    roles {
        BIGINT id PK
        VARCHAR(50) name UK
        VARCHAR(100) display_name
        VARCHAR(255) description
    }

    user_roles {
        BIGINT user_id FK
        BIGINT role_id FK
    }

    password_reset_tokens {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR(64) token_hash
        DATETIME expires_at
        DATETIME used_at
        DATETIME created_at
        VARCHAR(45) request_ip
        VARCHAR(255) user_agent
    }

    trailers {
        BIGINT id PK
        VARCHAR(30) codigo UK
        VARCHAR(100) nombre
        BOOLEAN activo
        VARCHAR(255) descripcion
    }

    rutas {
        BIGINT id PK
        BIGINT trailer_id FK
        VARCHAR(100) nombre
        VARCHAR(100) origen
        VARCHAR(100) destino
        BOOLEAN activa
        DATE fecha_inicio
        DATE fecha_fin
        VARCHAR(500) descripcion
        DATETIME created_at
        DATETIME updated_at
    }

    ruta_tecnicos {
        BIGINT ruta_id FK
        BIGINT tecnico_id FK
    }

    paradas {
        BIGINT id PK
        BIGINT ruta_id FK
        VARCHAR(100) nombre
        VARCHAR(100) municipio
        VARCHAR(100) provincia
        VARCHAR(150) direccion
        DECIMAL(9-6) latitud
        DECIMAL(9-6) longitud
        INTEGER orden_parada
        DATE fecha
        TIME hora_inicio
        TIME hora_fin
        INTEGER capacidad_maxima
        BOOLEAN activa
        DATETIME created_at
        DATETIME updated_at
    }

    slot_cita {
        BIGINT id PK
        BIGINT parada_id FK
        DATETIME fecha_hora_inicio
        DATETIME fecha_hora_fin
        INTEGER cupo_numero
        VARCHAR(20) estado
    }

    cita {
        BIGINT id_cita PK
        BIGINT paciente_id FK
        BIGINT tecnico_id FK
        BIGINT doctor_id FK
        BIGINT slot_id FK UK
        VARCHAR(50) tipo_prueba
        VARCHAR(20) estado
        DATETIME created_at
        DATETIME updated_at
    }

    registros_clinicos {
        BIGINT id PK
        BIGINT cita_id FK
        BIGINT paciente_id FK
        BIGINT tecnico_id FK
        BIGINT medico_id FK
        VARCHAR(50) tipo_prueba
        TEXT resultado
        TEXT observaciones_tecnico
        TEXT observaciones_medico
        TEXT receta_o_solucion
        VARCHAR(30) estado
        DATETIME confirmed_at
        DATETIME created_at
        DATETIME updated_at
    }

    users ||--|| user_profiles : "perfil"
    users ||--o{ password_reset_tokens : "tokens reset"
    users }o--o{ roles : "user_roles"
    users }o--o{ rutas : "ruta_tecnicos"
    users ||--o{ cita : "paciente_id"
    users ||--o{ cita : "tecnico_id"
    users ||--o{ cita : "doctor_id"
    users ||--o{ registros_clinicos : "paciente_id"
    users ||--o{ registros_clinicos : "tecnico_id"
    users ||--o{ registros_clinicos : "medico_id"
    trailers ||--o{ rutas : "trailer_id"
    rutas ||--o{ paradas : "ruta_id"
    paradas ||--o{ slot_cita : "parada_id"
    slot_cita ||--o| cita : "slot_id"
    cita ||--o{ registros_clinicos : "cita_id"
```
