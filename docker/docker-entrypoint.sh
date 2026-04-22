#!/bin/sh
set -eu

DEFAULT_KEYSTORE_TARGET="${JWT_KEYSTORE_PATH:-/app/certs/jwt-keystore.p12}"
DEFAULT_KEYSTORE_PASSWORD="${JWT_KEYSTORE_PASSWORD:-changeit}"
DEFAULT_KEYSTORE_ALIAS="${JWT_KEYSTORE_ALIAS:-jwt-keypair}"
DEFAULT_KEYSTORE_DNAME="${JWT_KEYSTORE_DNAME:-CN=medilab, OU=IT, O=Medilab, L=Sevilla, ST=Sevilla, C=ES}"
DEFAULT_KEYSTORE_VALIDITY_DAYS="${JWT_KEYSTORE_VALIDITY_DAYS:-3650}"

mkdir -p "$(dirname "$DEFAULT_KEYSTORE_TARGET")" /app/data/uploads

if [ ! -s "$DEFAULT_KEYSTORE_TARGET" ]; then
  rm -f "$DEFAULT_KEYSTORE_TARGET"
  keytool -genkeypair \
    -alias "$DEFAULT_KEYSTORE_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore "$DEFAULT_KEYSTORE_TARGET" \
    -storepass "$DEFAULT_KEYSTORE_PASSWORD" \
    -keypass "$DEFAULT_KEYSTORE_PASSWORD" \
    -dname "$DEFAULT_KEYSTORE_DNAME" \
    -validity "$DEFAULT_KEYSTORE_VALIDITY_DAYS"
fi

exec "$@"
