package com.sanchez.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanchez.saborapp.adapter.DetallePedidoAdapter
import com.sanchez.saborapp.adapter.MesaAdapter
import com.sanchez.saborapp.databinding.ActivityPedidoBinding
import com.sanchez.saborapp.model.*
import com.sanchez.saborapp.network.ApiClient
import kotlinx.coroutines.launch

class PedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoBinding
    private lateinit var mesaAdapter: MesaAdapter
    private lateinit var detalleAdapter: DetallePedidoAdapter

    private var mesas: List<Mesa> = emptyList()
    private var platosDisponibles: List<Plato> = emptyList()
    private var mesaSeleccionada: Mesa? = null
    private var pedidoActual: Pedido? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        cargarDatosIniciales()
    }

    private fun setupRecyclerViews() {
        // Grilla de mesas (3 columnas)
        mesaAdapter = MesaAdapter(emptyList()) { mesa ->
            seleccionarMesa(mesa)
        }
        binding.rvMesasPedido.layoutManager = GridLayoutManager(this, 3)
        binding.rvMesasPedido.adapter = mesaAdapter

        // Lista de detalles del pedido
        detalleAdapter = DetallePedidoAdapter(emptyList())
        binding.rvDetallePedido.layoutManager = LinearLayoutManager(this)
        binding.rvDetallePedido.adapter = detalleAdapter
    }

    private fun setupListeners() {
        binding.btnBackPedido.setOnClickListener { finish() }

        // Agregar plato al pedido (HU-08 CA3)
        binding.btnAgregarPlato.setOnClickListener {
            agregarPlato()
        }

        // Ver cuenta (HU-09)
        binding.btnVerCuenta.setOnClickListener {
            val ped = pedidoActual
            if (ped == null || ped.detalles.isEmpty()) {
                Toast.makeText(this, "No hay consumos en esta mesa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, CuentaActivity::class.java).apply {
                putExtra("PEDIDO_EXTRA", ped)
            }
            startActivity(intent)
        }
    }

    private fun cargarDatosIniciales() {
        lifecycleScope.launch {
            try {
                // 1. Cargar mesas
                val respMesas = ApiClient.apiService.getMesas()
                if (respMesas.isSuccessful && respMesas.body()?.status == true) {
                    mesas = respMesas.body()?.datos ?: emptyList()
                    mesaAdapter.actualizarLista(mesas)

                    // Si no hay mesa seleccionada, tomar la primera
                    if (mesaSeleccionada == null && mesas.isNotEmpty()) {
                        seleccionarMesa(mesas.first())
                    } else if (mesaSeleccionada != null) {
                        // Refrescar mesa seleccionada
                        val actualizada = mesas.find { it.id == mesaSeleccionada!!.id }
                        if (actualizada != null) seleccionarMesa(actualizada)
                    }
                }

                // 2. Cargar platos disponibles (HU-07 CA4: solo disponibles)
                val respPlatos = ApiClient.apiService.getPlatos()
                if (respPlatos.isSuccessful && respPlatos.body()?.status == true) {
                    val todos = respPlatos.body()?.datos ?: emptyList()
                    platosDisponibles = todos.filter { it.disponible == 1 }

                    val nombresPlatos = platosDisponibles.map { "${it.nombre} (S/ ${String.format("%.2f", it.precio)})" }
                    val spinnerAdapter = ArrayAdapter(this@PedidoActivity, android.R.layout.simple_spinner_dropdown_item, nombresPlatos)
                    binding.spnPlatos.adapter = spinnerAdapter
                }
            } catch (e: Exception) {
                Toast.makeText(this@PedidoActivity, "Error al cargar datos: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun seleccionarMesa(mesa: Mesa) {
        mesaSeleccionada = mesa
        binding.tvMesaSeleccionada.text = "Mesa ${mesa.numero} · ${if (mesa.estado == "OCUPADA") "pedido abierto" else "disponible"}"
        cargarPedidoDeMesa(mesa.id)
    }

    private fun cargarPedidoDeMesa(idMesa: Int) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getPedidoPorMesa(idMesa)
                if (response.isSuccessful && response.body()?.status == true && response.body()?.datos != null) {
                    pedidoActual = response.body()?.datos
                    detalleAdapter.actualizarLista(pedidoActual?.detalles ?: emptyList())
                    binding.tvTotalPedido.text = "S/ ${String.format("%.2f", pedidoActual?.total ?: 0.0)}"
                    binding.btnVerCuenta.isEnabled = true
                } else {
                    pedidoActual = null
                    detalleAdapter.actualizarLista(emptyList())
                    binding.tvTotalPedido.text = "S/ 0.00"
                    binding.btnVerCuenta.isEnabled = false
                }
            } catch (e: Exception) {
                pedidoActual = null
                detalleAdapter.actualizarLista(emptyList())
                binding.tvTotalPedido.text = "S/ 0.00"
            }
        }
    }

    private fun agregarPlato() {
        val mesa = mesaSeleccionada
        if (mesa == null) {
            Toast.makeText(this, "Seleccione una mesa", Toast.LENGTH_SHORT).show()
            return
        }

        val platoPos = binding.spnPlatos.selectedItemPosition
        if (platoPos < 0 || platoPos >= platosDisponibles.size) {
            Toast.makeText(this, "Seleccione un plato", Toast.LENGTH_SHORT).show()
            return
        }

        val plato = platosDisponibles[platoPos]
        val cantStr = binding.etCantidad.text?.toString()?.trim().orEmpty()
        val cantidad = cantStr.toIntOrNull() ?: 0

        if (cantidad <= 0) {
            binding.tilCantidad.error = "Mayor a 0"
            Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            return
        } else {
            binding.tilCantidad.error = null
        }

        lifecycleScope.launch {
            try {
                val req = AgregarPlatoRequest(
                    id_mesa = mesa.id,
                    id_plato = plato.id,
                    cantidad = cantidad
                )
                val response = ApiClient.apiService.agregarPlatoPedido(req)

                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@PedidoActivity, "Plato agregado", Toast.LENGTH_SHORT).show()
                    binding.etCantidad.setText("1")
                    // Recargar datos para refrescar mesas y pedido
                    cargarDatosIniciales()
                } else {
                    val msg = response.body()?.mensaje ?: "Error al agregar plato"
                    Toast.makeText(this@PedidoActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PedidoActivity, "Error de red: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
