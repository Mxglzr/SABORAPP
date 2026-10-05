package com.sanchez.saborapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.sanchez.saborapp.adapter.MesaAdapter
import com.sanchez.saborapp.databinding.ActivityMesasBinding
import com.sanchez.saborapp.databinding.DialogNuevaMesaBinding
import com.sanchez.saborapp.model.Mesa
import com.sanchez.saborapp.network.ApiClient
import kotlinx.coroutines.launch

class MesasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesasBinding
    private lateinit var adapter: MesaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        cargarMesas()
    }

    private fun setupRecyclerView() {
        adapter = MesaAdapter(emptyList()) { mesa ->
            Toast.makeText(this, "Mesa ${mesa.numero} (${mesa.estado})", Toast.LENGTH_SHORT).show()
        }
        // HU-06: GridLayoutManager de 3 columnas
        binding.rvMesas.layoutManager = GridLayoutManager(this, 3)
        binding.rvMesas.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBackMesas.setOnClickListener { finish() }

        binding.fabAgregarMesa.setOnClickListener {
            mostrarDialogoNuevaMesa()
        }
    }

    private fun mostrarDialogoNuevaMesa() {
        val dialogBinding = DialogNuevaMesaBinding.inflate(LayoutInflater.from(this))

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.btnCancelarMesa.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnGuardarMesa.setOnClickListener {
            val numStr = dialogBinding.etNumeroMesa.text?.toString()?.trim().orEmpty()
            val capStr = dialogBinding.etCapacidadMesa.text?.toString()?.trim().orEmpty()

            val numero = numStr.toIntOrNull() ?: 0
            val capacidad = capStr.toIntOrNull() ?: 0

            if (numero <= 0) {
                dialogBinding.tilNumeroMesa.error = "Número inválido"
                return@setOnClickListener
            } else {
                dialogBinding.tilNumeroMesa.error = null
            }

            // CA2: Capacidad debe estar entre 1 y 12
            if (capacidad < 1 || capacidad > 12) {
                dialogBinding.tilCapacidadMesa.error = "Capacidad inválida (1 a 12)"
                Toast.makeText(this@MesasActivity, "Capacidad inválida (1 a 12)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                dialogBinding.tilCapacidadMesa.error = null
            }

            guardarMesa(numero, capacidad, dialog)
        }

        dialog.show()
    }

    private fun guardarMesa(numero: Int, capacidad: Int, dialog: AlertDialog) {
        lifecycleScope.launch {
            try {
                val nuevaMesa = Mesa(numero = numero, capacidad = capacidad)
                val response = ApiClient.apiService.registrarMesa(nuevaMesa)

                if (response.isSuccessful && response.body()?.status == true) {
                    dialog.dismiss()
                    Toast.makeText(this@MesasActivity, "Mesa registrada", Toast.LENGTH_SHORT).show()
                    cargarMesas()
                } else {
                    // CA1: "La mesa ya existe"
                    val msg = response.body()?.mensaje ?: "Error al registrar mesa"
                    Toast.makeText(this@MesasActivity, msg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MesasActivity, "Error de red: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarMesas() {
        binding.progressBarMesas.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getMesas()
                binding.progressBarMesas.visibility = View.GONE

                if (response.isSuccessful && response.body()?.status == true) {
                    val lista = response.body()?.datos ?: emptyList()
                    adapter.actualizarLista(lista)
                } else {
                    Toast.makeText(this@MesasActivity, "No se pudieron cargar las mesas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBarMesas.visibility = View.GONE
                Toast.makeText(this@MesasActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
