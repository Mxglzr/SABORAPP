package com.sanchez.saborapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanchez.saborapp.adapter.PlatoAdapter
import com.sanchez.saborapp.databinding.ActivityPlatosBinding
import com.sanchez.saborapp.model.Plato
import com.sanchez.saborapp.network.ApiClient
import kotlinx.coroutines.launch

class PlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatosBinding
    private lateinit var adapter: PlatoAdapter
    private var categoriaSeleccionada: String = "Todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        cargarPlatos()
    }

    private fun setupRecyclerView() {
        adapter = PlatoAdapter(emptyList()) { plato ->
            // Abrir formulario en modo edición (HU-07)
            val intent = Intent(this, PlatoFormActivity::class.java).apply {
                putExtra("PLATO_EXTRA", plato)
            }
            startActivity(intent)
        }
        binding.rvPlatos.layoutManager = LinearLayoutManager(this)
        binding.rvPlatos.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // Búsqueda en tiempo real (HU-07 CA3)
        binding.etBuscarPlato.doAfterTextChanged {
            cargarPlatos()
        }

        // Filtro por Chips de categoría
        binding.chipGroupCategorias.setOnCheckedStateChangeListener { _, checkedIds ->
            categoriaSeleccionada = when (checkedIds.firstOrNull()) {
                R.id.chipFondos -> "Fondos"
                R.id.chipBebidas -> "Bebidas"
                R.id.chipPostres -> "Postres"
                R.id.chipEntradas -> "Entradas"
                else -> "Todos"
            }
            cargarPlatos()
        }

        // Botón FAB para registrar nuevo plato (HU-05)
        binding.fabAgregarPlato.setOnClickListener {
            val intent = Intent(this, PlatoFormActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarPlatos() {
        val buscar = binding.etBuscarPlato.text?.toString()?.trim()
        val catParam = if (categoriaSeleccionada == "Todos") null else categoriaSeleccionada

        binding.progressBarPlatos.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getPlatos(buscar, catParam)
                binding.progressBarPlatos.visibility = View.GONE

                if (response.isSuccessful && response.body()?.status == true) {
                    val lista = response.body()?.datos ?: emptyList()
                    adapter.actualizarLista(lista)
                } else {
                    Toast.makeText(this@PlatosActivity, "No se encontraron platos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBarPlatos.visibility = View.GONE
                Toast.makeText(this@PlatosActivity, "Error de conexión: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
