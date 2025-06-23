![](./.github/tetoca_cover.png)

# Documentación de Endpoints de la API Tetoca

Esta documentación detalla todos los endpoints disponibles en la API REST 
de Tetoca. La API está diseñada siguiendo un modelo multitenant, donde cada 
empresa opera en un contexto aislado (`tenantId`).

## Autenticación y Autorización

La mayoría de los endpoints de la API están protegidos. Las solicitudes a 
endpoints protegidos deben incluir una cabecera de autorización con un 
JSON Web Token (JWT) válido.

`Authorization: Bearer <YOUR_JWT_HERE>`

El token se obtiene a través de los endpoints de autenticación 
correspondientes a cada actor.

---

## Endpoints de Autenticación (Públicos)

Estos endpoints son la puerta de entrada para los diferentes actores de 
la plataforma.

### 1. Login de Administrador SaaS

-   **Método HTTP:** `POST`
-   **URL:** `/api/auth/admin/login`
-   **Descripción:** Autentica a un Administrador SaaS usando email y 
    contraseña, y devuelve un JWT con el rol `SAAS_ADMIN`.
-   **Request Body:**
    ```json
    {
      "username": "admin@tetoca.com",       // (String) Email del Admin SaaS.
      "password": "a_strong_password"       // (String) Contraseña del Admin SaaS.
    }
    ```
-   **Response Body (200 OK):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYWFzOmFkbWluQHRldG9jYS5jb20iLCJpYXQiOjE2NjkzMzIwNzgsImV4cCI6MTY2OTQxODQ3OH0.abc123xyz" // (String) JSON Web Token para usar en solicitudes posteriores.
    }
    ```

### 2. Login de Trabajador (Tenant)

-   **Método HTTP:** `POST`
-   **URL:** `/api/auth/worker/login/{tenantId}`
-   **Descripción:** Autentica a un trabajador (Admin Empresa, Operario, 
    etc.) de una empresa específica. El `tenantId` en la URL es crucial para 
    dirigir la autenticación a la base de datos correcta.
-   **Request Body:**
    ```json
    {
      "username": "operario1@empresa.com",      // (String) Email del trabajador.
      "password": "worker_password"             // (String) Contraseña del trabajador.
    }
    ```
-   **Response Body (200 OK):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3b3JrZXI6b3BlcmFyaW8xQGVtcHJlc2EuY29tIiwiaWF0IjoxNjY5MzMyMDc4LCJleHAiOjE2Njk0MTg0Nzh9.def456uvw" // (String) JWT con los roles dinámicos del trabajador.
    }
    ```

### 3. Login/Registro de Cliente Final (OAuth)

-   **Método HTTP:** `POST`
-   **URL:** `/api/auth/client/oauth`
-   **Descripción:** Permite a un cliente final iniciar sesión o registrarse 
    usando un token de un proveedor OAuth (actualmente solo Google). El 
    backend valida el token, crea o recupera el usuario en la base de datos 
    Global y devuelve un JWT con el rol `CLIENT`.
-   **Request Body:**
    ```json
    {
      "provider": "google",         // (String) Proveedor de OAuth. Por ahora, solo "google".
      "token": "ey..."              // (String) El id_token proporcionado por el SDK de Google Sign-In.
    }
    ```
-   **Response Body (200 OK):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvYXV0aDpnb29nbGVfMTAxMjM0NTY3ODkwIiwiaWF0IjoxNjY5MzMyMDc4LCJleHAiOjE2Njk0MTg0Nzh9.ghi789pqr" // (String) JWT para el cliente final.
    }
    ```

---

## Endpoints del Administrador SaaS

**Autorización:** Requiere un JWT con el rol `ROLE_SAAS_ADMIN`.

### 1. Registrar Nueva Empresa

-   **Método HTTP:** `POST`
-   **URL:** `/api/admin/companies`
-   **Descripción:** Crea una nueva empresa (tenant), su instancia de base 
    de datos asociada y dispara el provisionamiento asíncrono de la nueva 
    base de datos.
-   **Request Body:**
    ```json
    {
      "company": {
        "name": "Innovate Solutions S.A.C.",    // (String) Nombre comercial de la empresa.
        "ruc": "20609876543",                   // (String) RUC de 11 dígitos.
        "email": "contacto@innovate.com",       // (String) Email de contacto.
        "categoryCode": 1                       // (Integer) ID de la categoría de la empresa.
      },
      "instance": {
        "tenantId": "innovate",                                     // (String) Identificador único para la URL.
        "dbName": "db_innovate",                                    // (String) Nombre de la base de datos a crear.
        "dbUri": "jdbc:postgresql://localhost:5432/db_innovate",    // (String) URI de conexión.
        "dbUser": "user_innovate",                                  // (String) Usuario para la nueva BD.
        "dbPassword": "a_secure_password",                          // (String) Contraseña para la nueva BD.
        "dbTypeCode": 1                                             // (Integer) ID del tipo de BD (e.g., PostgreSQL).
      }
    }
    ```
-   **Response Body (201 Created):**
    ```json
    {
      "id": 3,                             // (Integer) ID único de la empresa.
      "name": "Innovate Solutions S.A.C.", // (String) Nombre de la empresa.
      "ruc": "20609876543",                // (String) RUC de la empresa.
      "email": "contacto@innovate.com",    // (String) Email de la empresa.
      "category": "Tecnología",            // (String) Nombre de la categoría.
      "state": "Activo",                   // (String) Estado actual de la empresa.
      "tenantId": "innovate",              // (String) Identificador del tenant.
      "recordStatus": "A",                 // (String) Estado del registro (A: Activo).
      "registerDate": 20231028             // (Integer) Fecha de registro en formato YYYYMMDD.
    }
    ```

### 2. Obtener Todas las Empresas
-   **Método HTTP:** `GET`
-   **URL:** `/api/admin/companies`
-   **Descripción:** Devuelve una lista de todas las empresas registradas.
-   **Response Body (200 OK):** `(Un array de objetos CompanyResponse, 
    como el del ejemplo anterior)`

### 3. Obtener Detalles de una Empresa
-   **Método HTTP:** `GET`
-   **URL:** `/api/admin/companies/{id}`
-   **Descripción:** Devuelve la información de una empresa específica.
-   **Response Body (200 OK):** `(Un objeto CompanyResponse, como el del 
    ejemplo anterior)`

### 4. Actualizar una Empresa
-   **Método HTTP:** `PUT`
-   **URL:** `/api/admin/companies/{id}`
-   **Descripción:** Actualiza los datos generales de una empresa.
-   **Request Body:** `(Objeto CompanyRequest, sin la sección "instance")`
-   **Response Body (200 OK):** `(Devuelve el objeto CompanyResponse 
    actualizado)`

### 5. Eliminar una Empresa (Lógicamente)
-   **Método HTTP:** `DELETE`
-   **URL:** `/api/admin/companies/{id}`
-   **Descripción:** Marca una empresa y su instancia como eliminadas.

-   **Response Body:** `(Vacío, con estado 204 No Content)`

---

## Endpoints del Usuario (Cliente Final)

### Vistas Públicas (No requieren autenticación)

#### 1. Listar Agencias (Empresas del Tenant)
-   **Método HTTP:** `GET`
-   **URL:** `/api/tenant/{tenantId}/public/agencies`
-   **Descripción:** Devuelve una lista paginada de todas las agencias 
    activas de un tenant.
-   **Response Body (200 OK):**
    ```json
    {
      "content": [
        {
          "id": "1",                           // (String) ID de la agencia.
          "name": "Agencia Principal Centro",  // (String) Nombre de la agencia.
          "shortName": "APC",                  // (String) Nombre corto autogenerado.
          "type": "Agencia",                   // (String) Tipo de entidad.
          "logo": null,                        // (String) URL del logo.
          "address": "Av. Central 123",        // (String) Dirección de la agencia.
          "schedule": null,                    // (String) Horario de atención.
          "phone": null,                       // (String) Teléfono de contacto.
          "isAvailable": true,                 // (Boolean) Indica si la agencia está activa.
          "activeQueues": 3,                   // (Integer) Número de colas activas.
          "queues": null                       // (Array) Lista de colas (solo en el endpoint de detalle).
        }
      ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": { "empty": false, "sorted": true, "unsorted": false },
        "offset": 0,
        "paged": true,
        "unpaged": false
      },
      "last": true,
      "totalPages": 1,
      "totalElements": 1,
      "size": 10,
      "number": 0,
      "sort": { "empty": false, "sorted": true, "unsorted": false },
      "first": true,
      "numberOfElements": 1,
      "empty": false
    }
    ```

#### 2. Obtener Detalles de una Agencia
-   **Método HTTP:** `GET`
-   **URL:** `/api/tenant/{tenantId}/public/agencies/{agencyId}`
-   **Descripción:** Obtiene los detalles de una agencia, incluyendo la 
    lista completa de sus colas activas.
-   **Response Body (200 OK):**
    ```json
    {
      "id": "1",
      "name": "Agencia Principal Centro",
      "shortName": "APC",
      "type": "Agencia",
      "logo": null,
      "address": "Av. Central 123",
      "schedule": null,
      "phone": null,
      "isAvailable": true,
      "activeQueues": 2,
      "queues": [
        {
          "id": "101",                         // (String) ID de la cola.
          "name": "Caja Rápida",               // (String) Nombre de la cola.
          "icon": null,                        // (String) Nombre del ícono.
          "peopleWaiting": 5,                  // (Integer) Personas en espera.
          "avgTime": "3 min",                  // (String) Tiempo promedio de atención.
          "enterpriseId": "1",                 // (String) ID de la agencia a la que pertenece.
          "isActive": true,                    // (Boolean) Si la cola está abierta.
          "currentTicket": "C-012",            // (String) Ticket que está siendo atendido.
          "waitTimePerPerson": "3 min",        // (String) Tiempo de espera estimado por persona.
          "enterprise": null                   // (Object) Detalles de la empresa (solo en endpoint de detalle de cola).
        }
      ]
    }
    ```

### Acciones Autenticadas (Requieren autenticación)

**Autorización:** Requiere un JWT con el rol `ROLE_CLIENT`.

#### 1. Unirse a una Cola
-   **Método HTTP:** `POST`
-   **URL:** `/api/tenant/{tenantId}/queues/{queueId}/join`
-   **Descripción:** Permite a un cliente autenticado unirse a una cola.
-   **Request Body:**
    ```json
    {
      "pushToken": "ExponentPushToken[xxxxxxxxxxxxxxxxx]" // (String) Token de Expo para notificaciones.
    }
    ```
-   **Response Body (201 Created):**
    ```json
    {
      "id": 50,                                      // (Long) ID del turno.
      "queueRegistration": {                         // (Object) Detalles del registro en la cola.
        "id": 75,
        "queue": { "id": 101, "name": "Caja Rápida" /* ... */ },
        "companyClient": { "id": 22, "fullName": "Juan Pérez" /* ... */ },
        "registrationDateTime": 1669333000000
      },
      "turnStatus": {                                // (Object) Estado actual del turno.
        "id": 1,
        "name": "EN_ESPERA"
      },
      "operator": null,                              // (Object) Operario que atiende (null al inicio).
      "orderNumber": 15,                             // (Integer) Número de orden en la cola.
      "generationDateTime": 1669333000000,           // (Long) Timestamp de creación del turno.
      "attentionDateTime": null,                     // (Long) Timestamp de atención (null al inicio).
      "recordStatus": "A"
    }
    ```

> **Nota:** La estructura exacta del `Turn` puede variar. Se creará un DTO 
> `TurnResponse` para aplanar esta estructura.

---

## Endpoints del Operario

**Autorización:** Requiere un JWT con el rol `ROLE_OPERATOR`.

### 1. Llamar al Siguiente Turno
-   **Método HTTP:** `POST`
-   **URL:** `/api/tenants/{tenantId}/operator/queues/{queueId}/call-next`
-   **Descripción:** Llama al siguiente cliente en espera en la cola 
    especificada.
-   **Autorización Adicional:** Requiere que el operario esté asignado a 
    la cola.
-   **Response Body (200 OK):** `(Devuelve el objeto Turn actualizado, 
    ahora con un estado 'LLAMANDO' y la información del operario)`

### 2. Marcar Turno como Atendido
-   **Método HTTP:** `POST`
-   **URL:** `/api/tenants/{tenantId}/operator/turns/{turnId}/complete`
-   **Descripción:** Finaliza un turno, cambiando su estado a 'ATENDIDO'.
-   **Response Body (200 OK):** `(Devuelve el objeto Turn actualizado con 
    el nuevo estado)`

### 3. Marcar Turno como Ausente
-   **Método HTTP:** `POST`
-   **URL:** `/api/tenants/{tenantId}/operator/turns/{turnId}/mark-as-absent`
-   **Descripción:** Cambia el estado de un turno a 'AUSENTE'.
-   **Response Body (200 OK):** `(Devuelve el objeto Turn actualizado con 
    el nuevo estado)`

---

## Endpoints de Administración del Tenant (En Desarrollo)

Los endpoints para los roles `Admin Empresa`, `Admin División` y `Admin 
Agencia` están en desarrollo y se documentarán una vez implementados
