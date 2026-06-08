# 📬 Feedback App — API REST de Feedback Anónimo

> Sistema backend para la gestión de feedback anónimo entre usuarios, construido con **Java 21** y **Spring Boot 4.0.2**. Incluye autenticación JWT, autorización basada en roles y privilegios, recuperación de contraseña y una arquitectura modular **Screaming Architecture**. Desplegado de forma independiente en un servidor de **DigitalOcean** protegido por **Cloudflare Tunnels**, con base de datos **PostgreSQL en Supabase** y envío de correos transaccionales vía **Resend API**.

---

## 📑 Tabla de Contenidos

- [Tecnologías](#-tecnologías)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Modelo de Base de Datos](#-modelo-de-base-de-datos)
- [Módulos](#-módulos)
- [Configuración de Entorno](#-configuración-de-entorno)
- [Seguridad](#-seguridad)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Ejecución Local](#-ejecución-local)
- [Docker](#-docker)
- [Despliegue](#-despliegue)

---

## 🛠 Tecnologías

| Tecnología | Versión | Descripción |
|---|---|---|
| **Java** | 21 (LTS) | Lenguaje principal del proyecto |
| **Spring Boot** | 4.0.2 | Framework base del backend |
| **Spring Security** | 7.x | Autenticación y autorización |
| **Spring Data JPA** | — | Persistencia y acceso a datos con Hibernate |
| **Spring Validation** | — | Validación de DTOs con Jakarta Validation |
| **JJWT (io.jsonwebtoken)** | 0.12.6 | Generación y validación de tokens JWT |
| **PostgreSQL** | — | Motor de base de datos relacional |
| **Supabase** | — | Servicio cloud que alberga la base de datos PostgreSQL |
| **Lombok** | — | Reducción de boilerplate (getters, setters, constructors) |
| **Jackson** | 2.20.2 | Serialización/deserialización JSON con soporte `java.time` |
| **Spring Dotenv** | 3.0.0 | Carga de variables de entorno desde archivos `.env` |
| **Spring Actuator** | — | Monitoreo y health checks del servicio |
| **Spring DevTools** | — | Recarga en caliente durante el desarrollo |
| **Maven** | — | Gestión de dependencias y construcción |
| **Docker** | — | Contenedorización con multi-stage build |
| **Resend (Java SDK)** | 3.1.0 | API para el envío de correos transaccionales vía HTTP (puerto 443) |
| **Deepgram API** | — | API de transcripción de audio a texto vía HTTP REST. Modelo: `nova-2`, idioma: español (es) |
| **DigitalOcean** | — | Servidor IaaS (Droplet) alojando el contenedor de la aplicación |
| **Cloudflare Tunnels** | — | Exposición segura del servicio a Internet (Zero Trust) sin abrir puertos públicos entrantes |

---

## 🏗 Arquitectura del Proyecto

El proyecto sigue una **Screaming Architecture** (Arquitectura por módulos/dominio), donde la estructura de carpetas grita la intención del negocio en lugar de los detalles técnicos. Cada módulo encapsula su controlador, entidades, DTOs, repositorio y servicio, evitando el caos de carpetas con miles de archivos mezclados.

```
src/main/java/com/feedback/feedback/
│
├── FeedbackApplication.java              # Punto de entrada de la aplicación
│
├── common/                               # Componentes transversales compartidos
│   ├── exception/                        # Manejo global de excepciones
│   │   ├── CustomAccessDeniedHandler     # Handler para acceso denegado (403)
│   │   ├── CustomBasicAuthEntryPoint     # Handler para no autenticado (401)
│   │   ├── EntityNotFoundException       # Excepción personalizada (404)
│   │   ├── ErrorResponse                 # DTO estándar de error
│   │   └── GlobalExceptionHandler        # @RestControllerAdvice global
│   ├── filter/
│   │   └── JwtFilter                     # Filtro que intercepta y valida JWT
│   ├── mapper/
│   │   ├── FeedbackMapper                # Entity ↔ DTO para Feedback
│   │   ├── PrivilegeMapper               # Entity ↔ DTO para Privilege
│   │   ├── RoleMapper                    # Entity ↔ DTO para Role
│   │   ├── RolePrivilegeMapper           # Entity ↔ DTO para RolePrivilege
│   │   └── UserMapper                    # Entity ↔ DTO para User
│   └── util/
│       └── JwtUtil                       # Generación, extracción y validación de JWT
│
├── config/                               # Configuraciones de Spring
│   ├── AuditAwareImpl                    # Proveedor de usuario auditor (JPA Auditing)
│   ├── CustomerUserDetailService         # UserDetailsService personalizado
│   ├── DataSeeder                        # Seed inicial de roles y privilegios
│   ├── JpaAuditingConfig                 # Habilita @CreatedDate, @LastModifiedDate
│   └── SecurityConfig                    # Cadena de filtros de seguridad y CORS
│
└── modules/                              # 🔥 Módulos de dominio (Screaming Architecture)
    │
    ├── auth/                             # Módulo de Autenticación
    │   ├── controller/
    │   │   ├── AuthController            # Endpoints: login, forgot-password, reset, health-check
    │   │   └── dto/
    │   │       ├── LoginRequestDto        # record: username, password
    │   │       ├── LoginResponseDto       # token + UserResponseDto
    │   │       ├── ForgotPasswordRequestDto # token, email, password
    │   │       └── StartForgotPasswordResponseDto # email, link
    |   |       └── ConfigurationResponse # Healthcheck de las apis y servicio externos
    │   ├── entity/
    │   │   └── TokenPasswordResetEntity  # Token temporal para reset de contraseña
    │   ├── repository/
    │   │   └── TokenPasswordResetRepository
    │   └── service/
    │       ├── AuthService               # Interfaz del servicio
    │       └── impl/
    │           └── AuthServiceImpl       # Login con JWT + Forgot/Reset Password
    │
    ├── feedback/                         # Módulo de Feedback Anónimo
    │   ├── controller/
    │   │   └── FeedbackController        # CRUD de feedbacks anónimos
    │   ├── model/
    │   │   ├── dto/
    │   │   │   ├── FeedbackRequestDto     # content, recipientId
    │   │   │   └── FeedbackResponseDto    # id, content, recipient, createdAt
    │   │   └── entity/
    │   │       └── FeedbackEntity        # Entidad: content, recipient (FK), active, createdAt
    │   ├── repository/
    │   │   └── FeedbackRepository
    │   └── service/
    │       ├── FeedbackService
    │       └── impl/
    │           └── FeedbackServiceImpl
    │
    ├── user/                             # Módulo de Usuarios
    │   ├── controller/
    │   │   └── UserController            # CRUD + registro de admin
    │   ├── model/
    │   │   ├── dto/
    │   │   │   ├── UserRequestDto         # username, email, password
    │   │   │   └── UserResponseDto        # id, username, email, role
    │   │   └── entity/
    │   │       └── UserEntity            # username, email, password, role, auditoría
    │   ├── repository/
    │   │   └── UserRepository
    │   └── service/
    │       ├── UserService
    │       └── impl/
    │           └── UserServiceImpl
    │
    ├── role/                             # Módulo de Roles
    │   ├── controller/
    │   │   └── RoleController            # CRUD de roles (solo ADMIN)
    │   ├── model/
    │   │   ├── dto/
    │   │   │   ├── RoleRequestDto         # name, description
    │   │   │   └── RoleResponseDto        # id, name, description
    │   │   └── entity/
    │   │       └── RoleEntity            # name, description, active, auditoría
    │   ├── repository/
    │   │   └── RoleRepository
    │   └── service/
    │       ├── RoleService
    │       └── impl/
    │           └── RoleServiceImpl
    │
    └── privilege/                        # Módulo de Privilegios
        ├── controller/
        │   ├── PrivilegeController       # CRUD + asignación a roles (solo ADMIN)
        │   └── dto/
        │       ├── PrivilegeRequestDto    # name, description
        │       ├── PrivilegeResponseDto   # id, name, description
        │       └── RolePrivilegeResponseDto # rolePrivilegeId, roleName, privilegeName
        ├── entity/
        │   ├── PrivilegeEntity           # name (UNIQUE), description, active, auditoría
        │   └── RolePrivilegeEntity       # Tabla intermedia Role ↔ Privilege
        ├── repository/
        │   ├── PrivilegeRepository
        │   └── RolePrivilegeRepository
        └── service/
            ├── PrivilegeService
            └── impl/
                └── PrivilegeServiceImpl
```

---

## 🗄 Modelo de Base de Datos

### Entidades y Relaciones

```
┌──────────────┐       ┌──────────────┐       ┌───────────────────┐       ┌──────────────────┐
│    roles     │       │    users     │       │    feedbacks      │       │   privileges     │
├──────────────┤       ├──────────────┤       ├───────────────────┤       ├──────────────────┤
│ id (PK)      │◄──┐   │ id (PK)      │◄──┬───│ user_id (FK)      │       │ id (PK)          │
│ name         │   └───│ role_id (FK) │   │   │ content           │       │ name (UNIQUE)    │
│ description  │       │ username     │   │   │ active            │       │ description      │
│ active       │       │ email        │   │   │ created_at        │       │ active           │
│ created_by   │       │ password     │   │   └───────────────────┘       │ created_by       │
│ created_date │       │ active       │   │                               │ created_date     │
│ last_mod_by  │       │ created_by   │   │                               │ last_modified    │
│ last_mod_date│       │ created_date │   │                               │ last_modified_by │
└──────┬───────┘       │ last_modified│   │                               └────────┬─────────┘
       │               │ last_mod_by  │   │                                        │
       │               └──────┬───────┘   │                                        │
       │                      │           │                                        │
       │               ┌──────┘           │                                        │
       │               │                  │                                        │
       │     ┌─────────┴─────────────┐    │                                        │
       │     │   role_privileges     │    │  (Tabla Intermedia)                    │
       │     ├───────────────────────┤    │                                        │
       └────►│ id (PK)               │◄───┴────────────────────────────────────────┘
             │ role_id (FK)          │
             │ privilege_id (FK)     │
             │ active                │
             │ created_by            │
             │ created_date          │
             │ last_modified         │
             │ last_modified_by      │
             └─────────────────────────┘
                       │   
       ┌───────────────┴──────────────┐
       │  token_password_reset        │
       ├──────────────────────────────┤
       │ id (PK)                      │
       │ token (UNIQUE)               │
       │ user_id (FK) ───────────────►│ users
       │ expire_date                  │
       │ used                         │
       └──────────────────────────────┘
```

> **Nota:** La relación entre `roles` y `privileges` se gestiona mediante la entidad intermedia `RolePrivilegeEntity` en lugar de un `@ManyToMany` directo, lo que permite auditoría, control de estado activo y mayor flexibilidad.

---

## 📦 Módulos

### 🔐 Auth
Gestiona la autenticación de usuarios y la recuperación de contraseña.
- **Login:** Autentica al usuario con `username/password` y devuelve un **token JWT** con el rol embebido junto con los datos del usuario (`LoginResponseDto`).
- **Forgot Password:** Genera un token UUID temporal, lo almacena en la BD con expiración de 1 hora y envía un correo HTML al email del usuario con el token de recuperación vía **Resend API**, utilizando la plantilla `templates/index_mail.html`.
- **Reset Password:** Valida el token recibido junto con el email, verifica que no haya expirado, actualiza la contraseña del usuario (encriptada con BCrypt) y marca el token como usado.
- **Health Check:** Endpoint simple (`GET /auth/health-check`) para verificar que el servicio está activo.
- **Comfiguration-Info:** Endpoint de Admin (`GET /auth/configuraation`) para verificar si las apis y servicios estan conectados correctamente

### 💬 Feedback
Módulo principal del sistema. Permite enviar feedback **anónimo** a cualquier usuario registrado, con soporte para texto directo o transcripción de audio mediante **Deepgram API**.
- Crear feedback anónimo con contenido de texto, indicando el `content` y el `recipientId`.
- **Crear feedback desde audio:** Enviar un archivo de audio (`.wav`, `.mp3`, `.flac`, etc.) que se transcribe automáticamente a texto mediante **Deepgram API** (modelo `nova-2`, idioma español). El audio se procesa y se crea el feedback con la transcripción como contenido.
- Listar todos los feedbacks o filtrar por `recipientId`.
- Obtener un feedback específico por su `id`.
- Actualizar y eliminar feedbacks (eliminación lógica vía campo `active`; requiere privilegios).
- Listar los usuarios con rol `OWNER` activos que son destinatarios de feedbacks.

### 👤 User
Gestión completa de usuarios del sistema.
- Registro de usuario normal (público, asigna rol `OWNER` por defecto) y registro de administrador (solo `ADMIN`).
- Consulta de todos los usuarios, usuarios activos y por ID.
- Actualización de usuario (retorna `LoginResponseDto` con nuevo token JWT y datos del usuario actualizado).
- Eliminación lógica de usuarios (requiere privilegios específicos).
- Cada usuario tiene un rol asignado.

### 🎭 Role
Administración de roles del sistema (solo accesible por `ADMIN`).
- CRUD completo de roles.
- Los roles por defecto (`ADMIN`, `OWNER`) se crean automáticamente mediante el `DataSeeder`.

### 🔑 Privilege
Administración de privilegios granulares (solo accesible por `ADMIN`).
- CRUD de privilegios individuales.
- Consulta de las relaciones rol-privilegio existentes (`GET /privileges/roles-privileges`).
- Asignación y remoción de privilegios a roles mediante la entidad intermedia `RolePrivilegeEntity`.
- Privilegios por defecto creados por el `DataSeeder`: `READ_FEEDBACK`, `UPDATE_FEEDBACK`, `DELETE_FEEDBACK`.
- El privilegio `READ_FEEDBACK` se asigna automáticamente al rol `ADMIN` al iniciar la aplicación.

---

## ⚙ Configuración de Entorno

El proyecto utiliza **spring-dotenv** para cargar variables de entorno desde archivos `.env` ubicados en `src/main/resources/`.

### Archivos de entorno

| Archivo | Descripción |
|---|---|
| `local.env` | Variables para desarrollo local |
| `dev.env` | Variables para el entorno de desarrollo/staging |

### Variables de entorno requeridas

```env
# Base de datos PostgreSQL
URL_DB=jdbc:postgresql://<host>:<port>/<database>
USER_DB=<usuario_db>
PASSWORD_DB=<password_db>

# Email Via Resend API 
RESEND_API_KEY=<api_key>

# JWT
JWT_SECRET=<clave_secreta_min_256_bits>
JWT_EXPIRATION=3600000

# Deepgram API (Transcripción de audio)
DEEPGRAM_API_KEY=<api_key_deepgram>

# Perfil activo de Spring (local | dev)
SPRING_PROFILES_ACTIVE=local
```

> **Nota sobre Resend:** La API Key debe ser generada desde el panel de control de [Resend](https://resend.com). Asegúrate de que el dominio `automasilabo.space` esté verificado y configurado en tu cuenta para garantizar la entregabilidad de correos.

> **Nota sobre Deepgram:** La API Key debe ser generada desde el panel de control de [Deepgram](https://console.deepgram.com). El modelo configurado es `nova-2` con idioma español (`es`). Más información: [Deepgram API Documentation](https://developers.deepgram.com/docs/getting-started).

### Configuración YAML (`application.yaml`)

```yaml
server:
  port: 8082

spring:
  application:
    name: feedback
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:local}
  datasource:
    url: ${URL_DB}
    username: ${USER_DB}
    password: ${PASSWORD_DB}
    driver-class-name: org.postgresql.Driver
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  jwt:
    secret: ${JWT_SECRET}
    expiration: ${JWT_EXPIRATION}

resend:
  api:
    key: ${RESEND_API_KEY}

deepgram:
  api:
    key: ${DEEPGRAM_API_KEY}
    url: https://api.deepgram.com/v1/listen?model=nova-2&language=es
```

---

## 🔒 Seguridad

### Autenticación con JWT

1. El usuario se autentica en `/auth/login` con `username` y `password`.
2. El servidor valida las credenciales mediante `AuthenticationManager` y `CustomerUserDetailService`.
3. Se genera un **JWT** firmado con HMAC-SHA256 que contiene el `username` (subject) y el `role` como claim.
4. Cada petición protegida debe enviar el token en el header: `Authorization: Bearer <token>`.
5. El `JwtFilter` intercepta cada request, extrae y valida el token, carga los privilegios del usuario desde `CustomerUserDetailService` y establece el `SecurityContext`.

### Autorización basada en Roles y Privilegios

| Recurso               | Método | Acceso |
|-----------------------|--------|---|
| `/auth/**`            | ALL    | 🌐 Público |
| `/auth/configuration` | GET     | 🔴 Solo `ROLE_ADMIN` |
| `/users`              | POST   | 🌐 Público (registro) |
| `/feedbacks/**`       | POST   | 🌐 Público (envío anónimo) |
| `/feedbacks/owners`   | GET    | 🌐 Público |
| `/users/admin/**`     | POST   | 🔴 Solo `ROLE_ADMIN` |
| `/roles/**`           | ALL    | 🔴 Solo `ROLE_ADMIN` |
| `/privileges/**`      | ALL    | 🔴 Solo `ROLE_ADMIN` |
| `/users/**`           | GET    | 🟡 Requiere `READ_USER` |
| `/users/**`           | PUT    | 🟡 Requiere `UPDATE_USER` |
| `/users/**`           | DELETE | 🟡 Requiere `DELETE_USER` |
| `/feedbacks/**`       | GET    | 🟡 Requiere `READ_FEEDBACK` |
| `/feedbacks/**`       | PUT    | 🟡 Requiere `UPDATE_FEEDBACK` |
| `/feedbacks/**`       | DELETE | 🟡 Requiere `DELETE_FEEDBACK` |

### CORS

Configurado para aceptar todos los orígenes (`*`) con los métodos `GET`, `POST`, `PUT`, `DELETE` y `OPTIONS`. Headers permitidos: `authorization`, `content-type`, `x-requested-with`.

### Data Seeder

Al iniciar la aplicación se crean automáticamente (si no existen):
- **Roles:** `ADMIN`, `OWNER`
- **Privilegios:** `READ_FEEDBACK`, `UPDATE_FEEDBACK`, `DELETE_FEEDBACK`
- **Asignación:** El privilegio `READ_FEEDBACK` se asigna al rol `ADMIN`

### Auditoría JPA

Todas las entidades principales (excepto `FeedbackEntity` y `TokenPasswordResetEntity`) registran automáticamente:
- `created_by` / `created_date` — Quién y cuándo se creó el registro.
- `last_modified_by` / `last_modified_date` — Quién y cuándo se modificó por última vez.

`FeedbackEntity` solo registra `created_at` mediante `@CreatedDate`.

---

## 📡 Endpoints de la API

### 🔐 Auth — `/auth`

| Método | Endpoint | Descripción | Body / Params |
|---|---|---|---|
| `POST` | `/auth/login` | Iniciar sesión | `{ "username", "password" }` |
| `POST` | `/auth/forgot-password/{email}` | Solicitar reset de contraseña | `email` como path variable |
| `PUT` | `/auth/reset-password` | Restablecer contraseña con token | `{ "token", "email", "password" }` |
| `GET` | `/auth/health-check` | Verificar estado del servicio | — |
| `GET` | `/auth/configuration` | Verificar estado de APIs y servicios externos | — (Solo ADMIN) |

**Respuestas destacadas:**
- **Login:** Retorna `{ "token": "jwt...", "user": { "id", "username", "email", "role" } }`
- **Forgot Password:** Retorna `{ "email": "...", "link": "..." }` y envía correo HTML con el token.
- **Reset Password:** Retorna un `String` confirmando el restablecimiento.
- **Configuration:** Retorna `{ "email_status": "OK_RESEND_API|MISSING_RESEND_KEY", "jwt_status": "OK|MISSING", "db_status": "OK|CONNECTION_ERROR|INVALID_CONNECTION" }`

### 👤 Users — `/users`

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `POST` | `/users` | Registrar usuario (rol OWNER) | 🌐 Público |
| `POST` | `/users/admin` | Registrar administrador | 🔴 ADMIN |
| `GET` | `/users` | Listar todos los usuarios | 🟡 READ_USER |
| `GET` | `/users/{id}` | Obtener usuario por ID | 🟡 READ_USER |
| `GET` | `/users/active` | Listar usuarios activos | 🟡 READ_USER |
| `PUT` | `/users/{id}` | Actualizar usuario | 🟡 UPDATE_USER |
| `DELETE` | `/users/{id}` | Eliminar usuario (lógico) | 🟡 DELETE_USER |

> **Nota:** `PUT /users/{id}` retorna un `LoginResponseDto` (token + datos del usuario actualizado).

### 💬 Feedbacks — `/feedbacks`

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `POST` | `/feedbacks` | Crear feedback anónimo con texto | 🌐 Público |
| `POST` | `/feedbacks/audio` | Crear feedback anónimo desde archivo de audio (transcripción automática con Deepgram) | 🌐 Público |
| `GET` | `/feedbacks` | Listar todos los feedbacks | 🟡 READ_FEEDBACK |
| `GET` | `/feedbacks/{recipientId}` | Feedbacks por destinatario | 🟡 READ_FEEDBACK |
| `GET` | `/feedbacks/content/{id}` | Obtener feedback por ID | 🟡 READ_FEEDBACK |
| `GET` | `/feedbacks/owners` | Listar usuarios con rol OWNER activos | 🌐 Público |
| `PUT` | `/feedbacks/{id}` | Actualizar feedback | 🟡 UPDATE_FEEDBACK |
| `DELETE` | `/feedbacks/{id}` | Eliminar feedback (lógico) | 🟡 DELETE_FEEDBACK |

### 🎭 Roles — `/roles`

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `POST` | `/roles` | Crear rol | 🔴 ADMIN |
| `GET` | `/roles` | Listar todos los roles | 🔴 ADMIN |
| `GET` | `/roles/{id}` | Obtener rol por ID | 🔴 ADMIN |
| `PUT` | `/roles/{id}` | Actualizar rol | 🔴 ADMIN |
| `DELETE` | `/roles/{id}` | Eliminar rol | 🔴 ADMIN |

### 🔑 Privileges — `/privileges`

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `POST` | `/privileges` | Crear privilegio | 🔴 ADMIN |
| `GET` | `/privileges` | Listar todos los privilegios | 🔴 ADMIN |
| `GET` | `/privileges/{id}` | Obtener privilegio por ID | 🔴 ADMIN |
| `GET` | `/privileges/roles-privileges` | Listar relaciones rol-privilegio | 🔴 ADMIN |
| `PUT` | `/privileges/{id}` | Actualizar privilegio | 🔴 ADMIN |
| `DELETE` | `/privileges/{id}` | Eliminar privilegio | 🔴 ADMIN |
| `POST` | `/privileges/role/{role_id}/privilege/{privilege_id}` | Asignar privilegio a rol | 🔴 ADMIN |
| `DELETE` | `/privileges/role/{role_id}/privilege/{privilege_id}` | Remover privilegio de rol | 🔴 ADMIN |

---

## 🚀 Ejecución Local

### Prerrequisitos

- **Java 21** (JDK)
- **Maven 3.9+**
- **PostgreSQL** (local o remoto)
- **API Key de Resend** — Obtenida desde [resend.com](https://resend.com) con el dominio `automasilabo.space` configurado para envío de correos.
- **API Key de Deepgram** — Obtenida desde [console.deepgram.com](https://console.deepgram.com) para transcripción de audios.

### Pasos

1. **Clonar el repositorio:**
   ```bash
   git clone <url-del-repositorio>
   cd feedback-anonymous-excercise
   ```

2. **Configurar variables de entorno:**

   Crear/editar el archivo `src/main/resources/local.env` (o `dev.env`) con las variables necesarias:
   ```env
   URL_DB=jdbc:postgresql://localhost:5432/feedback_db
   USER_DB=postgres
   PASSWORD_DB=tu_password
   RESEND_API_KEY=tu_api_key_de_resend
   JWT_SECRET=tu_clave_secreta_de_al_menos_256_bits
   JWT_EXPIRATION=3600000
   SPRING_PROFILES_ACTIVE=local
   ```

3. **Ejecutar la aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```
   O en Windows:
   ```cmd
   mvnw.cmd spring-boot:run
   ```

4. **Verificar:**
   ```
   GET http://localhost:8082/auth/health-check → "OK"
   ```

---

## 🐳 Docker

El proyecto incluye un **Dockerfile multi-stage** optimizado para Java 21.

### Build

```bash
docker build -t feedback-app .
```

### Run

```bash
docker run -d \
  -p 8082:8082 \
  -e URL_DB=jdbc:postgresql://host:5432/feedback_db \
  -e USER_DB=postgres \
  -e PASSWORD_DB=tu_password \
  -e RESEND_API_KEY=tu_api_key_de_resend \
  -e DEEPGRAM_API_KEY=tu_api_key_de_deepgram \
  -e JWT_SECRET=tu_clave_secreta \
  -e JWT_EXPIRATION=3600000 \
  --name feedback-api \
  feedback-app
```

### Detalles del Dockerfile

- **Etapa 1 (Build):** Usa `maven:3.9.5-eclipse-temurin-21` para compilar el proyecto con `mvn clean package -DskipTests`.
- **Etapa 2 (Runtime):** Usa `eclipse-temurin:21-jre-alpine` (imagen ligera).
- Ejecuta la aplicación con un **usuario no root** (`appuser`) por seguridad.
- Límite de memoria JVM configurado en `-Xmx300m`.
- Puerto expuesto: **8082**.

---

## ☁ Despliegue

### Infraestructura

La aplicación cuenta con una arquitectura de despliegue moderna, segura y autogestionada, separando la capa de datos de la lógica de negocio y aplicando principios de red *Zero Trust*.

### Arquitectura de Red y Servicios

| Componente | Plataforma | Descripción |
|---|---|---|
| **Base de Datos** | [Supabase](https://supabase.com) | Instancia PostgreSQL gestionada en la nube conectada a través de un pooler transaccional (puerto 6543). |
| **API Backend** | [DigitalOcean](https://digitalocean.com) | Droplet (Servidor Virtual Linux) ejecutando la aplicación dentro de un contenedor Docker aislado. |
| **Exposición Web** | [Cloudflare Tunnels](https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/) | Demonio (`cloudflared`) en el servidor que crea un túnel cifrado saliente hacia Cloudflare. Esto permite enrutar tráfico HTTP/HTTPS al dominio oficial (`feedback-api.automasilabo.space`) **sin necesidad de abrir puertos entrantes (como el 80 o 443) en el firewall de DigitalOcean**, previniendo ataques directos al servidor. |
| **Mailing** | [Resend](https://resend.com) | Servicio de envío de correos transaccionales vía HTTP REST (puerto 443). Configurado con el dominio `automasilabo.space` para garantizar entregabilidad. Elude eficazmente los bloqueos de puertos SMTP (25/465/587) en DigitalOcean. |

### Flujo de Despliegue Manual (Git -> Docker)

El proceso de actualización en el servidor de producción en DigitalOcean sigue un enfoque basado en control de versiones y empaquetado con contenedores Docker:

1. **Fusión de cambios** en la rama `main` en el repositorio remoto (GitHub).
2. **Conexión SSH** al Droplet de DigitalOcean.
3. **Extracción de los nuevos cambios** en la carpeta del proyecto:
   ```bash
   cd /ruta/del/proyecto && git pull origin main
   ```
4. **Reconstrucción de la imagen** optimizada (Multi-stage) para compilar el `.jar`:
   ```bash
   docker build -t feedback-app:latest .
   ```
5. **Detención del contenedor anterior** y **arranque del nuevo**:
   ```bash
   docker stop feedback-api
   docker rm feedback-api
   docker run -d \
     --name feedback-api \
     --network general-network \
     -e URL_DB=<supabase_url> \
     -e USER_DB=<supabase_user> \
     -e PASSWORD_DB=<supabase_password> \
     -e RESEND_API_KEY=<api_key> \
     -e DEEPGRAM_API_KEY=<api_key_deepgram> \
     -e JWT_SECRET=<jwt_secret> \
     -e JWT_EXPIRATION=3600000 \
     -e SPRING_PROFILES_ACTIVE=dev \
     feedback-app:latest
   ```

> **Red `general-network`:** El contenedor está conectado a la red Docker `general-network` donde también reside el demonio de **Cloudflare Tunnel** (`cloudflared`), permitiendo enrutar el tráfico cifrado hacia el dominio `feedback-api.automasilabo.space` sin exponer puertos públicamente.

---

## 📧 Recuperación de Contraseña — Flujo

```
1. POST /auth/forgot-password/{email}
   ├── Se valida que el email exista en la BD
   ├── Se genera un token UUID único
   ├── Se almacena en token_password_reset con expiración de 1 hora
   ├── Se envía un correo HTML (templates/index_mail.html) con el token al email mediante Resend API
   |  (El correo incluye un link al frontend con el token como query param, e.g. https://feedback-api.automasilabo.space/reset-password?token=xxx)
   └── Se responde con { email, link }

2. PUT /auth/reset-password
   ├── Se recibe { token, email, password }
   ├── Se busca el token en la BD
   ├── Se valida que no haya expirado
   ├── Se busca al usuario por email
   ├── Se actualiza la contraseña del usuario (encriptada con BCrypt)
   ├── Se marca el token como usado
   └── Se responde con mensaje de éxito
```

El correo enviado utiliza una **plantilla HTML profesional** (`templates/index_mail.html`) con el branding de Feedback App, incluyendo el token y los datos de recuperación.

---

## 🎤 Transcripción de Audio — Deepgram

### Flujo de Procesamiento

La aplicación integra **Deepgram API** para transcribir archivos de audio a texto automáticamente en la creación de feedbacks anónimos.

```
1. POST /feedbacks/audio (multipart/form-data)
   ├── Se recibe un archivo de audio (audio, recipientId)
   ├── Se valida que el archivo no sea null o esté vacío
   ├── Se envía el audio a Deepgram API mediante RestClient
   │  └── Endpoint: https://api.deepgram.com/v1/listen?model=nova-2&language=es
   │  └── Headers: Authorization: Token <DEEPGRAM_API_KEY>, Content-Type: <tipo_de_audio>
   ├── Deepgram procesa el audio y retorna JSON con la transcripción
   ├── Se extrae el texto transcrito desde: results.channels[0].alternatives[0].transcript
   ├── Se crea un FeedbackRequestDto con el contenido transcrito
   ├── Se invoca createFeedback() con el contenido del texto
   └── Se responde con FeedbackResponseDto (id, content, recipient, createdAt)
```

### Formatos de Audio Soportados

Deepgram soporta múltiples formatos de audio. Los más comunes incluyen:
- **WAV** — Formato sin compresión (recomendado para máxima calidad)
- **MP3** — Formato comprimido con pérdida (tamaño reducido)
- **FLAC** — Formato sin pérdida (compresión lossless)
- **OGG** — Contenedor Ogg Vorbis
- **M4A** — Contenedor MPEG-4 Audio

El header `Content-Type` se obtiene directamente del archivo enviado (`audioFile.getContentType()`).

### Configuración en application.yaml

```yaml
deepgram:
  api:
    key: ${DEEPGRAM_API_KEY}
    url: https://api.deepgram.com/v1/listen?model=nova-2&language=es
```

**Parámetros:**
- **model=nova-2** — Modelo de Deepgram más reciente y preciso
- **language=es** — Idioma español para mejor reconocimiento

### Servicio DeepgramService

La clase `DeepgramService` encapsula la lógica de transcripción:

```java
@Service
public class DeepgramService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public DeepgramService(
            @Value("${deepgram.api.url}") String apiUrl,
            @Value("${deepgram.api.key}") String apiKey) {
        // Configuración del RestClient con headers de autenticación
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Token " + apiKey)
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    public String transcribeAudio(MultipartFile audioFile) {
        // Validación del archivo
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("El archivo de audio no puede estar vacío");
        }
        
        try {
            // Petición POST a Deepgram
            String response = restClient.post()
                    .header("Content-Type", audioFile.getContentType())
                    .body(audioFile.getBytes())
                    .retrieve()
                    .body(String.class);

            // Parsing del JSON response
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode transcriptNode = rootNode
                    .path("results")
                    .path("channels").path(0)
                    .path("alternatives").path(0)
                    .path("transcript");

            if (transcriptNode.isMissingNode()) {
                throw new RuntimeException(
                    "No se pudo extraer la transcripción de la respuesta de Deepgram"
                );
            }

            return transcriptNode.asText();
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el buffer del audio", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al transcribir el audio", e);
        }
    }
}
```

### Uso en FeedbackServiceImpl

```java
@Override
public FeedbackResponseDto createFeedbackWithAudio(MultipartFile audio, Long recipientId) {
    if (audio == null || audio.isEmpty()) {
        throw new RuntimeException("Error: El audio no puede ser nulo o estar vacío");
    }
    
    // Transcribir audio
    String content = deepgramService.transcribeAudio(audio);
    
    // Crear feedback con el contenido transcrito
    FeedbackRequestDto request = new FeedbackRequestDto(content, recipientId);
    return createFeedback(request);
}
```

### Ejemplo de Uso vía cURL

```bash
curl -X POST http://localhost:8082/feedbacks/audio \
  -F "audio=@archivo_audio.wav" \
  -F "recipientId=1"
```

### Ejemplo de Respuesta

```json
{
  "id": 1,
  "content": "Este es el contenido transcrito del archivo de audio",
  "recipient": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "OWNER"
  },
  "createdAt": "2026-04-18T15:30:00Z"
}
```

### Manejo de Errores

El servicio maneja los siguientes errores:

| Escenario | Error | HTTP Status |
|---|---|---|
| Archivo vacío o null | `IllegalArgumentException` | 400 |
| Error de I/O al leer archivo | `RuntimeException` | 500 |
| Respuesta JSON sin transcripción | `RuntimeException` | 500 |
| Credenciales Deepgram inválidas | `RuntimeException` (Unauthorized) | 500 |
| Conexión rechazada por Deepgram | `RuntimeException` | 500 |

---

## 🧩 Manejo Global de Excepciones

El `GlobalExceptionHandler` captura y formatea las siguientes excepciones:

| Excepción | HTTP Status | Descripción |
|---|---|---|
| `EntityNotFoundException` | 404 | Entidad no encontrada en la BD |
| `IllegalArgumentException` | 400 | Argumento inválido |
| `ExpiredJwtException` | 401 | Token JWT expirado |
| `JwtException` | 401 | Token JWT inválido o malformado |
| `MethodArgumentNotValidException` | 400 | Errores de validación en DTOs (retorna mapa campo → mensaje) |
| `Exception` | 500 | Error genérico interno del servidor |

Adicionalmente, el `JwtFilter` maneja errores JWT directamente en el filtro de seguridad con respuestas JSON personalizadas, y se cuenta con:
- `CustomBasicAuthenticationEntryPoint` — Respuesta JSON personalizada para errores 401 (no autenticado).
- `CustomAccessDeniedHandler` — Respuesta JSON personalizada para errores 403 (acceso denegado).

Formato estándar de respuesta de error:
```json
{
  "status": 404,
  "message": "Usuario no encontrado",
  "timestamp": "2026-03-06T10:30:00"
}
```

---

## 📝 Notas Adicionales

- **Mappers manuales:** Se utilizan clases `Mapper` personalizadas en `common/mapper/` (`UserMapper`, `FeedbackMapper`, `RoleMapper`, `PrivilegeMapper`, `RolePrivilegeMapper`) para la conversión Entity ↔ DTO sin dependencias externas como MapStruct.
- **JPA Auditing:** Habilitado con `@EnableJpaAuditing` y `AuditAwareImpl` para rastrear automáticamente quién crea/modifica cada registro.
- **Tabla intermedia explícita:** La relación roles-privilegios usa `RolePrivilegeEntity` en lugar de `@ManyToMany`, lo cual es una mejor práctica porque permite campos adicionales como `active`, auditoría y control individual.
- **CustomerUserDetailService:** Carga los privilegios del usuario consultando directamente la tabla intermedia `role_privileges` por `role_id`, evitando relaciones bidireccionales innecesarias en las entidades. Las authorities incluyen `ROLE_<nombre>` + los nombres de los privilegios activos.
- **Eliminación lógica (Soft Delete):** Tanto usuarios como feedbacks se eliminan lógicamente estableciendo `active = false` en lugar de borrar el registro de la BD.
- **DevTools:** Incluido para recarga en caliente durante el desarrollo.
- **DTOs como records:** `LoginRequestDto` utiliza Java Records para mayor concisión; los demás DTOs usan clases con Lombok.

---

> Desarrollado con ☕ Java 21 + Spring Boot 4.0.2 por **@IzpoDev**

