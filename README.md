# SaborApp — Pollería El Buen Sabor 🍗
**Proyecto:** App Android para gestión de comandas y cuentas  
**Institución:** SENATI — Seminario de Complementación Práctica III (VI Semestre)  
**Rama:** `Sprint1`  

---
## 🎯 Objetivo del Sprint 1
> **App navegable:** Login validado, menú principal y pantallas del restaurante (sin datos persistidos).
---
## 📋 Historias de Usuario Implementadas
### 🔹 HU-01: Pantalla de inicio de sesión (2 pts)
* **Historia:** *Como mozo, quiero ingresar con usuario y contraseña, para que solo el personal autorizado use la app.*
* **Criterios de Aceptación:**
  * **CA1:** Si los campos están vacíos, al pulsar «Ingresar» se muestra un mensaje de error debajo de cada campo (`TextInputLayout.error`).
  * **CA2:** Con credenciales de prueba (`admin / 1234` o `mozo / 1234`), se abre el menú principal y el login se cierra (al presionar atrás no regresa al login).
  * **CA3:** Con credenciales incorrectas, se muestra el Toast: `«Credenciales incorrectas»`.
  * **CA4:** El campo de contraseña cuenta con alternador visual de visibilidad (icono de ojo).
### 🔹 HU-02: Menú principal y navegación (2 pts)
* **Historia:** *Como mozo, quiero un menú con Platos, Mesas, Pedidos, Reportes, para llegar rápido a cada función.*
* **Criterios de Aceptación:**
  * **CA1:** Tras iniciar sesión, se visualiza el saludo personalizado (`Hola, {usuario}`) y las 4 opciones principales más el botón «Salir».
  * **CA2:** Al tocar una opción se abre su respectiva pantalla y con atrás vuelve al menú.
  * **CA3:** Al tocar «Salir», se regresa al Login cerrando la pila de actividades.
  * **CA4:** **Control de Roles:** Si el usuario ingresa con rol `MOZO`, el módulo de **Reportes se oculta automáticamente** (exclusivo para `ADMIN`).
### 🔹 HU-03: Identidad visual del negocio (1 pto)
* **Historia:** *Como dueño del negocio, quiero que la app tenga el nombre, colores e ícono de mi empresa.*
* **Criterios de Aceptación:**
  * **CA1:** Nombre visible «SaborApp» e isotipo de la pollería.
  * **CA2:** Paleta cálida (naranja `#E64A19`, acento `#FF7043` y fondos `#F8F9FA`) en `colors.xml` y `themes.xml`.
  * **CA3:** 100% de los textos centralizados en `strings.xml` sin cadenas hardcodeadas.
---
## 🛠️ Stack Tecnológico
* **Lenguaje:** Kotlin
* **Diseño:** XML con ViewBinding y Material Components (3.x)
* **SDK:** Min SDK 24 (Android 7.0) / Target SDK 34+
