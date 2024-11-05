
# funduk

Проект представляет собой тестирующую систему, реализованную с использованием Kotlin Multiplatform. Система включает клиентскую часть, сервер и общий код, что позволяет разделить логику и использовать её в разных платформах.
## Backend

#### Technology Stack
- Programming Language: Kotlin
- Framework: Ktor
- Database: PostgreSQL
- ORM: Exposed

#### Roles

#### Database Diagram

![Database Diagram](/images/diagram.png)
## Frontend

#### Technology Stack
- Programming Language: Kotlin (Kotlin/JS via Kotlin Multiplatform)
- Framework: React, Ktor Client

#### Design Prototype

[Figma](https://www.figma.com/design/M9TR2k9xhBSGOADXkkEMxV/PC?node-id=0-1&t=iBpw1mR7k9AZp8R3-1)

## API Reference

[API](/documentation/API.md)
or
http://localhost:8080/swagger

## Start
Run Server
```console
./gradlew :server:run
```
Run Client
```console
./gradlew :web:run
```
