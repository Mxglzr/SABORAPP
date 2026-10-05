# SaborApp — Pollería El Buen Sabor 🍗
**App Móvil Nativa Android para Gestión de Pedidos, Comandas y Facturación de Mesa**

| Datos Académicos | Detalle |
| :--- | :--- |
| **Institución** | SENATI |
| **Carrera** | Desarrollo de Software (VI Semestre) |
| **Curso** | Seminario de Complementación Práctica III |
| **Fecha** | Octubre 2026 |

---

## 🎯 Objetivo del Sprint 4
> **Reportes, Integración y Entrega:** Visualización de métricas de venta, compartir cuenta por WhatsApp, persistencia de sesión con SharedPreferences y generación de APK final.

---

## 📋 Historias de Usuario Implementadas

### 🔹 HU-10: Reportes de ventas (5 pts)
* **Historia:** *Como dueño, quiero ver la venta del día, los platos más pedidos y la venta por mesa, para decidir qué preparar y promocionar.*
* **Criterios de Aceptación:**
  * **CA1:** Muestra la venta acumulada del día (`SUM(total)` con fecha `CURDATE()`).
  * **CA2:** Indicadores de Total de Pedidos y Ticket Promedio diario.
  * **CA3:** Gráfico de barras con el **Top 5 de platos más pedidos** (`GROUP BY id_plato ORDER BY SUM(cantidad) DESC`).
  * **CA4:** Si no se registran cierres en la fecha, muestra el mensaje `«Sin ventas hoy»`.

### 🔹 HU-11: Compartir la cuenta por WhatsApp (2 pts)
* **Historia:** *Como mozo, quiero enviar la cuenta al celular del cliente, para que tenga el detalle de su consumo.*
* **Criterios de Aceptación:**
  * **CA1:** Construye un mensaje con formato amigable incluyendo nombre del restaurante, número de mesa, lista de productos y total.
  * **CA2:** Utiliza un Intent implícito (`Intent.ACTION_SEND`) con `Intent.createChooser` para seleccionar WhatsApp u otra aplicación.
  * **CA3:** Si el dispositivo no tiene WhatsApp instalado, el selector muestra apps alternativas sin provocar un cierre inesperado (cero crashes).

### 🔹 HU-12: Sesión recordada y APK instalable (3 pts)
* **Historia:** *Como mozo, quiero que la app recuerde mi sesión y se pueda instalar en el celular del negocio.*
* **Criterios de Aceptación:**
  * **CA1:** Persistencia de sesión mediante `SharedPreferences`. Al abrir la app, entra directamente al menú principal saltándose el login.
  * **CA2:** Al tocar «Salir» en el menú, se eliminan las credenciales y vuelve a solicitar inicio de sesión.
  * **CA3:** APK generado y listo para instalar en cualquier dispositivo con Android 7.0 (API 24) o superior.

---

## 🏗️ Arquitectura y Tecnologías
* **Frontend:** Kotlin 100% nativo + ViewBinding + Material Components (M3).
* **Networking:** Retrofit 2 + Gson Converter + Kotlin Coroutines (`lifecycleScope`).
* **Backend:** Servidor Apache + PHP (PDO) bajo entorno **XAMPP**.
* **Base de Datos:** MySQL / MariaDB con claves foráneas, restricciones `CHECK`, `ON DELETE CASCADE` y transacciones ACID.

---

## 📱 Credenciales de Prueba
| Rol | Usuario | Contraseña | Permisos |
| :--- | :---: | :---: | :--- |
| **Administrador** | `admin` | `1234` | Acceso total: Platos, Mesas, Pedidos y Reportes |
| **Mozo** | `mozo1` | `1234` | Operativo: Platos, Mesas y Pedidos (Reportes oculto) |

---

## 📦 APK Instalable
El instalable se encuentra disponible directamente en el repositorio:
📁 **[`apk/SaborApp.apk`](apk/SaborApp.apk)**

## 🗄️ Instalación del Backend en XAMPP
1. Iniciar Apache y MySQL en **XAMPP Control Panel**.
2. Abrir **phpMyAdmin** (`http://localhost/phpmyadmin`) e importar el script SQL:
   `backend/saborapp.sql`
3. Copiar la carpeta `backend/` dentro de tu directorio `htdocs` con el nombre `saborapp_api`:
   `C:\xampp\htdocs\saborapp_api\`
4. Configurar la IP en `ApiClient.kt` según la red local.
