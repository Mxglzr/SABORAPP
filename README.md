# SaborApp â€” PollerÃ­a El Buen Sabor ðŸ—
AplicaciÃ³n Android nativa desarrollada en Kotlin para la gestiÃ³n y digitalizaciÃ³n de comandas y cuentas de restaurante.

## ðŸš€ Sprint 1: App Navegable
- Login y navegaciÃ³n validados.

## ðŸš€ Sprint 2: Platos y Mesas en XAMPP
- GestiÃ³n de catÃ¡logo de platos y mesas con persistencia MySQL.

## ðŸš€ Sprint 3: Toma de Pedidos por Mesa y Cierre de Cuenta
- **Objetivo:** El mozo toma pedidos por mesa y cierra la cuenta con transacciones atÃ³micas.
- **Historias de Usuario:**
  - `HU-07`: EdiciÃ³n, eliminaciÃ³n y bÃºsqueda de platos en tiempo real.
  - `HU-08`: Toma de pedidos maestro-detalle con transacciones PDO, guardando precio unitario histÃ³rico y actualizando estado de mesa a `OCUPADA`.
  - `HU-09`: Cierre de cuenta y liberaciÃ³n automÃ¡tica de la mesa (`LIBRE`).
