package com.sanchez.saborapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanchez.saborapp.databinding.ActivityPlatoFormBinding
import com.sanchez.saborapp.model.Plato
import com.sanchez.saborapp.network.ApiClient
import kotlinx.coroutines.launch

class PlatoFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatoFormBinding
    private var platoEdicion: Plato? = null
    private val categorias = arrayOf("Fondos", "Entradas", "Bebidas", "Postres")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        verificarModoEdicion()
        setupListeners()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
        binding.actvCategoria.setAdapter(adapter)
        if (categorias.isNotEmpty() && platoEdicion == null) {
            binding.actvCategoria.setText(categorias[0], false)
        }
    }

    private fun verificarModoEdicion() {
        @Suppress("DEPRECATION")
        platoEdicion = intent.getSerializableExtra("PLATO_EXTRA") as? Plato

        if (platoEdicion != null) {
            // MODO EDICIÓN (HU-07)
            binding.tvTituloForm.text = "Editar plato"
            binding.btnGuardarPlato.text = "Actualizar"
            binding.btnEliminarPlato.visibility = View.VISIBLE

            platoEdicion?.let { p ->
                binding.etNombre.setText(p.nombre)
                binding.etPrecio.setText(p.precio.toString())
                binding.actvCategoria.setText(p.categoria, false)
                binding.switchDisponible.isChecked = (p.disponible == 1)
            }
        } else {
            // MODO REGISTRO (HU-05)
            binding.tvTituloForm.text = "Nuevo plato"
            binding.btnGuardarPlato.text = "Guardar"
            binding.btnEliminarPlato.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnBackForm.setOnClickListener { finish() }

        binding.btnGuardarPlato.setOnClickListener {
            guardarOActualizar()
        }

        binding.btnEliminarPlato.setOnClickListener {
            confirmarEliminacion()
        }
    }

    private fun guardarOActualizar() {
        val nombre = binding.etNombre.text?.toString()?.trim().orEmpty()
        val precioStr = binding.etPrecio.text?.toString()?.trim().orEmpty()
        val categoria = binding.actvCategoria.text?.toString()?.trim().orEmpty()
        val disponible = if (binding.switchDisponible.isChecked) 1 else 0

        // CA1: Validar campos vacíos
        if (nombre.isEmpty()) {
            binding.tilNombre.error = "Ingrese el nombre del plato"
            return
        } else {
            binding.tilNombre.error = null
        }

        if (categoria.isEmpty()) {
            binding.tilCategoria.error = "Seleccione una categoría"
            return
        } else {
            binding.tilCategoria.error = null
        }

        if (precioStr.isEmpty()) {
            binding.tilPrecio.error = "Ingrese el precio"
            return
        } else {
            binding.tilPrecio.error = null
        }

        val precio = precioStr.toDoubleOrNull() ?: 0.0

        // CA2: Validar precio mayor a 0
        if (precio <= 0) {
            binding.tilPrecio.error = "Precio inválido"
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
            return
        } else {
            binding.tilPrecio.error = null
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                if (platoEdicion == null) {
                    // REGISTRAR (HU-05)
                    val nuevoPlato = Plato(nombre = nombre, categoria = categoria, precio = precio, disponible = disponible)
                    val response = ApiClient.apiService.registrarPlato(nuevoPlato)
                    setLoading(false)

                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@PlatoFormActivity, "Plato registrado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@PlatoFormActivity, response.body()?.mensaje ?: "Error al registrar", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // ACTUALIZAR (HU-07)
                    val platoActualizado = platoEdicion!!.copy(
                        nombre = nombre,
                        categoria = categoria,
                        precio = precio,
                        disponible = disponible
                    )
                    val response = ApiClient.apiService.actualizarPlato(platoActualizado)
                    setLoading(false)

                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@PlatoFormActivity, "Plato actualizado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@PlatoFormActivity, response.body()?.mensaje ?: "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                setLoading(false)
                Toast.makeText(this@PlatoFormActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmarEliminacion() {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro de eliminar este plato?")
            .setPositiveButton("Eliminar") { _, _ ->
                ejecutarEliminacion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun ejecutarEliminacion() {
        val id = platoEdicion?.id ?: return
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.eliminarPlato(id)
                setLoading(false)

                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@PlatoFormActivity, "Plato eliminado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // CA2: Si tiene pedidos, mostrar el mensaje exacto
                    val msg = response.body()?.mensaje ?: "No se pudo eliminar el plato"
                    Toast.makeText(this@PlatoFormActivity, msg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                setLoading(false)
                Toast.makeText(this@PlatoFormActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoading(cargando: Boolean) {
        binding.progressBarForm.visibility = if (cargando) View.VISIBLE else View.GONE
        binding.btnGuardarPlato.isEnabled = !cargando
        binding.btnEliminarPlato.isEnabled = !cargando
    }
}
