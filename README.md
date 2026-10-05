# SaborApp — Pollería El Buen Sabor 🍗
**Proyecto:** App Android para gestión de comandas y cuentas  
**Institución:** SENATI — Seminario de Complementación Práctica III (VI Semestre)  
**Rama:** `Sprint2`  
---
## 🎯 Objetivo del Sprint 2
> **Catálogo y Persistencia:** Platos y mesas se registran y listan desde base de datos en XAMPP (MySQL); login real con autenticación de usuarios.
---
## 📋 Historias de Usuario Implementadas
### 🔹 HU-04: Base de datos y login con Backend (3 pts)
* **Historia:** *Como administrador, quiero que los usuarios se guarden y validen en la base de datos, para no depender de credenciales escritas en el código.*
* **Criterios de Aceptación:**
  * **CA1:** Conexión con base de datos `saborapp` (MySQL en XAMPP) y tabla `usuario`.
  * **CA2:** Validación mediante consulta parametrizada con PDO para evitar inyecciones SQL.
  * **CA3:** Respuestas JSON vía Retrofit 2 procesando roles (`ADMIN` o `MOZO`).
### 🔹 HU-05: Registrar y listar platos (5 pts)
* **Historia:** *Como administrador, quiero registrar platos con nombre, categoría, precio y disponibilidad, para tener la carta del restaurante en la app.*
* **Criterios de Aceptación:**
  * **CA1:** Validación de campos obligatorios en el formulario.
  * **CA2:** Regla de negocio: Precio estrictamente mayor a 0 (`CHECK (precio > 0)`). Si es $\le 0$, muestra `«Precio inválido»`.
  * **CA3:** Selector de categorías mediante Spinner (`Fondos`, `Entradas`, `Bebidas`, `Postres`).
  * **CA4:** Listado en `RecyclerView` con badge visual de disponibilidad (`Disponible` / `Agotado`).
### 🔹 HU-06: Registrar y listar mesas (3 pts)
* **Historia:** *Como administrador, quiero registrar las mesas con su número y capacidad, para asignar los pedidos a cada mesa.*
* **Criterios de Aceptación:**
  * **CA1:** Restricción de unicidad: si se repite el número de mesa, responde `«La mesa ya existe»`.
  * **CA2:** Regla de negocio: Capacidad entre 1 y 12 comensales. Si no, muestra `«Capacidad inválida»`.
  * **CA3:** Toda mesa nueva se inicializa con estado `LIBRE`.
  * **CA4:** Cuadrícula de mesas con `GridLayoutManager` de 3 columnas diferenciadas por color.
---
## 🗄️ Backend y Base de Datos (XAMPP)
* **Script SQL:** `backend/saborapp.sql`
* **Endpoints:**
  * `POST backend/login.php`
  * `GET` / `POST backend/platos.php`
  * `GET` / `POST backend/mesas.php`
