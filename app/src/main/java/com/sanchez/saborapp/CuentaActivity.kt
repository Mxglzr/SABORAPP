package com.sanchez.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanchez.saborapp.adapter.DetallePedidoAdapter
import com.sanchez.saborapp.databinding.ActivityCuentaBinding
import com.sanchez.saborapp.model.CerrarCuentaRequest
import com.sanchez.saborapp.model.Pedido
import com.sanchez.saborapp.network.ApiClient
import kotlinx.coroutines.launch

class CuentaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuentaBinding
    private var pedido: Pedido? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        pedido = intent.getSerializableExtra("PEDIDO_EXTRA") as? Pedido

        mostrarDatos()
        setupListeners()
    }

    private fun mostrarDatos() {
        val p = pedido ?: return

        binding.tvTituloCuenta.text = "Cuenta · Mesa ${p.numero_mesa ?: p.id_mesa}"
        binding.tvSubtituloPedido.text = "Pedido #${p.id} · ${p.fecha}"
        binding.tvTotalCuenta.text = "S/ ${String.format("%.2f", p.total)}"

        val adapter = DetallePedidoAdapter(p.detalles)
        binding.rvDetalleCuenta.layoutManager = LinearLayoutManager(this)
        binding.rvDetalleCuenta.adapter = adapter

        // Si ya está cerrado o no tiene platos, deshabilitar botón (HU-09 CA3)
        binding.btnCerrarCuenta.isEnabled = (p.estado == "ABIERTO" && p.detalles.isNotEmpty())
    }

    private fun setupListeners() {
        binding.btnBackCuenta.setOnClickListener { finish() }

        // Cerrar cuenta (HU-09)
        binding.btnCerrarCuenta.setOnClickListener {
            confirmarCierreCuenta()
        }

        // Compartir por WhatsApp (HU-11)
        binding.btnCompartirWhatsApp.setOnClickListener {
            compartirPorWhatsApp()
        }
    }

    private fun confirmarCierreCuenta() {
        val p = pedido ?: return
        AlertDialog.Builder(this)
            .setTitle("Cerrar cuenta")
            .setMessage("¿Desea cerrar la cuenta de la Mesa ${p.numero_mesa ?: p.id_mesa} por un total de S/ ${String.format("%.2f", p.total)}?")
            .setPositiveButton("Cerrar cuenta") { _, _ ->
                ejecutarCierre(p.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun ejecutarCierre(idPedido: Int) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.cerrarCuenta(CerrarCuentaRequest(id_pedido = idPedido))

                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@CuentaActivity, "Mesa liberada con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val msg = response.body()?.mensaje ?: "Error al cerrar cuenta"
                    Toast.makeText(this@CuentaActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CuentaActivity, "Error de red: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // HU-11: Compartir por WhatsApp (Intent ACTION_SEND con createChooser)
    private fun compartirPorWhatsApp() {
        val p = pedido ?: return

        val builder = java.lang.StringBuilder()
        builder.append("🍗 *Pollería El Buen Sabor*\n")
        builder.append("-----------------------------\n")
        builder.append("Mesa: ${p.numero_mesa ?: p.id_mesa}\n")
        builder.append("Pedido: #${p.id}\n")
        builder.append("Fecha: ${p.fecha}\n")
        builder.append("-----------------------------\n")

        for (item in p.detalles) {
            builder.append("${item.cantidad} x ${item.nombre_plato}  -  S/ ${String.format("%.2f", item.subtotal)}\n")
        }

        builder.append("-----------------------------\n")
        builder.append("*TOTAL: S/ ${String.format("%.2f", p.total)}*\n")
        builder.append("¡Gracias por su preferencia!")

        val textoCuenta = builder.toString()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textoCuenta)
            type = "text/plain"
        }

        // Selector seguro: si no hay WhatsApp abre las demás apps sin cerrarse (CA2, CA3)
        val shareIntent = Intent.createChooser(sendIntent, "Enviar cuenta por:")
        startActivity(shareIntent)
    }
}
