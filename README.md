# SaborApp — Pollería El Buen Sabor 🍗
**Proyecto:** App Android para gestión de comandas y cuentas  
**Institución:** SENATI — Seminario de Complementación Práctica III (VI Semestre)  
**Rama:** `Sprint3`  

---

## 🎯 Objetivo del Sprint 3
> **Operación Principal:** El mozo toma pedidos por mesa y cierra la cuenta con transacciones atómicas maestro-detalle.

---

## 📋 Historias de Usuario Implementadas

### 🔹 HU-07: Editar, eliminar y buscar platos (3 pts)
* **Historia:** *Como administrador, quiero corregir, eliminar y buscar platos, para mantener la carta actualizada.*
* **Criterios de Aceptación:**
  * **CA1:** Al pulsar un plato se abre `PlatoFormActivity` en modo edición con sus datos precargados.
  * **CA2:** **Integridad referencial:** Al eliminar un plato con pedidos asociados, el sistema bloquea la acción con el mensaje: `«No se puede eliminar: tiene pedidos»`.
  * **CA3:** Buscador en tiempo real (`doAfterTextChanged`) que filtra por nombre utilizando `LIKE`.
  * **CA4:** Platos marcados como "Agotado" no aparecen en la lista de selección para pedidos del mozo.

### 🔹 HU-08: Tomar pedido por mesa (8 pts)
* **Historia:** *Como mozo, quiero elegir una mesa y agregarle platos con su cantidad, para registrar el pedido sin papel.*
* **Criterios de Aceptación:**
  * **CA1:** Grilla de mesas con distinción cromática en vivo: verde para `LIBRE` y naranja/rojo para `OCUPADA`.
  * **CA2:** Al agregar el primer plato a una mesa libre, se crea el pedido `ABIERTO` y la mesa pasa a `OCUPADA`.
  * **CA3:** Se recalcula el subtotal y total automáticamente ante cada adición.
  * **CA4:** **Transacción Atómica:** Se captura `precio_unit` histórico para evitar discrepancias si el precio del plato varía después. Si ocurre un fallo en red, se aplica `ROLLBACK` y nada queda a medias.

### 🔹 HU-09: Cerrar la cuenta de una mesa (5 pts)
* **Historia:** *Como mozo, quiero cerrar la cuenta de una mesa, para cobrar al cliente y liberar la mesa.*
* **Criterios de Aceptación:**
  * **CA1:** Vista `CuentaActivity` con el desglose detallado (platos, cantidades, precios y total general).
  * **CA2:** Al confirmar «Cerrar cuenta», el pedido pasa a `CERRADO` y la mesa vuelve a `LIBRE` en una sola transacción atómica.
  * **CA3:** Si una mesa no tiene consumos o está libre, el botón de cierre se encuentra inhabilitado.

---

## 🗄️ Endpoints Incorporados
* `GET backend/pedidos.php?id_mesa={id}`: Obtiene el pedido abierto con sus consumos.
* `POST backend/pedidos.php`: 
  * `accion: AGREGAR_PLATO` (Transacción con PDO `beginTransaction`).
  * `accion: CERRAR_CUENTA` (Cierra orden y libera mesa en un solo commit).
