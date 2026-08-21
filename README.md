# 🚀 Gestión de Ventas API

API REST para la gestión de ventas, clientes, productos, categorías y pedidos. Desarrollada con **Spring Boot** y **Java 21**, implementa operaciones CRUD completas, manejo de relaciones entre entidades, reglas de negocio, reportes mediante consultas a la base de datos y consultas dinámicas mediante **Spring Data JPA Specifications**.

---

## 📋 Tabla de Contenidos

- [Tecnologías](#-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración del Proyecto](#-configuración-del-proyecto)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Base de Datos](#-base-de-datos)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Reglas de Negocio](#-reglas-de-negocio)
- [Reportes Disponibles](#-reportes-disponibles)
- [Ejemplos de Uso](#-ejemplos-de-uso)
- [Pruebas con Postman](#-pruebas-con-postman)
- [Manejo de Errores](#-manejo-de-errores)
- [Autores](#-autores)
- [Licencia](#-licencia)

---

## 🛠️ Tecnologías

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| **Java** | 21 | Lenguaje de programación principal |
| **Spring Boot** | 3.2.8 | Framework principal para el backend |
| **Spring Data JPA** | - | Persistencia y acceso a datos |
| **Hibernate** | - | ORM (Object-Relational Mapping) |
| **SQL Server** | 2019+ | Base de datos relacional |
| **Maven** | 3.9+ | Gestión de dependencias y construcción |
| **Lombok** | 1.18.30 | Reducción de código repetitivo |
| **Spring Data JPA Specifications** | - | Consultas dinámicas y filtros |
| **Postman** | - | Pruebas de APIs REST |

---

## 📦 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21) o superior
- [Maven 3.9+](https://maven.apache.org/download.cgi)
- [SQL Server 2019+](https://www.microsoft.com/es-es/sql-server/sql-server-downloads) o [SQL Server Express](https://www.microsoft.com/es-es/sql-server/sql-server-downloads)
- [Postman](https://www.postman.com/downloads/) (para pruebas)
- [Git](https://git-scm.com/downloads) (opcional)

---

## ⚙️ Configuración del Proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/E-TorresC/gestion-ventas-api.git
cd gestion-ventas-api
````

### 2. Configurar la base de datos

Ejecuta el siguiente script en SQL Server Management Studio o Azure Data Studio:

sql

```
-- Crear la base de datos
CREATE DATABASE gestion_ventas_db;
GO

USE gestion_ventas_db;
GO

-- Crear tablas (el script completo está en la documentación)
-- O puedes dejar que Hibernate las cree automáticamente
```

### 3. Configurar las credenciales

Edita el archivo `src/main/resources/application-dev.properties`:

properties

```
# Configuración de conexión a SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=gestion_ventas_db;encrypt=true;trustServerCertificate=true
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# Configuración de JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Ejecutar el proyecto

bash

```
# Con Maven
mvn clean spring-boot:run

# O usando el wrapper de Maven (Linux/Mac)
./mvnw spring-boot:run

# O usando el wrapper de Maven (Windows)
mvnw.cmd spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

---

## 📁 Estructura del Proyecto

text

```
gestion-ventas-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── etctech/
│   │   │       └── gestionventas/
│   │   │           ├── controller/          # Controladores REST
│   │   │           │   ├── CategoriaController.java
│   │   │           │   ├── ClienteController.java
│   │   │           │   ├── ProductoController.java
│   │   │           │   ├── PedidoController.java
│   │   │           │   └── ReporteController.java
│   │   │           ├── service/             # Lógica de negocio
│   │   │           │   ├── CategoriaService.java
│   │   │           │   ├── ClienteService.java
│   │   │           │   ├── ProductoService.java
│   │   │           │   ├── PedidoService.java
│   │   │           │   ├── ReporteService.java
│   │   │           │   └── impl/            # Implementaciones
│   │   │           ├── repository/          # Repositorios JPA
│   │   │           │   ├── CategoriaRepository.java
│   │   │           │   ├── ClienteRepository.java
│   │   │           │   ├── ProductoRepository.java
│   │   │           │   ├── PedidoRepository.java
│   │   │           │   ├── DetallePedidoRepository.java
│   │   │           │   └── ReporteRepository.java
│   │   │           ├── entity/              # Entidades JPA
│   │   │           │   ├── Categoria.java
│   │   │           │   ├── Cliente.java
│   │   │           │   ├── Producto.java
│   │   │           │   ├── Pedido.java
│   │   │           │   └── DetallePedido.java
│   │   │           ├── dto/                 # Data Transfer Objects
│   │   │           │   ├── request/         # DTOs de entrada
│   │   │           │   ├── response/        # DTOs de salida
│   │   │           │   └── reporte/         # DTOs para reportes
│   │   │           ├── exception/           # Manejo de excepciones
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   ├── ResourceNotFoundException.java
│   │   │           │   ├── BusinessException.java
│   │   │           │   ├── InsufficientStockException.java
│   │   │           │   ├── InactiveProductException.java
│   │   │           │   └── InvalidStateTransitionException.java
│   │   │           ├── specification/       # Consultas dinámicas
│   │   │           │   ├── ProductoSpecification.java
│   │   │           │   ├── PedidoSpecification.java
│   │   │           │   └── ClienteSpecification.java
│   │   │           ├── config/              # Configuraciones
│   │   │           │   ├── WebConfig.java
│   │   │           │   └── TransactionConfig.java
│   │   │           ├── util/                # Utilidades
│   │   │           │   ├── Constants.java
│   │   │           │   └── EstadoPedidoValidator.java
│   │   │           └── GestionVentasApiApplication.java  # Clase principal
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/                                # Pruebas unitarias
├── pom.xml                                  # Configuración de Maven
└── README.md                                # Este archivo
```

---

## 🗄️ Base de Datos

### Diagrama de Entidades

text

```
┌──────────────┐          ┌──────────────┐
│   CLIENTE    │          │  CATEGORIA   │
│──────────────│          │──────────────│
│ id_cliente   │          │ id_categoria │
│ nombres      │          │ nombre       │
│ apellidos    │          │ estado       │
│ email        │          └──────────────┘
│ telefono     │                │
│ estado       │                │ 1
│ fecha_registro│               │
└──────────────┘                │
       │ 1                      │
       │                        │
       │ N                      │ N
┌──────────────┐          ┌──────────────┐
│   PEDIDO     │          │   PRODUCTO   │
│──────────────│          │──────────────│
│ id_pedido    │◄─────────│ id_producto  │
│ id_cliente   │   N:1    │ nombre       │
│ fecha_pedido │          │ precio       │
│ total        │          │ stock        │
│ estado       │          │ estado       │
└──────────────┘          │ id_categoria │
       │ 1                 └──────────────┘
       │                        │
       │                        │
       │ N                      │ 1
┌──────────────┐                │
│DETALLE_PEDIDO│                │
│──────────────│                │
│id_detalle    │                │
│id_pedido     │◄───────────────┘
│id_producto   │   N:1
│cantidad      │
│precio_unitario│
│subtotal      │
└──────────────┘
```

### Estados del Pedido

| **EstadoDescripción** |                                        |
| --------------------- | -------------------------------------- |
| `PENDIENTE`           | Pedido creado, esperando procesamiento |
| `CONFIRMADO`          | Pedido confirmado, stock descontado    |
| `ENVIADO`             | Pedido enviado al cliente              |
| `ENTREGADO`           | Pedido entregado al cliente            |
| `ANULADO`             | Pedido anulado, stock devuelto         |

### Transiciones de Estados

text

```
PENDIENTE → CONFIRMADO → ENVIADO → ENTREGADO
PENDIENTE → ANULADO
CONFIRMADO → ANULADO
```

---

## 🔗 Endpoints de la API

### Base URL

text

```
http://localhost:8080/api
```

### 1. Categorías

| **MétodoEndpointDescripción** |                              |                                      |
| ----------------------------- | ---------------------------- | ------------------------------------ |
| `POST`                        | `/categorias`                | Crear una nueva categoría            |
| `GET`                         | `/categorias`                | Listar categorías activas (paginado) |
| `GET`                         | `/categorias/{id}`           | Obtener categoría por ID             |
| `GET`                         | `/categorias/buscar?nombre=` | Buscar categorías por nombre         |
| `GET`                         | `/categorias/todos`          | Listar todas las categorías activas  |
| `PUT`                         | `/categorias/{id}`           | Actualizar categoría                 |
| `DELETE`                      | `/categorias/{id}`           | Eliminar lógicamente categoría       |

### 2. Clientes

| **MétodoEndpointDescripción** |                            |                                     |
| ----------------------------- | -------------------------- | ----------------------------------- |
| `POST`                        | `/clientes`                | Crear un nuevo cliente              |
| `GET`                         | `/clientes`                | Listar clientes activos (paginado)  |
| `GET`                         | `/clientes/{id}`           | Obtener cliente por ID              |
| `GET`                         | `/clientes/buscar?search=` | Buscar clientes por nombre/apellido |
| `GET`                         | `/clientes/top-compras`    | Clientes con mayor monto de compra  |
| `PUT`                         | `/clientes/{id}`           | Actualizar cliente                  |
| `DELETE`                      | `/clientes/{id}`           | Eliminar lógicamente cliente        |

### 3. Productos

| **MétodoEndpointDescripción** |                                      |                                     |
| ----------------------------- | ------------------------------------ | ----------------------------------- |
| `POST`                        | `/productos`                         | Crear un nuevo producto             |
| `GET`                         | `/productos`                         | Listar productos activos (paginado) |
| `GET`                         | `/productos/{id}`                    | Obtener producto por ID             |
| `POST`                        | `/productos/buscar`                  | Buscar con filtros dinámicos        |
| `GET`                         | `/productos/buscar/nombre?nombre=`   | Buscar por nombre                   |
| `GET`                         | `/productos/categoria/{idCategoria}` | Buscar por categoría                |
| `GET`                         | `/productos/stock-bajo?limite=`      | Productos con stock bajo            |
| `PUT`                         | `/productos/{id}`                    | Actualizar producto                 |
| `DELETE`                      | `/productos/{id}`                    | Eliminar lógicamente producto       |

### 4. Pedidos

| **MétodoEndpointDescripción** |                                |                              |
| ----------------------------- | ------------------------------ | ---------------------------- |
| `POST`                        | `/pedidos`                     | Registrar un nuevo pedido    |
| `GET`                         | `/pedidos`                     | Listar pedidos (paginado)    |
| `GET`                         | `/pedidos/{id}`                | Obtener pedido por ID        |
| `POST`                        | `/pedidos/buscar`              | Buscar con filtros dinámicos |
| `GET`                         | `/pedidos/cliente/{idCliente}` | Listar pedidos de un cliente |
| `GET`                         | `/pedidos/rango-fechas`        | Pedidos por rango de fechas  |
| `PATCH`                       | `/pedidos/{id}/estado?estado=` | Cambiar estado del pedido    |
| `DELETE`                      | `/pedidos/{id}`                | Anular pedido                |

### 5. Reportes

| **MétodoEndpointDescripción** |                                   |                                    |
| ----------------------------- | --------------------------------- | ---------------------------------- |
| `GET`                         | `/reportes/top-productos`         | Top 5 productos más vendidos       |
| `GET`                         | `/reportes/ventas-por-mes`        | Total de ventas por mes            |
| `GET`                         | `/reportes/ventas-por-mes?anio=`  | Ventas por mes (año específico)    |
| `GET`                         | `/reportes/clientes-top?limite=`  | Clientes con mayor monto de compra |
| `GET`                         | `/reportes/stock-bajo?limite=`    | Productos con stock bajo           |
| `GET`                         | `/reportes/pedidos-fecha`         | Pedidos por rango de fechas        |
| `GET`                         | `/reportes/pedidos-fecha/resumen` | Resumen de pedidos                 |

---

## 📋 Reglas de Negocio

### RN-01: Stock suficiente

No se podrá registrar un pedido cuando la cantidad solicitada de un producto sea superior al stock disponible.

### RN-02: Descuento de stock

Al registrar correctamente un pedido, el stock de cada producto deberá disminuir según la cantidad vendida.

### RN-03: Devolución de stock

Al anular un pedido, se deberá devolver al stock la cantidad de productos asociada al pedido.

### RN-04: Productos inactivos

No se podrán registrar ventas de productos cuyo estado sea inactivo.

### RN-05: Listados activos

Los listados normales deberán mostrar únicamente registros activos.

### RN-06: Consistencia de la operación

El registro y la anulación de pedidos deberán ejecutarse mediante transacciones para mantener la consistencia de los datos.

---

## 📊 Reportes Disponibles

### REP-01: Top 5 productos más vendidos

http

```
GET /api/reportes/top-productos
```

**Respuesta:**

json

```
{
    "data": [
        {
            "idProducto": 1,
            "nombreProducto": "Laptop Dell XPS 13",
            "nombreCategoria": "Electrónicos",
            "totalUnidadesVendidas": 25,
            "totalVentas": 32499.75,
            "stockActual": 25
        }
    ]
}
```

### REP-02: Total de ventas por mes

http

```
GET /api/reportes/ventas-por-mes
GET /api/reportes/ventas-por-mes?anio=2026
```

### REP-03: Clientes con mayor monto de compra

http

```
GET /api/reportes/clientes-top?limite=10
```

### REP-04: Productos con stock bajo

http

```
GET /api/reportes/stock-bajo?limite=10
```

### REP-05: Pedidos por rango de fechas

http

```
GET /api/reportes/pedidos-fecha?fechaInicio=2026-01-01T00:00:00&fechaFin=2026-12-31T23:59:59&limite=20
```

---

## 💡 Ejemplos de Uso

### 1. Crear una Categoría

http

```
POST /api/categorias
Content-Type: application/json

{
    "nombre": "Electrónicos"
}
```

**Respuesta:**

json

```
{
    "timestamp": "2026-08-20T10:30:00",
    "status": 201,
    "message": "Recurso creado exitosamente",
    "data": {
        "idCategoria": 1,
        "nombre": "Electrónicos",
        "estado": true
    }
}
```

### 2. Crear un Producto

http

```
POST /api/productos
Content-Type: application/json

{
    "nombre": "Laptop Dell XPS 13",
    "precio": 1299.99,
    "stock": 50,
    "idCategoria": 1
}
```

### 3. Registrar un Pedido

http

```
POST /api/pedidos
Content-Type: application/json

{
    "idCliente": 1,
    "detalles": [
        {
            "idProducto": 1,
            "cantidad": 2
        },
        {
            "idProducto": 2,
            "cantidad": 1
        }
    ]
}
```

### 4. Buscar Productos con Filtros Dinámicos

http

```
POST /api/productos/buscar?page=0&size=10&sort=precio,desc
Content-Type: application/json

{
    "nombre": "Laptop",
    "precioMinimo": 500.00,
    "precioMaximo": 2000.00,
    "stockMinimo": 10,
    "idCategoria": 1
}
```

### 5. Anular un Pedido

http

```
DELETE /api/pedidos/1
```

**Respuesta:** `204 No Content`

---

## 🧪 Pruebas con Postman

### Importar la Colección

1. Abre Postman
2. Haz clic en **Import** > **Import from File**
3. Selecciona el archivo `gestion-ventas-api.postman_collection.json`
4. Las variables de entorno se configurarán automáticamente

### Variables de Entorno

| **VariableValorDescripción** |                             |                            |
| ---------------------------- | --------------------------- | -------------------------- |
| `baseUrl`                    | `http://localhost:8080/api` | URL base de la API         |
| `categoriaId`                | `1`                         | ID de categoría de ejemplo |
| `clienteId`                  | `1`                         | ID de cliente de ejemplo   |
| `productoId`                 | `1`                         | ID de producto de ejemplo  |
| `pedidoId`                   | `1`                         | ID de pedido de ejemplo    |

### Ejecutar Pruebas

1. Selecciona la colección en Postman
2. Haz clic en **Run**
3. Selecciona el entorno de pruebas
4. Haz clic en **Run Gestion Ventas API**

---

## ❌ Manejo de Errores

### Estructura de Respuesta de Error

json

```
{
    "timestamp": "2026-08-20T10:30:00",
    "status": 400,
    "error": "Business Rule Violation",
    "message": "Stock insuficiente para el producto 'Laptop' (ID: 1). Stock disponible: 5, Cantidad solicitada: 10",
    "path": "/api/pedidos",
    "errorCode": "INSUFFICIENT_STOCK"
}
```

### Códigos de Error HTTP

| **CódigoDescripciónCuándo ocurre** |                       |                                        |
| ---------------------------------- | --------------------- | -------------------------------------- |
| `200`                              | OK                    | Operación exitosa                      |
| `201`                              | Created               | Recurso creado exitosamente            |
| `204`                              | No Content            | Recurso eliminado                      |
| `400`                              | Bad Request           | Error de validación o regla de negocio |
| `404`                              | Not Found             | Recurso no encontrado                  |
| `409`                              | Conflict              | Violación de integridad de datos       |
| `500`                              | Internal Server Error | Error inesperado                       |

### Excepciones Personalizadas

| **ExcepciónCódigoDescripción**    |     |                               |
| --------------------------------- | --- | ----------------------------- |
| `ResourceNotFoundException`       | 404 | Recurso no encontrado         |
| `BusinessException`               | 400 | Violación de regla de negocio |
| `InsufficientStockException`      | 400 | Stock insuficiente            |
| `InactiveProductException`        | 400 | Producto inactivo             |
| `InvalidStateTransitionException` | 400 | Transición de estado inválida |

---

## 👥 Autores

- **Erick Torres** - *Desarrollo inicial* - [ETC Tech](https://github.com/tu-usuario)

---

## 📄 Licencia

Este proyecto está bajo la Licencia Apache 2.0 - ver el archivo [LICENSE](https://license/) para más detalles.

---

## 🙏 Agradecimientos

- Spring Boot por su excelente framework
- La comunidad de desarrolladores por su soporte

---

## 📞 Contacto

Para cualquier consulta o sugerencia, por favor contacta a:

- **Email**: etorresca8\@gmail.com
- **GitHub**: [https://github.com/E-TorresC](https://github.com/E-TorresC)

---

**¡Gracias por usar Gestión de Ventas API!** 🚀

text

````
---
