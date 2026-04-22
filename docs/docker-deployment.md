# Despliegue Docker

La API ya no necesita rutas absolutas del host para `uploads` ni para el keystore JWT.

## Persistencia

- Base de datos: volumen `mariadb_data`
- Imágenes subidas: volumen `uploads_data`
- Certificados JWT: volumen `jwt_certs`

## Primer arranque

1. Copia `.env.docker.example` a `.env`.
2. Ajusta secretos y variables públicas.
3. Arranca con `docker compose up --build -d`.

## Keystore JWT

En el arranque, si el volumen `jwt_certs` no contiene un keystore válido, el entrypoint genera uno PKCS#12 en `/app/certs/jwt-keystore.p12`.

Variables relevantes:

- `JWT_KEYSTORE_PASSWORD`
- `JWT_KEYSTORE_ALIAS`
- `JWT_KEYSTORE_DNAME`

Después del primer arranque, el keystore queda persistido en el volumen Docker y deja de depender del host.

## Uploads

La aplicación usa `APP_UPLOAD_PATH=/app/data`.
`FileStorageService` sigue guardando en la subcarpeta `uploads`, por lo que los ficheros quedan en `/app/data/uploads` dentro del contenedor y en el volumen `uploads_data`.
