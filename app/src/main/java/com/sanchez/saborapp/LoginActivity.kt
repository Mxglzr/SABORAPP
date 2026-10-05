package com.sanchez.saborapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sanchez.saborapp.databinding.ActivityLoginBinding
import com.sanchez.saborapp.model.LoginRequest
import com.sanchez.saborapp.network.ApiClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // HU-12 CA1: Si ya hay sesión guardada, entrar directo al Menú
        val prefs = getSharedPreferences("saborapp_prefs", MODE_PRIVATE)
        val guardadoUsuario = prefs.getString("usuario", null)
        val guardadoRol = prefs.getString("rol", null)
        if (!guardadoUsuario.isNullOrEmpty() && !guardadoRol.isNullOrEmpty()) {
            val intent = Intent(this, MenuActivity::class.java).apply {
                putExtra("NOMBRE_USUARIO", guardadoUsuario)
                putExtra("ROL_USUARIO", guardadoRol)
            }
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIngresar.setOnClickListener {
            validarYConectar()
        }
    }

    private fun validarYConectar() {
        val usuario = binding.etUsuario.text?.toString()?.trim().orEmpty()
        val clave = binding.etClave.text?.toString()?.trim().orEmpty()

        // CA1: Validar campos vacíos con mensaje de error debajo
        var hayError = false
        if (usuario.isEmpty()) {
            binding.tilUsuario.error = getString(R.string.error_usuario_vacio)
            hayError = true
        } else {
            binding.tilUsuario.error = null
        }

        if (clave.isEmpty()) {
            binding.tilClave.error = getString(R.string.error_clave_vacia)
            hayError = true
        } else {
            binding.tilClave.error = null
        }

        if (hayError) return

        // Iniciar llamada de red a XAMPP
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.login(LoginRequest(usuario, clave))
                setLoading(false)

                if (response.isSuccessful && response.body()?.status == true) {
                    val user = response.body()?.usuario
                    val rol = user?.rol ?: "MOZO"
                    val nombre = user?.usuario ?: usuario

                    // HU-12: Recordar sesión en SharedPreferences
                    val prefs = getSharedPreferences("saborapp_prefs", MODE_PRIVATE)
                    prefs.edit()
                        .putInt("id_usuario", user?.id ?: 0)
                        .putString("usuario", nombre)
                        .putString("rol", rol)
                        .apply()

                    // CA2: Abrir menú principal y cerrar login (no regresa con atrás)
                    val intent = Intent(this@LoginActivity, MenuActivity::class.java).apply {
                        putExtra("ID_USUARIO", user?.id ?: 0)
                        putExtra("NOMBRE_USUARIO", nombre)
                        putExtra("ROL_USUARIO", rol)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    // CA3: Credenciales incorrectas
                    val mensaje = response.body()?.mensaje ?: getString(R.string.error_credenciales)
                    Toast.makeText(this@LoginActivity, mensaje, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                setLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    "${getString(R.string.error_conexion)}: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setLoading(cargando: Boolean) {
        binding.progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        binding.btnIngresar.isEnabled = !cargando
    }
}
