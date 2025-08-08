# Сервис корпоративных обсуждений Муза

Документация проекта: https://github.com/MalykhSergey/muse-service/deployments/github-pages

Запуск в Docker: 
```shell 
    docker compose up -d muse
```

UI для API (с документацией в будущем): http://127.0.0.1:8080/swagger-ui/index.html

Запрос для получения токенов:
```shell
    curl -X POST "http://localhost:9090/realms/users-auth/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=password" \
    -d "client_id=qa-front" \
    -d "username=testet" \
    -d "password=123"
```
