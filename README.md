# Alquiler Coches API

API REST desarrollada con Spring Boot para la gestión de coches de alquiler.

## Tecnologías

- Java 25 + Spring Boot 4.0..
- Spring Data JPA + Hibernate
- MySQL
- Lombok
- Bean Validation (jakarta.validation)

## Endpoints — `/api/coches`

| Método | Ruta            | Descripción                         | Respuesta éxito | Respuesta error |
|--------|-----------------|-------------------------------------|-----------------|-----------------|
| GET    | `/api/coches`   | Obtener todos los coches            | 200 OK          | —               |
| GET    | `/api/coches/{id}` | Obtener un coche por ID          | 200 OK          | 404 Not Found   |
| POST   | `/api/coches`   | Crear un nuevo coche                | 201 Created     | 400 Bad Request |
| PUT    | `/api/coches/{id}` | Actualizar un coche existente    | 200 OK          | 404 Not Found   |
| DELETE | `/api/coches/{id}` | Eliminar un coche por ID         | 204 No Content  | 404 Not Found   |

## Modelo — `Coche`

| Campo       | Tipo    | Restricciones              |
|-------------|---------|----------------------------|
| id          | Integer | Auto-generado              |
| marca       | String  | Obligatorio (not blank)    |
| modelo      | String  | Obligatorio (not blank)    |
| matricula   | String  | Obligatorio, único         |
| disponible  | boolean | —                          |

## Configuración

Ajusta la conexión a base de datos en `src/main/resources/application.properties` y ejecuta el script `script_bbdd.sql` para crear el esquema inicial.

## Arrancar la aplicación

```bash
./mvnw spring-boot:run
```
