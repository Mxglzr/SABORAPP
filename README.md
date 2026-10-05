# 🍗 SaborApp — Pollería El Buen Sabor
> **Sistema Móvil Nativo Android para la Gestión de Comandas, Control de Mesas y Facturación**

[![Kotlin](https://img.shields.io/badge/Kotlin-Native-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![XAMPP](https://img.shields.io/badge/Backend-XAMPP%20(PHP%20%2B%20MySQL)-FB7A24?logo=xampp&logoColor=white)](https://www.apachefriends.org/)
[![Scrum](https://img.shields.io/badge/Metodología-Scrum%20(4%20Sprints)-blue)](https://scrumguides.org/)

---

## 📌 1. Ficha del Proyecto y Datos Académicos

| Campo | Detalle |
| :--- | :--- |
| **Institución** | **SENATI** |
| **Carrera Profesional** | Desarrollo de Software |
| **Semestre** | Sexto Semestre |
| **Curso** | Seminario de Complementación Práctica III |
| **Proyecto** | P1 · SaborApp (Pollería El Buen Sabor) |
| **Fecha** | Octubre 2026 |

---

## 💡 2. Contexto del Negocio y Problemática

### 🏢 Situación Actual (El Problema)
En la **Pollería El Buen Sabor**, la atención diaria presenta cuellos de botella operativos:
* Las comandas se anotan en papel, provocando demoras, pérdidas de pedidos o letra ilegible en la cocina.
* En horas punta, los mozos suman las cuentas manualmente, generando errores en los cobros y quejas de comensales.
* El administrador/dueño no cuenta con estadísticas claras sobre la venta diaria ni sobre cuáles son los platos con mayor rotación.

### 📱 Solución Propuesta (SaborApp)
**SaborApp** es una solución móvil cliente-servidor desarrollada para agilizar el flujo de atención:
1. Permite a los mozos registrar pedidos en tiempo real por mesa desde el celular.
2. Calcula automáticamente subtotales, totales y asegura precios históricos en cada orden mediante transacciones atómicas.
3. Notifica el estado de ocupación de las mesas (`LIBRE` u `OCUPADA`) de manera visual e interactiva.
4. Permite compartir el detalle del consumo directo al WhatsApp del cliente y ofrece un panel de reportes gerenciales para el dueño.

---

## 👥 3. Roles de Usuario y Permisos

| Rol | Funciones en la App |
| :--- | :--- |
| **Administrador (Dueño)** | Gestión de platos (CRUD), gestión de mesas (capacidad y número), y acceso al panel de **Reportes de Ventas**. |
| **Mozo** | Apertura de pedidos por mesa, adición de platos, cierre de cuentas y envío de comprobante por WhatsApp. *(La opción de Reportes se oculta por seguridad).* |

---

## 🏃 4. Metodología Ágil: Estructura de Ramas por Sprint

El desarrollo se planificó en **4 Sprints** con un total de **12 Historias de Usuario (42 Puntos de Historia)**:

```text
main (Versión final completa)
 ├── Sprint1 (App navegable, login validado, menú e identidad visual)
 ├── Sprint2 (Persistencia con XAMPP/MySQL: CRUD de platos y mesas)
 ├── Sprint3 (Toma de pedidos maestro-detalle y cierre de cuenta con transacciones)
 └── Sprint4 (Dashboard de reportes, compartir por WhatsApp, sesión SharedPreferences y APK)
