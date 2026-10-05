package com.sanchez.saborapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.saborapp.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "MOZO"

        // Mostrar datos del usuario (CA1)
        binding.tvBienvenida.text = getString(R.string.saludo_usuario, nombreUsuario)
        binding.tvRol.text = getString(R.string.rol_usuario, if (rolUsuario == "ADMIN") "Administrador" else "Mozo")
        binding.tvAvatarInicial.text = nombreUsuario.firstOrNull()?.uppercase() ?: "U"

        // CA4: Si es MOZO, ocultar la opción Reportes (solo visible para ADMIN)
        if (rolUsuario == "MOZO") {
            binding.cardReportes.visibility = View.GONE
        } else {
            binding.cardReportes.visibility = View.VISIBLE
        }

        // Navegación (CA2)
        binding.cardPlatos.setOnClickListener {
            startActivity(Intent(this, PlatosActivity::class.java))
        }

        binding.cardMesas.setOnClickListener {
            startActivity(Intent(this, MesasActivity::class.java))
        }

        binding.cardPedidos.setOnClickListener {
            startActivity(Intent(this, PedidoActivity::class.java))
        }

        binding.cardReportes.setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }

        // HU-02 CA3 & HU-12 CA2: Salir, borrar sesión y volver al Login
        binding.btnSalir.setOnClickListener {
            val prefs = getSharedPreferences("saborapp_prefs", MODE_PRIVATE)
            prefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
