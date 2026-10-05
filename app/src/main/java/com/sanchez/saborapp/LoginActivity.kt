package com.sanchez.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.saborapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIngresar.setOnClickListener {
            validarIngreso()
        }
    }

    private fun validarIngreso() {
        val usuario = binding.etUsuario.text?.toString()?.trim().orEmpty()
        val clave = binding.etClave.text?.toString()?.trim().orEmpty()

        // CA1: Validar campos vacÃ­os con mensaje de error debajo
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

        // CA2: admin / 1234 -> abrir menÃº y cerrar login (no regresa)
        if (usuario == "admin" && clave == "1234") {
            val intent = Intent(this, MenuActivity::class.java).apply {
                putExtra("NOMBRE_USUARIO", "admin")
                putExtra("ROL_USUARIO", "ADMIN")
            }
            startActivity(intent)
            finish()
        } else if (usuario == "mozo" && clave == "1234") {
            val intent = Intent(this, MenuActivity::class.java).apply {
                putExtra("NOMBRE_USUARIO", "mozo")
                putExtra("ROL_USUARIO", "MOZO")
            }
            startActivity(intent)
            finish()
        } else {
            // CA3: Credenciales incorrectas
            Toast.makeText(this, getString(R.string.error_credenciales), Toast.LENGTH_SHORT).show()
        }
    }
}
