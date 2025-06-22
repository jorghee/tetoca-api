![](./.github/tetoca_cover.png)

# :hammer: <samp>API REST TeToca SaaS</samp>

## Resumen endpoints (Admin SaaS)

Estos endpoints están diseñados para ser consumidos por el **Administrador 
de la Plataforma SaaS** y operan sobre la base de datos **Global**. Todos 
ellos se encuentran bajo la ruta base `/api/admin/companies` y requieren 
autenticación con el rol `SAAS_ADMIN`.

### 1. Registrar una Nueva Empresa

*   **Funcionalidad:** Este es el endpoint principal para dar de alta a un 
nuevo cliente (tenant) en la plataforma. Crea el registro de la `Company` y 
su `Instance` asociada en la base de datos global. De forma asíncrona, 
dispara el evento que provisiona una nueva base de datos aislada y su 
esquema para el nuevo tenant.

*   **Método HTTP:** `POST`
*   **URL Completa:** `http://localhost:8080/api/admin/companies`
*   **Cuerpo de la Solicitud (Request Body):**

    ```json
    {
      "company": {
        "name": "Tech Innovators S.A.C.",
        "ruc": "20601234567",
        "email": "contacto@techinnovators.com",
        "categoryCode": 1
      },
      "instance": {
        "tenantId": "techinnovators",
        "dbName": "db_techinnovators",
        "dbUri": "jdbc:postgresql://localhost:5432/db_techinnovators",
        "dbUser": "user_techinnovators",
        "dbPassword": "a_very_secure_password",
        "dbTypeCode": 1
      }
    }
    ```

*   **Respuesta de Ejemplo (Response `201 Created`):**

    ```json
    {
      "id": 1,
      "name": "Tech Innovators S.A.C.",
      "ruc": "20601234567",
      "email": "contacto@techinnovators.com",
      "category": "Tecnología",
      "state": "Activo",
      "tenantId": "techinnovators",
      "recordStatus": "A",
      "registerDate": 20231027
    }
    ```

---

### 2. Obtener Todas las Empresas

*   **Funcionalidad:** Devuelve una lista de todas las empresas registradas 
en la plataforma que no han sido eliminadas lógicamente. Es ideal para que 
el Administrador SaaS tenga un dashboard o una vista general de todos sus 
clientes.

*   **Método HTTP:** `GET`
*   **URL Completa:** `http://localhost:8080/api/admin/companies`
*   **Cuerpo de la Solicitud (Request Body):** `N/A`

*   **Respuesta de Ejemplo (Response `200 OK`):**

    ```json
    [
      {
        "id": 1,
        "name": "Tech Innovators S.A.C.",
        "ruc": "20601234567",
        "email": "contacto@techinnovators.com",
        "category": "Tecnología",
        "state": "Activo",
        "tenantId": "techinnovators",
        "recordStatus": "A",
        "registerDate": 20231027
      },
      {
        "id": 2,
        "name": "Soluciones Logísticas S.A.",
        "ruc": "20509876543",
        "email": "ventas@solucioneslogisticas.com",
        "category": "Transporte",
        "state": "Activo",
        "tenantId": "solog",
        "recordStatus": "A",
        "registerDate": 20231026
      }
    ]
    ```

---

### 3. Obtener una Empresa por su ID

*   **Funcionalidad:** Recupera la información detallada de una única 
empresa, identificada por su ID numérico. Útil para ver los detalles de 
un cliente específico.

*   **Método HTTP:** `GET`
*   **URL Completa:** `http://localhost:8080/api/admin/companies/1`
*   **Cuerpo de la Solicitud (Request Body):** `N/A`

*   **Respuesta de Ejemplo (Response `200 OK`):**

    ```json
    {
      "id": 1,
      "name": "Tech Innovators S.A.C.",
      "ruc": "20601234567",
      "email": "contacto@techinnovators.com",
      "category": "Tecnología",
      "state": "Activo",
      "tenantId": "techinnovators",
      "recordStatus": "A",
      "registerDate": 20231027
    }
    ```

---

### 4. Actualizar una Empresa Existente

*   **Funcionalidad:** Permite modificar los datos generales de una 
empresa (nombre, RUC, email, categoría). No permite cambiar los datos de 
la instancia (como el `tenantId` o las credenciales de la base de datos), 
ya que estas son operaciones más sensibles que podrían requerir un flujo 
diferente.

*   **Método HTTP:** `PUT`
*   **URL Completa:** `http://localhost:8080/api/admin/companies/1`
*   **Cuerpo de la Solicitud (Request Body):**

    ```json
    {
      "name": "Tech Innovators Global",
      "ruc": "20601234567",
      "email": "ceo@techinnovators.com",
      "categoryCode": 2
    }
    ```

*   **Respuesta de Ejemplo (Response `200 OK`):**

    ```json
    {
      "id": 1,
      "name": "Tech Innovators Global",
      "ruc": "20601234567",
      "email": "ceo@techinnovators.com",
      "category": "Consultoría",
      "state": "Activo",
      "tenantId": "techinnovators",
      "recordStatus": "A",
      "registerDate": 20231027
    }
    ```

---

### 5. Eliminar una Empresa (Lógicamente)

*   **Funcionalidad:** Realiza una **eliminación lógica** de la empresa. En 
lugar de borrar el registro de la base de datos, cambia su `recordStatus` 
a `'*'` (Eliminado). Esto desactiva la empresa en la plataforma pero 
mantiene los datos para fines de auditoría, métricas o una posible 
reactivación futura.

*   **Método HTTP:** `DELETE`
*   **URL Completa:** `http://localhost:8080/api/admin/companies/1`
*   **Cuerpo de la Solicitud (Request Body):** `N/A`

*   **Respuesta de Ejemplo (Response `204 No Content`):**
    *El servidor responde con un código de estado `204` y un cuerpo de 
    respuesta vacío, indicando que la operación se realizó con éxito.*

---

## Resumen endpoints (Cliente SaaS)

## Resumen endpoints (Admin Empresa)

## Resumen endpoints (Admin División)

## Resumen endpoints (Admin Agencia)

## Resumen endpoints (Operadores)


---

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.0/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.0/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.0/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.0/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.5.0/reference/using/devtools.html)
* [Spring Security](https://docs.spring.io/spring-boot/3.5.0/reference/web/spring-security.html)
* [OAuth2 Client](https://docs.spring.io/spring-boot/3.5.0/reference/web/spring-security.html#web.security.oauth2.client)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
