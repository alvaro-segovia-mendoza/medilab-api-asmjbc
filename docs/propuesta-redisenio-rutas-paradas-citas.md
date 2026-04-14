# Propuesta de rediseño de datos para rutas, paradas y citas

## Objetivo

Separar claramente:

- la definicion de una ruta operativa,
- las ubicaciones fisicas donde se presta servicio,
- la planificacion concreta de una jornada,
- y la reserva real de una cita.

El modelo actual funciona para un caso simple, pero mezcla lugar, agenda operativa y reserva en las tablas `rutas`, `paradas` y `cita`. Eso obliga a validar en codigo inconsistencias que deberian estar resueltas en el modelo de datos.

## Problemas del modelo actual

### 1. `paradas` mezcla ubicacion y programacion

La tabla `paradas` contiene:

- identidad del punto de servicio: `nombre`, `municipio`, `direccion`
- planificacion operativa: `fecha`, `hora_inicio`, `hora_fin`, `capacidad_maxima`

Eso impide reutilizar una misma ubicacion para varias jornadas sin duplicar datos.

### 2. `cita` duplica el tiempo de la reserva

La tabla `cita` guarda `parada_id` y tambien `fecha_hora`.

Consecuencia:

- la cita depende de una parada,
- pero el instante reservado no esta modelado como entidad propia,
- y la coherencia entre `cita.fecha_hora` y la ventana de `parada` se delega al servicio.

### 3. `nombre` en `paradas` no tiene semantica de negocio estable

Valores como `Pueblo 1`, `Pueblo 2` o `Pueblo 7` no representan nada util para:

- pacientes,
- operadores,
- soporte,
- ni trazabilidad historica.

Si el dato relevante es el lugar real, el nombre debe identificar la ubicacion real. Si no aporta mas que municipio y direccion, ese campo deberia desaparecer o pasar a ser un alias opcional.

### 4. La reserva se hace contra una parada, no contra un slot

La disponibilidad se calcula generando slots en tiempo de ejecucion. Eso complica:

- bloqueos concurrentes,
- trazabilidad de disponibilidad publicada,
- sobreventa en escenarios concurrentes,
- y futuras replanificaciones.

### 5. `ruta` esta simplificada en exceso

Campos como `origen` y `destino` no describen bien una ruta movil sanitaria con varias paradas intermedias. En la operativa real interesa mas:

- nombre comercial,
- zona sanitaria,
- campaña o circuito,
- frecuencia,
- y la secuencia real de ubicaciones.

## Propuesta de modelo

Se propone separar el dominio en seis piezas.

## 1. `ruta_base`

Define el tipo de ruta o circuito.

Campos propuestos:

- `id`
- `codigo`
- `nombre_comercial`
- `zona_sanitaria`
- `descripcion`
- `activa`

Ejemplos:

- `R-SEV-CO-001`
- `Ruta Sierra Norte Cordobesa desde Aljarafe`

## 2. `ubicacion_parada`

Representa un punto fisico reutilizable.

Campos propuestos:

- `id`
- `nombre_visible`
- `municipio`
- `provincia`
- `direccion`
- `latitud`
- `longitud`
- `referencia`
- `activa`

Notas:

- `nombre_visible` sustituye al actual `paradas.nombre`.
- Debe ser un nombre real, por ejemplo `Centro de Salud de Castilleja de la Cuesta`.
- `municipio` por si solo no basta; hace falta `provincia` para rutas interprovinciales.

## 3. `ruta_base_ubicacion`

Secuencia teorica de ubicaciones de una ruta base.

Campos propuestos:

- `id`
- `ruta_base_id`
- `ubicacion_parada_id`
- `orden`
- `tiempo_estimado_desde_anterior_min`
- `activa`

Esta tabla permite definir el recorrido de referencia sin fijar una fecha concreta.

## 4. `ruta_jornada`

Representa una ejecucion concreta de una ruta en una fecha.

Campos propuestos:

- `id`
- `ruta_base_id`
- `fecha_servicio`
- `trailer_id`
- `estado`
- `hora_salida_base`
- `observaciones`
- `created_at`
- `updated_at`

Estados posibles:

- `PLANIFICADA`
- `PUBLICADA`
- `EN_CURSO`
- `FINALIZADA`
- `CANCELADA`

## 5. `ruta_jornada_parada`

Representa una parada concreta de una jornada concreta.

Campos propuestos:

- `id`
- `ruta_jornada_id`
- `ubicacion_parada_id`
- `orden`
- `hora_llegada_estimada`
- `hora_inicio_atencion`
- `hora_fin_atencion`
- `capacidad_maxima`
- `estado`
- `observaciones`

Estados posibles:

- `PLANIFICADA`
- `ACTIVA`
- `CERRADA`
- `CANCELADA`

Esta tabla sustituye funcionalmente a la `paradas` actual.

## 6. `slot_cita`

Representa un hueco reservable explicito.

Campos propuestos:

- `id`
- `ruta_jornada_parada_id`
- `fecha_hora_inicio`
- `fecha_hora_fin`
- `estado`
- `tecnico_id` nullable
- `bloqueado_hasta` nullable
- `created_at`
- `updated_at`

Estados posibles:

- `DISPONIBLE`
- `BLOQUEADO`
- `RESERVADO`
- `CANCELADO`
- `NO_DISPONIBLE`

Esta es la pieza clave del rediseño.

## 7. `cita`

La cita pasa a depender de un slot concreto.

Campos propuestos:

- `id`
- `slot_id`
- `paciente_id`
- `tecnico_id`
- `doctor_id`
- `tipo_prueba_id` o `tipo_prueba`
- `estado`
- `canal_reserva`
- `motivo_cancelacion`
- `created_at`
- `updated_at`

Estados posibles:

- `RESERVADA`
- `CONFIRMADA`
- `EN_ATENCION`
- `RESULTADOS_SUBIDOS`
- `RESULTADOS_APROBADOS`
- `CANCELADA`
- `NO_PRESENTADO`

## Relacion entre tablas

```text
ruta_base
  -> ruta_base_ubicacion
  -> ruta_jornada

ubicacion_parada
  -> ruta_base_ubicacion
  -> ruta_jornada_parada

ruta_jornada
  -> ruta_jornada_parada

ruta_jornada_parada
  -> slot_cita

slot_cita
  -> cita
```

## Que hacer con el campo `nombre`

### Recomendacion

Eliminar el uso artificial de `nombre` como numeracion tipo `Pueblo 1`.

Sustituirlo por:

- `ubicacion_parada.nombre_visible`
- `municipio`
- `provincia`
- `direccion`

### Regla recomendada

`nombre_visible` debe responder a una de estas formas:

- nombre del centro o recinto: `Centro de Salud de Bormujos`
- nombre de punto operativo: `Unidad movil junto al Ayuntamiento de Espiel`
- nombre corto reconocible por pacientes: `Consultorio local de Cardeña`

### Valores no validos funcionalmente

- `Pueblo 1`
- `Parada 3`
- `Ruta A - Punto 2`

No son buenos identificadores funcionales ni operativos.

## Ejemplo realista

### Ruta base

```text
codigo: R-SEV-CO-001
nombre_comercial: Ruta Sierra Norte Cordobesa desde Aljarafe
zona_sanitaria: Sevilla Oeste / Norte de Cordoba
descripcion: Circuito movil para toma de muestras y pruebas basicas en municipios con menor cobertura cercana
```

### Ubicaciones reutilizables

```text
1. nombre_visible: Centro de Salud de Castilleja de la Cuesta
   municipio: Castilleja de la Cuesta
   provincia: Sevilla
   direccion: Av. Juan Carlos I, s/n

2. nombre_visible: Recinto sanitario movil de Bormujos
   municipio: Bormujos
   provincia: Sevilla
   direccion: Zona anexa al hospital

3. nombre_visible: Plaza de Andalucia de Espiel
   municipio: Espiel
   provincia: Cordoba
   direccion: Plaza de Andalucia

4. nombre_visible: Consultorio local de Villaviciosa de Cordoba
   municipio: Villaviciosa de Cordoba
   provincia: Cordoba
   direccion: Calle Nueva, 4

5. nombre_visible: Centro de Salud de Cardena
   municipio: Cardena
   provincia: Cordoba
   direccion: Calle del Consultorio, 2
```

### Jornada concreta

```text
fecha_servicio: 2026-04-21
trailer: MEDILAB-MOVIL-01
estado: PUBLICADA
```

### Paradas de la jornada

```text
1. Castilleja de la Cuesta | 08:30-09:30
2. Bormujos               | 10:00-11:00
3. Espiel                 | 13:00-14:00
4. Villaviciosa           | 15:00-16:00
5. Cardena                | 17:00-18:00
```

### Slots generados

Para una duracion de 30 minutos:

```text
Castilleja de la Cuesta:
- 2026-04-21 08:30
- 2026-04-21 09:00

Bormujos:
- 2026-04-21 10:00
- 2026-04-21 10:30

Espiel:
- 2026-04-21 13:00
- 2026-04-21 13:30
```

La cita se reserva contra uno de esos slots.

## Flujo recomendado de reserva

### Opcion recomendada

Publicar slots explicitos y reservar por `slot_id`.

Flujo:

1. Se planifica una `ruta_jornada`.
2. Se crean sus `ruta_jornada_parada`.
3. Se generan `slot_cita` por cada parada.
4. El paciente consulta disponibilidad real.
5. El paciente reserva un `slot_id`.
6. Una transaccion:
   - verifica que el slot sigue `DISPONIBLE`
   - lo marca `RESERVADO`
   - crea la `cita`
   - asigna tecnico disponible
7. Si se cancela:
   - la `cita` pasa a `CANCELADA`
   - el `slot` vuelve a `DISPONIBLE` o queda `NO_DISPONIBLE` segun reglas

### Por que es mejor que el modelo actual

- evita duplicar la hora entre parada y cita
- reduce riesgo de sobreventa
- simplifica la concurrencia
- permite auditar cupos publicados
- facilita reasignaciones operativas
- hace mas simple la API de disponibilidad

## Asignacion de tecnico

### Recomendacion

Asignar tecnico al reservar, no al crear la parada.

Justificacion:

- la dotacion puede cambiar entre planificacion y jornada real
- evita fijar recursos demasiado pronto
- mantiene flexibilidad operativa

### Alternativa

Si la operativa exige preasignacion estricta, el tecnico puede quedar asociado a `slot_cita`.

Eso es util si:

- cada hueco solo puede ser atendido por un tecnico concreto
- o se quiere balancear carga desde planificacion

## Restricciones de base de datos recomendadas

### Unicidad

- `ruta_base.codigo` unique
- `ruta_base_ubicacion (ruta_base_id, orden)` unique
- `ruta_jornada (ruta_base_id, fecha_servicio)` unique opcional segun negocio
- `ruta_jornada_parada (ruta_jornada_id, orden)` unique
- `slot_cita (ruta_jornada_parada_id, fecha_hora_inicio)` unique
- `cita.slot_id` unique

### Checks logicos

- `hora_inicio_atencion < hora_fin_atencion`
- `fecha_hora_inicio < fecha_hora_fin`
- `capacidad_maxima > 0`

## Cambios API sugeridos

### Endpoints nuevos o rediseñados

- `GET /api/rutas-jornada?fecha=2026-04-21`
- `GET /api/rutas-jornada/{id}/paradas`
- `GET /api/paradas-jornada/{id}/slots`
- `POST /api/citas`
  - entrada recomendada: `slotId`, `pacienteId`, `tipoPrueba`

### Endpoint de disponibilidad

En lugar de calcular slots implicitamente desde `parada`, devolver slots ya persistidos:

- estado del slot
- tecnico asignado si procede
- plazas reales disponibles

## Estrategia de migracion

Se recomienda una migracion en tres fases.

### Fase 1. Introducir tablas nuevas sin romper compatibilidad

Crear:

- `ruta_base`
- `ubicacion_parada`
- `ruta_base_ubicacion`
- `ruta_jornada`
- `ruta_jornada_parada`
- `slot_cita`

Mantener temporalmente:

- `rutas`
- `paradas`
- `cita`

### Fase 2. Migrar datos actuales

Mapeo sugerido:

- `rutas` -> `ruta_base`
- cada `paradas` actual -> `ubicacion_parada` + `ruta_jornada_parada`
- `fecha`, `hora_inicio`, `hora_fin` -> jornada y parada de jornada
- cada `cita.fecha_hora` -> `slot_cita`
- `cita` pasa a apuntar a `slot_cita`

### Fase 3. Cambiar servicios y DTOs

Refactor progresivo:

1. disponibilidad basada en `slot_cita`
2. reserva basada en `slot_id`
3. detalle de cita basado en `slot -> parada_jornada -> ubicacion`
4. deprecacion de `paradas.nombre` y del uso directo de `cita.fecha_hora`

## Impacto sobre el codigo actual

### Piezas que deberian cambiar

- entidad `Parada`
- entidad `Cita`
- `ParadaServiceImpl`
- `CitaServiceImpl`
- DTOs de disponibilidad
- seed `data.sql`

### Cambio conceptual importante

Hoy:

- `parada` es casi una agenda diaria
- `cita` cuelga de `parada`

Propuesto:

- `ubicacion` es una ubicacion reutilizable
- `parada de jornada` es planificacion operativa
- `slot` es el recurso reservable
- `cita` es la reserva clinica

## Decision recomendada

La propuesta a implementar es:

1. Eliminar el uso artificial de `paradas.nombre`.
2. Introducir una entidad de ubicacion reutilizable.
3. Separar ruta base de ruta programada por fecha.
4. Introducir `slot_cita` como recurso reservable explicito.
5. Hacer que `cita` reserve `slot_id`, no `parada_id`.
6. Mantener la asignacion de tecnico en el momento de reserva, salvo necesidad operativa mas estricta.

## Implementacion minima viable

Si se quiere una version mas corta antes del rediseño completo:

1. Renombrar semanticamente `paradas.nombre` a un valor real.
2. Anadir `provincia` a `paradas`.
3. Crear `slot_cita`.
4. Mover la reserva a `slot_id`.
5. Mantener `rutas` y `paradas` actuales durante una primera iteracion.

Eso ya resolveria la mayor parte del problema de reserva sin exigir una reestructuracion total en un solo paso.
