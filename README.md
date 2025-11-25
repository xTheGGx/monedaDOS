
# monedaDOS

<p align="center">
  <img src="./server/src/main/resources/static/images/image_logo.png" alt="monedaDOS Logo" width="100" />
</p>

<h3 align="center">Control total de tus finanzas personales</h3>

<p align="center">
  Rastrea ingresos y gastos sin esfuerzo. Obtén control total de tus finanzas y toma mejores decisiones para tu futuro financiero.
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-17-blue?logo=java&logoColor=white" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?logo=spring&logoColor=white" />
  <img alt="MariaDB" src="https://img.shields.io/badge/MariaDB-10.4-blue?logo=mariadb&logoColor=white" />
  <img alt="Security" src="https://img.shields.io/badge/Spring%20Security-6-green?logo=springsecurity&logoColor=white" />
  <img alt="Maven" src="https://img.shields.io/badge/Maven-blue?logo=apachemaven&logoColor=white" />
  <img alt="Vue.js" src="https://img.shields.io/badge/Vue.js-3-42b883?logo=vuedotjs&logoColor=white" />
  <img alt="Vite" src="https://img.shields.io/badge/Vite-5-646CFF?logo=vite&logoColor=white" />
</p>

<p align="center">
  · <a href="#acerca-del-proyecto">Acerca del proyecto</a>
  · <a href="#versiones-del-proyecto">Versiones del proyecto</a>
  · <a href="#stack-tecnologico">Stack tecnologico</a>
  · <a href="#caracteristicas">Caracteristicas</a>
  · <a href="#estructura-de-la-base-de-datos">Estructura de la base de datos</a>
  · <a href="#primeros-pasos">Primeros pasos</a>
  · <a href="#manual-de-usuario">Manual de usuario</a>
  · <a href="#hoja-de-ruta">Hoja de ruta</a>
  · <a href="#troubleshooting">Troubleshooting</a>
</p>

---

## Acerca del proyecto

**monedaDOS** es una aplicación para la gestión de finanzas personales.

La versión actual del repositorio está organizada como **monorepo**:

- Un **backend** en `server/` expuesto como **API REST** con Spring Boot.
- Un **frontend** en `client/` construido con **Vue 3 + Vite**, que consume la API y se encarga de la experiencia de usuario.

El objetivo es poder:

- Registrar usuarios.
- Gestionar cuentas (débito, crédito, efectivo).
- Registrar transacciones de ingreso/egreso.
- Visualizar saldos y reportes a partir de la base de datos relacional.

---

## Versiones del proyecto

Este repositorio tiene dos etapas claras:

- `DGTIC-deployment`  
  Versión entregada en el **Diplomado de Java**.  
  Arquitectura monolítica: **Spring Boot + Thymeleaf** (render del front en el servidor).

- `main` (y ramas de refactor)  
  Versión en evolución, donde el proyecto se separa en:
  - `server/` → API REST (Spring Boot).
  - `client/` → SPA con Vue 3.

La idea es mantener `DGTIC-deployment` como referencia histórica de la entrega académica, y seguir creciendo el proyecto sobre `main`.

---

## Stack tecnologico

### Backend (`/server`)

- **Lenguaje:** Java 17  
- **Framework:** Spring Boot 3.5.6  
- **Acceso a datos:** Spring Data JPA (Hibernate)  
- **Base de datos:** MariaDB (compatible con MySQL)  
- **Seguridad:** Spring Security 6  
- **Autenticación / Autorización:**  
  - Autenticación de usuarios y roles en BD.  
  - Endpoints REST protegidos (en evolución).  
- **Arquitectura:**  
  - Capas: controlador → servicio → repositorio.  
  - Entidades mapeadas con JPA.  
- **Gestión de dependencias:** Maven  
- **Utilitarios:** Lombok

### Frontend (`/client`)

- **Framework:** Vue 3  
- **Build tool / dev server:** Vite  
- **Enrutamiento:** Vue Router  
- **Comunicación con la API:** módulo de servicios en `src/services/api.js`  
- **Estilos:** CSS modularizado en `src/assets/css`  
- **Vistas actuales:**  
  - `Login.vue` → pantalla de autenticación.  
  - `Cuentas.vue` → vista para listar/trabajar con cuentas.

---

## Caracteristicas

### Backend

- **Modelo de dominio financiero:**
  - Usuarios
  - Cuentas (Efectivo, Débito, Crédito)
  - (En roadmap) Transacciones, Categorías, Reportes
- **Validaciones de negocio:**
  - Validación de tipo de cuenta.
  - Reglas específicas para tarjetas de crédito (días de corte y pago).
- **Lógica en BD:**
  - Scripts SQL con tablas, vistas y triggers para mantener saldos actualizados.
- **Manejo de errores:**
  - Controladores y handlers para respuestas consistentes desde la API.

### Frontend

- **Autenticación (en progreso):**
  - Formulario de login.
  - Consumo de la API para iniciar sesión (según configuración actual).
- **Gestión de cuentas (en progreso):**
  - Vista para visualizar cuentas existentes.
  - En futuras iteraciones: crear, editar y eliminar cuentas desde el frontend, persistiendo cambios mediante la API.
- **Diseño:**
  - Estilos personalizados en `styles.css` y `cuentas.css`.
  - Uso de assets propios (`/public/images`) para mantener la identidad visual de monedaDOS.

---

## Estructura de la base de datos

Diagrama Entidad–Relación (DER):

<p align="center">
  <img src="./server/src/main/resources/sql/sfp_der.png" alt="Diagrama Entidad-Relación de monedaDOS" />
</p>

El script principal de creación de esquema y datos iniciales se encuentra en:

```text
./server/src/main/resources/sql/sfp.sql
````

---

## Primeros pasos

### Prerrequisitos

**Backend**

* Java **17+**
* Apache Maven **3.6+**
* MariaDB **10.4+** (o MySQL **8**)

**Frontend**

* Node.js **20+** (recomendado)
* npm o pnpm (según prefieras; por defecto npm)

---

### 1) Clonar el repositorio

```bash
git clone https://github.com/xTheGGx/monedaDOS.git
cd monedaDOS
```

---

### 2) Configurar la base de datos (backend)

Crea la base de datos `sfp` y ejecuta el script:

```sql
CREATE DATABASE sfp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Luego ejecuta el contenido de:

```text
./server/src/main/resources/sql/sfp.sql
```

> El script crea tablas, vistas, triggers y datos iniciales (roles, categorías, etc.).

---

### 3) Configurar la aplicación backend

Edita `./server/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/sfp?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=123
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
```

> Ajusta `username` y `password` a tu entorno local.

---

### 4) Levantar el backend

Desde la carpeta `server/`:

**macOS/Linux**

```bash
cd server
./mvnw spring-boot:run
```

**Windows**

```bash
cd server
.\mvnw.cmd spring-boot:run
```

Por defecto, la API quedará expuesta en:
[http://localhost:8080](http://localhost:8080)

---

### 5) Levantar el frontend

En otra terminal, desde la raíz del proyecto:

```bash
cd client
npm install
npm run dev
```

Por defecto, Vite levanta la app en algo como:
[http://localhost:5173](http://localhost:5173)

La aplicación Vue se comunicará con la API de Spring según la configuración de `client/src/services/api.js`.

---

## Manual de usuario

> ⚠️ **Nota:** Esta sección está en transición. La lógica funcional (usuarios, cuentas, etc.) ya existe en el backend; el frontend en Vue se encuentra en fase de integración.

### Flujo general previsto

1. **Inicio de sesión**

  * Acceder a la pantalla de login (Vue).
  * Introducir correo y contraseña registrados en la base de datos.
  * El frontend envía las credenciales a la API y, si son correctas, redirige a la vista principal.

2. **Panel / Home**

  * Mostrar un resumen de cuentas y, próximamente, transacciones y saldos.

3. **Gestión de cuentas**

  * Desde la vista de Cuentas (`Cuentas.vue`), listar las cuentas del usuario autenticado.
  * En futuras iteraciones: crear, editar y eliminar cuentas desde el frontend, persistiendo cambios mediante la API.

4. **Transacciones y reportes**

  * En el roadmap: formularios para registrar ingresos/gastos y vistas de reportes.

---

## Hoja de ruta

* [ ] Conectar completamente el login del frontend con la API REST.
* [ ] Exponer CRUD completo de Cuentas en `/api/cuentas` y consumirlo desde `Cuentas.vue`.
* [ ] Implementar CRUD de Transacciones en la API y vistas en el frontend.
* [ ] Añadir reportes dinámicos (gráficas y resúmenes) basados en la información de BD.
* [ ] Mejorar manejo de errores y mensajes de feedback en el frontend.
* [ ] Documentar los endpoints REST (OpenAPI/Swagger).

---

## Troubleshooting

**1) “Error: Rol 'USER' no encontrado.”**

Asegúrate de que el script `sfp.sql` insertó los roles. Si no, inserta uno manualmente:

```sql
INSERT INTO ROL (id, nombre) VALUES (1, 'USER');
```

**2) Problemas de conexión a la base de datos**

* Verifica la URL, usuario y contraseña en `application.properties`.
* Revisa que el servicio de MariaDB/MySQL esté levantado.
* Confirma que la base de datos `sfp` existe.

**3) El frontend no puede llamar a la API**

* Asegúrate de que el backend esté corriendo en `http://localhost:8080`.
* Verifica la URL base configurada en `client/src/services/api.js`.
* Si usas CORS, revisa que el backend permita llamadas desde el puerto de Vite (por ejemplo `http://localhost:5173`).

---

> Cualquier feedback o issue es bienvenido. Este proyecto sigue evolucionando como laboratorio personal de arquitectura cliente/servidor y buenas prácticas en finanzas personales.

