# Сервис корпоративных обсуждений Муза

Документация проекта: https://malykhsergey.github.io/muse-service/

## Для локального развёртывания

Запуск в Docker:
```shell 
    docker compose up -d muse
```

UI для API (с документацией в будущем): http://127.0.0.1/swagger-ui/index.html

Запрос для получения токенов:
```shell
    curl -X POST "http://localhost:8080/realms/users-auth/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=password" \
    -d "client_id=qa-front" \
    -d "username=testet" \
    -d "password=123"
```

Запрос обновления токенов:
```shell
    curl -X POST "http://localhost:8080/realms/users-auth/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=refresh_token" \
    -d "client_id=qa-front" \
    -d "refresh_token=eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI2YTViZjQzYy01Yzk3LTQ5YmQtYTA0YS05MTQ3YzgxYTdiNDgifQ.eyJleHAiOjE3NTQ5OTA5MTgsImlhdCI6MTc1NDk4OTExOCwianRpIjoiMjM2ZDI0ODktZTlhMS00ZGE5LWYyMjMtYzEyMDllMWJjMzM5IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy91c2Vycy1hdXRoIiwiYXVkIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy91c2Vycy1hdXRoIiwic3ViIjoiNTJiODZmMGYtM2E5Ni00YzY4LTlmZDItMmQxYzE2ZTliNDE5IiwidHlwIjoiUmVmcmVzaCIsImF6cCI6InFhLWZyb250Iiwic2lkIjoiZGNhOTFjMzItZTg0OC00NzQ1LTk0ZjctZjUzMTE4Mjg5MGRmIiwic2NvcGUiOiJwcm9maWxlIHJvbGVzIGJhc2ljIHdlYi1vcmlnaW5zIGFjciBlbWFpbCJ9.wBUL5BPkY_8iYQnhJImIGWe82AkRPwCdjfg5NP-OrwnA170ZshaX0i_ppZooRBAx5K30VnM-4fbv-qyovOVYcg"
```