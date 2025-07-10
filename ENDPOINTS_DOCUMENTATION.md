# Tetoca API - Documentación Exhaustiva de Endpoints

## Tabla de Contenidos

1. [Introducción](#introducción)
2. [Arquitectura y Autenticación](#arquitectura-y-autenticación)
3. [Endpoints Globales (SaaS Admin)](#endpoints-globales-saas-admin)
4. [Endpoints de Autenticación](#endpoints-de-autenticación)
5. [Endpoints de Tenant (Multi-tenant)](#endpoints-de-tenant-multi-tenant)
6. [Modelos de Datos](#modelos-de-datos)
7. [Códigos de Estado HTTP](#códigos-de-estado-http)
8. [Ejemplos de Uso](#ejemplos-de-uso)

---

## Introducción

Tetoca API es una aplicación Spring Boot multi-tenant que gestiona sistemas de colas para múltiples empresas. La API está dividida en dos niveles principales:

- **Nivel Global**: Administración SaaS para gestionar empresas/tenants
- **Nivel Tenant**: Funcionalidades específicas de cada empresa (colas, turnos, trabajadores)

### URL Base

```
Base URL: https://localhost:8080
```

---

## Arquitectura y Autenticación

### Tipos de Usuarios

1. **SaaS Admin**: Administrador del sistema global
2. **Company Admin**: Administrador de empresa/tenant
3. **Division Admin**: Administrador de división
4. **Agency Admin**: Administrador de agencia
5. **Operator**: Operario que atiende colas
6. **Client**: Cliente que se une a colas

### Autenticación

- **JWT Tokens**: Todos los endpoints autenticados requieren Bearer token
- **Roles**: Sistema basado en roles con `@PreAuthorize`
- **Multi-tenant**: Los endpoints de tenant requieren `tenantId` en la URL

---

## Endpoints Globales (SaaS Admin)

### Gestión de Empresas

#### 1. Registrar Nueva Empresa

```http
POST /api/admin/companies
```

**Autenticación**: Requerida (`SAAS_ADMIN`)

**Headers**:

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body**:

```json
{
  "company": {
    "name": "Empresa Demo S.A.C.",
    "ruc": "20123456789",
    "email": "admin@empresademo.com",
    "categoryCode": 1
  },
  "instance": {
    "tenantId": "empresa_demo",
    "dbName": "tetoca_empresa_demo",
    "dbUri": "jdbc:postgresql://localhost:5432/tetoca_empresa_demo",
    "dbUser": "empresa_demo_user",
    "dbPassword": "secure_password123",
    "dbTypeCode": 1
  }
}
```

**Response** (201 Created):

```json
{
  "id": 1,
  "name": "Empresa Demo S.A.C.",
  "ruc": "20123456789",
  "email": "admin@empresademo.com",
  "category": "Financiero",
  "state": "ACTIVO",
  "tenantId": "empresa_demo",
  "recordStatus": "A",
  "registerDate": 20241210
}
```

**Validaciones**:

- `name`: Máximo 100 caracteres, no vacío
- `ruc`: Exactamente 11 dígitos
- `email`: Formato válido de email
- `tenantId`: Máximo 50 caracteres, único
- `dbName`, `dbUri`, `dbUser`, `dbPassword`: No vacíos

---

#### 2. Obtener Empresa por ID

```http
GET /api/admin/companies/{id}
```

**Autenticación**: Requerida (`SAAS_ADMIN`)

**Path Parameters**:

- `id` (Integer): ID de la empresa

**Response** (200 OK):

```json
{
  "id": 1,
  "name": "Empresa Demo S.A.C.",
  "ruc": "20123456789",
  "email": "admin@empresademo.com",
  "category": "Financiero",
  "state": "ACTIVO",
  "tenantId": "empresa_demo",
  "recordStatus": "A",
  "registerDate": 20241210
}
```

---

#### 3. Listar Todas las Empresas

```http
GET /api/admin/companies
```

**Autenticación**: Requerida (`SAAS_ADMIN`)

**Response** (200 OK):

```json
[
  {
    "id": 1,
    "name": "Empresa Demo S.A.C.",
    "ruc": "20123456789",
    "email": "admin@empresademo.com",
    "category": "Financiero",
    "state": "ACTIVO",
    "tenantId": "empresa_demo",
    "recordStatus": "A",
    "registerDate": 20241210
  }
]
```

---

#### 4. Actualizar Empresa

```http
PUT /api/admin/companies/{id}
```

**Autenticación**: Requerida (`SAAS_ADMIN`)

**Path Parameters**:

- `id` (Integer): ID de la empresa

**Request Body**:

```json
{
  "name": "Empresa Demo Actualizada S.A.C.",
  "ruc": "20123456789",
  "email": "nuevo-admin@empresademo.com",
  "categoryCode": 2
}
```

**Response** (200 OK):

```json
{
  "id": 1,
  "name": "Empresa Demo Actualizada S.A.C.",
  "ruc": "20123456789",
  "email": "nuevo-admin@empresademo.com",
  "category": "Retail",
  "state": "ACTIVO",
  "tenantId": "empresa_demo",
  "recordStatus": "A",
  "registerDate": 20241210
}
```

---

#### 5. Eliminar Empresa

```http
DELETE /api/admin/companies/{id}
```

**Autenticación**: Requerida (`SAAS_ADMIN`)

**Path Parameters**:

- `id` (Integer): ID de la empresa

**Response** (204 No Content)

---

## Endpoints de Autenticación

### 1. Login SaaS Admin

```http
POST /api/auth/admin/login
```

**Autenticación**: No requerida

**Request Body**:

```json
{
  "username": "admin@tetoca.com",
  "password": "admin123"
}
```

**Response** (200 OK):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Validaciones**:

- `username`: No vacío (email del admin)
- `password`: No vacío

---

### 2. Login Trabajador (Worker/Operator)

```http
POST /api/auth/worker/login/{tenantId}
```

**Autenticación**: No requerida

**Path Parameters**:

- `tenantId` (String): ID del tenant

**Request Body**:

```json
{
  "username": "operador@empresademo.com",
  "password": "password123"
}
```

**Response** (200 OK):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 3. Login/Registro Cliente OAuth

```http
POST /api/auth/client/oauth
```

**Autenticación**: No requerida

**Request Body**:

```json
{
  "provider": "google",
  "token": "ya29.a0AfH6SMBxxx..."
}
```

**Response** (200 OK):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Validaciones**:

- `provider`: No vacío ("google", "facebook", etc.)
- `token`: No vacío (token del proveedor OAuth)

---

## Endpoints de Tenant (Multi-tenant)

### Endpoints Públicos

#### 1. Ping del Tenant

```http
GET /api/tenant/{tenantId}/ping
```

**Autenticación**: No requerida

**Path Parameters**:

- `tenantId` (String): ID del tenant

**Response** (200 OK):

```json
"Tenant database 'empresa_demo' is reachable."
```

---

#### 2. Listar Agencias (Empresas)

```http
GET /api/tenant/{tenantId}/public/agencies
```

**Autenticación**: No requerida

**Path Parameters**:

- `tenantId` (String): ID del tenant

**Query Parameters**:

- `page` (Integer, default: 1): Número de página
- `limit` (Integer, default: 10): Cantidad por página

**Response** (200 OK):

```json
{
  "content": [
    {
      "id": "1",
      "name": "Agencia Centro",
      "shortName": "Centro",
      "type": "Sucursal",
      "logo": "https://...",
      "address": "Av. Principal 123",
      "schedule": "L-V: 8:00-18:00",
      "phone": "+51 1 234-5678",
      "isAvailable": true,
      "activeQueues": 3,
      "queues": [...]
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

---

#### 3. Buscar Agencias

```http
GET /api/tenant/{tenantId}/public/agencies/search
```

**Autenticación**: No requerida

**Query Parameters**:

- `q` (String): Término de búsqueda
- `page` (Integer, default: 1): Número de página
- `limit` (Integer, default: 10): Cantidad por página

**Response** (200 OK): Igual al anterior

---

#### 4. Obtener Detalles de Agencia

```http
GET /api/tenant/{tenantId}/public/agencies/{agencyId}
```

**Autenticación**: No requerida

**Path Parameters**:

- `tenantId` (String): ID del tenant
- `agencyId` (Integer): ID de la agencia

**Response** (200 OK):

```json
{
  "id": "1",
  "name": "Agencia Centro",
  "shortName": "Centro",
  "type": "Sucursal",
  "logo": "https://...",
  "address": "Av. Principal 123",
  "schedule": "L-V: 8:00-18:00",
  "phone": "+51 1 234-5678",
  "isAvailable": true,
  "activeQueues": 3,
  "queues": [
    {
      "id": "1",
      "name": "Atención General",
      "icon": "general",
      "peopleWaiting": 5,
      "avgTime": "15 min",
      "enterpriseId": "1",
      "isActive": true,
      "currentTicket": "A001",
      "waitTimePerPerson": "3 min"
    }
  ]
}
```

---

#### 5. Obtener Colas por Agencia

```http
GET /api/tenant/{tenantId}/public/agencies/{agencyId}/queues
```

**Autenticación**: No requerida

**Response** (200 OK):

```json
[
  {
    "id": "1",
    "name": "Atención General",
    "icon": "general",
    "peopleWaiting": 5,
    "avgTime": "15 min",
    "enterpriseId": "1",
    "isActive": true,
    "currentTicket": "A001",
    "waitTimePerPerson": "3 min"
  }
]
```

---

#### 6. Obtener Detalles de Cola

```http
GET /api/tenant/{tenantId}/public/queues/{queueId}
```

**Autenticación**: No requerida

**Response** (200 OK):

```json
{
  "id": "1",
  "name": "Atención General",
  "icon": "general",
  "peopleWaiting": 5,
  "avgTime": "15 min",
  "enterpriseId": "1",
  "isActive": true,
  "currentTicket": "A001",
  "waitTimePerPerson": "3 min",
  "enterprise": {
    "id": "1",
    "name": "Agencia Centro",
    "address": "Av. Principal 123"
  }
}
```

---

#### 7. Obtener Categorías

```http
GET /api/tenant/{tenantId}/public/categories
```

**Autenticación**: No requerida

**Response** (200 OK):

```json
[
  {
    "id": "1",
    "name": "Atención al Cliente",
    "iconName": "customer_service",
    "color": "#3498db"
  }
]
```

---

### Endpoints de Cliente

#### 1. Unirse a una Cola

```http
POST /api/tenant/{tenantId}/queues/{queueId}/join
```

**Autenticación**: Requerida (`ROLE_CLIENT`)

**Path Parameters**:

- `tenantId` (String): ID del tenant
- `queueId` (Integer): ID de la cola

**Request Body**:

```json
{
  "pushToken": "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]"
}
```

**Response** (201 Created):

```json
{
  "id": 12345,
  "queueRegistration": {
    "id": 567,
    "queue": {
      "id": 1,
      "name": "Atención General"
    },
    "companyClient": {
      "id": 123,
      "fullName": "Juan Pérez"
    },
    "registrationDateTime": 1701234567890,
    "registrationMethod": "APP"
  },
  "turnStatus": {
    "id": 1,
    "name": "EN_ESPERA"
  },
  "operator": null,
  "orderNumber": 15,
  "generationDateTime": 1701234567890
}
```

**Validaciones**:

- `pushToken`: No vacío (token de notificaciones push)

---

### Endpoints de Operario

#### 1. Llamar Siguiente Turno

```http
POST /api/tenants/{tenantId}/operator/queues/{queueId}/call-next
```

**Autenticación**: Requerida (`ROLE_OPERATOR` + asignado a la cola)

**Path Parameters**:

- `tenantId` (String): ID del tenant
- `queueId` (Integer): ID de la cola

**Response** (200 OK):

```json
{
  "id": 12345,
  "queueRegistration": {
    "companyClient": {
      "fullName": "Juan Pérez"
    }
  },
  "turnStatus": {
    "name": "LLAMADO"
  },
  "operator": {
    "worker": {
      "fullName": "María González"
    }
  },
  "orderNumber": 15,
  "generationDateTime": 1701234567890
}
```

---

#### 2. Completar Turno

```http
POST /api/tenants/{tenantId}/operator/turns/{turnId}/complete
```

**Autenticación**: Requerida (`ROLE_OPERATOR`)

**Path Parameters**:

- `tenantId` (String): ID del tenant
- `turnId` (Long): ID del turno

**Response** (200 OK):

```json
{
  "id": 12345,
  "turnStatus": {
    "name": "COMPLETADO"
  },
  "orderNumber": 15
}
```

---

#### 3. Marcar como Ausente

```http
POST /api/tenants/{tenantId}/operator/turns/{turnId}/mark-as-absent
```

**Autenticación**: Requerida (`ROLE_OPERATOR`)

**Path Parameters**:

- `tenantId` (String): ID del tenant
- `turnId` (Long): ID del turno

**Response** (200 OK):

```json
{
  "id": 12345,
  "turnStatus": {
    "name": "AUSENTE"
  },
  "orderNumber": 15
}
```

---

### Endpoints de Administración de Tenant

#### 1. Crear Administrador de Empresa

```http
POST /api/tenant/{tenantId}/admin/company-admins
```

**Autenticación**: Requerida (`SAAS_ADMIN` o `COMPANY_ADMIN`)

**Path Parameters**:

- `tenantId` (String): ID del tenant

**Request Body**:

```json
{
  "fullName": "Carlos Rodríguez",
  "email": "carlos@empresademo.com",
  "password": "password123",
  "workerTypeId": 1,
  "phone": "+51987654321"
}
```

**Response** (201 Created):

```json
{
  "id": 123,
  "fullName": "Carlos Rodríguez",
  "email": "carlos@empresademo.com",
  "phone": "+51987654321",
  "workerType": "ADMIN_EMPRESA",
  "recordStatus": "A",
  "roles": ["ROLE_COMPANY_ADMIN"]
}
```

**Validaciones**:

- `fullName`: Máximo 100 caracteres, no vacío
- `email`: Formato válido, máximo 100 caracteres, único
- `password`: Mínimo 8 caracteres
- `phone`: Máximo 15 caracteres (opcional)

---

## Modelos de Datos

### CompanyClient (Cliente)

```json
{
  "id": 123,
  "externalUid": "google_123456789",
  "fullName": "Juan Pérez García",
  "email": "juan.perez@email.com",
  "registrationDate": 20241210,
  "recordStatus": "A"
}
```

### Turn (Turno)

```json
{
  "id": 12345,
  "queueRegistration": {
    "id": 567,
    "queue": {...},
    "companyClient": {...},
    "registrationDateTime": 1701234567890,
    "registrationMethod": "APP"
  },
  "turnStatus": {
    "id": 1,
    "name": "EN_ESPERA",
    "description": "El turno está en espera"
  },
  "operator": {
    "workerId": 456,
    "worker": {...},
    "agency": {...}
  },
  "orderNumber": 15,
  "generationDateTime": 1701234567890
}
```

### Queue (Cola)

```json
{
  "id": 1,
  "queueType": {...},
  "agency": {...},
  "queueStatus": {
    "id": 1,
    "name": "ABIERTA"
  },
  "name": "Atención General",
  "maxCapacity": 50,
  "recordStatus": "A"
}
```

### Worker (Trabajador)

```json
{
  "id": 123,
  "workerType": {
    "id": 1,
    "name": "OPERARIO"
  },
  "fullName": "María González",
  "email": "maria@empresademo.com",
  "phone": "+51987654321",
  "recordStatus": "A"
}
```

---

## Códigos de Estado HTTP

### Códigos de Éxito

- **200 OK**: Operación exitosa
- **201 Created**: Recurso creado exitosamente
- **204 No Content**: Operación exitosa sin contenido

### Códigos de Error del Cliente

- **400 Bad Request**: Datos de entrada inválidos
- **401 Unauthorized**: Token de autenticación requerido o inválido
- **403 Forbidden**: Sin permisos para la operación
- **404 Not Found**: Recurso no encontrado
- **409 Conflict**: Conflicto (ej. email duplicado)

### Códigos de Error del Servidor

- **500 Internal Server Error**: Error interno del servidor

### Ejemplos de Respuestas de Error

**400 Bad Request**:

```json
{
  "timestamp": "2024-12-10T15:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/tenant/empresa_demo/queues/1/join",
  "errors": [
    {
      "field": "pushToken",
      "message": "El token de notificación (pushToken) no puede estar vacío."
    }
  ]
}
```

**401 Unauthorized**:

```json
{
  "timestamp": "2024-12-10T15:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token JWT inválido o expirado",
  "path": "/api/tenant/empresa_demo/queues/1/join"
}
```

**403 Forbidden**:

```json
{
  "timestamp": "2024-12-10T15:30:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "No tiene permisos para acceder a este recurso",
  "path": "/api/tenants/empresa_demo/operator/queues/1/call-next"
}
```

**404 Not Found**:

```json
{
  "timestamp": "2024-12-10T15:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Cola con ID 999 no encontrada",
  "path": "/api/tenant/empresa_demo/public/queues/999"
}
```

---

## Ejemplos de Uso

### Flujo Completo: Cliente se Une a una Cola

#### 1. Cliente se autentica con OAuth

```bash
curl -X POST https://api.tetoca.com/api/auth/client/oauth \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "google",
    "token": "ya29.a0AfH6SMBxxx..."
  }'
```

#### 2. Cliente consulta agencias disponibles

```bash
curl -X GET https://api.tetoca.com/api/tenant/empresa_demo/public/agencies
```

#### 3. Cliente consulta colas de una agencia específica

```bash
curl -X GET https://api.tetoca.com/api/tenant/empresa_demo/public/agencies/1/queues
```

#### 4. Cliente se une a una cola

```bash
curl -X POST https://api.tetoca.com/api/tenant/empresa_demo/queues/1/join \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "pushToken": "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]"
  }'
```

### Flujo Completo: Operario Atiende Turnos

#### 1. Operario se autentica

```bash
curl -X POST https://api.tetoca.com/api/auth/worker/login/empresa_demo \
  -H "Content-Type: application/json" \
  -d '{
    "username": "operador@empresademo.com",
    "password": "password123"
  }'
```

#### 2. Operario llama al siguiente turno

```bash
curl -X POST https://api.tetoca.com/api/tenants/empresa_demo/operator/queues/1/call-next \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 3. Operario completa el turno

```bash
curl -X POST https://api.tetoca.com/api/tenants/empresa_demo/operator/turns/12345/complete \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Flujo de Administración: SaaS Admin Registra Nueva Empresa

#### 1. SaaS Admin se autentica

```bash
curl -X POST https://api.tetoca.com/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@tetoca.com",
    "password": "admin123"
  }'
```

#### 2. SaaS Admin registra nueva empresa

```bash
curl -X POST https://api.tetoca.com/api/admin/companies \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "company": {
      "name": "Nueva Empresa S.A.C.",
      "ruc": "20987654321",
      "email": "admin@nuevaempresa.com",
      "categoryCode": 1
    },
    "instance": {
      "tenantId": "nueva_empresa",
      "dbName": "tetoca_nueva_empresa",
      "dbUri": "jdbc:postgresql://localhost:5432/tetoca_nueva_empresa",
      "dbUser": "nueva_empresa_user",
      "dbPassword": "secure_password456",
      "dbTypeCode": 1
    }
  }'
```

#### 3. SaaS Admin lista todas las empresas

```bash
curl -X GET https://api.tetoca.com/api/admin/companies \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Notas Adicionales

### Notificaciones Push

- El sistema utiliza Expo Push Notifications para notificar a los clientes
- Cuando un turno es llamado, se envía automáticamente una notificación push
- El `pushToken` debe ser un token válido de Expo

### Manejo de Errores

- Todos los endpoints manejan errores de validación con códigos HTTP apropiados
- Los mensajes de error incluyen detalles específicos sobre qué validación falló
- Las excepciones personalizadas incluyen `ResourceNotFoundException`, `QueueClosedException`, etc.

### Base de Datos Multi-tenant

- Cada empresa tiene su propia base de datos (tenant)
- El `tenantId` en la URL determina qué base de datos utilizar
- La configuración de datasource se maneja dinámicamente

### Seguridad

- Autenticación basada en JWT tokens
- Autorización granular con `@PreAuthorize`
- Validación de permisos específicos (ej. operario asignado a cola específica)
- Encriptación de contraseñas con BCrypt

Esta documentación cubre todos los endpoints disponibles en el proyecto Tetoca API. Para mayor información sobre la implementación específica, consulte el código fuente de cada controlador y servicio.
