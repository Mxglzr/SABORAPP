package com.sanchez.saborapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanchez.saborapp.adapter.TopPlatoAdapter
import com.sanchez.saborapp.databinding.ActivityReportesBinding
import com.sanchez.saborapp.model.ReporteDatos
import com.sanchez.saborapp.network.ApiClient
import kotlinx.coroutines.launch

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private lateinit var adapter: TopPlatoAdapter
    private var reporteActual: ReporteDatos? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        cargarReportes()
    }

    private fun setupRecyclerView() {
        adapter = TopPlatoAdapter(emptyList())
        binding.rvTopPlatos.layoutManager = LinearLayoutManager(this)
        binding.rvTopPlatos.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBackReportes.setOnClickListener { finish() }

        binding.btnCompartirResumen.setOnClickListener {
            compartirResumen()
        }
    }

    private fun cargarReportes() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getReportes()
                if (response.isSuccessful && response.body()?.status == true) {
                    val datos = response.body()?.datos
                    reporteActual = datos

                    if (datos != null) {
                        // CA1: Venta del día y métricas
                        binding.tvVentaHoy.text = "S/ ${String.format("%.2f", datos.venta_hoy)}"
                        binding.tvTotalPedidos.text = datos.total_pedidos.toString()
                        binding.tvTicketPromedio.text = "S/ ${String.format("%.2f", datos.ticket_promedio)}"

                        // CA2 & CA3: Lista de top platos o "Sin ventas hoy"
                        if (datos.total_pedidos == 0 || datos.top_platos.isEmpty()) {
                            binding.tvSinVentas.visibility = View.VISIBLE
                            binding.rvTopPlatos.visibility = View.GONE
                        } else {
                            binding.tvSinVentas.visibility = View.GONE
                            binding.rvTopPlatos.visibility = View.VISIBLE
                            adapter.actualizarLista(datos.top_platos)
                        }
                    }
                } else {
                    Toast.makeText(this@ReportesActivity, "No se pudieron obtener los reportes", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReportesActivity, "Error de red: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun compartirResumen() {
        val rep = reporteActual ?: return

        val builder = StringBuilder()
        builder.append("📊 *Reporte Diario - Pollería El Buen Sabor*\n")
        builder.append("------------------------------------\n")
        builder.append("💰 *Venta de hoy:* S/ ${String.format("%.2f", rep.venta_hoy)}\n")
        builder.append("🧾 *Pedidos cerrados:* ${rep.total_pedidos}\n")
        builder.append("📈 *Ticket promedio:* S/ ${String.format("%.2f", rep.ticket_promedio)}\n")
        builder.append("------------------------------------\n")
        builder.append("*Top platos más pedidos:*\n")

        if (rep.top_platos.isEmpty()) {
            builder.append("Sin ventas registradas hoy.\n")
        } else {
            for ((index, item) in rep.top_platos.withIndex()) {
                builder.append("${index + 1}. ${item.nombre}: ${item.total_vendido} vendidos\n")
            }
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, builder.toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Compartir reporte diario")
        startActivity(shareIntent)
    }
}
