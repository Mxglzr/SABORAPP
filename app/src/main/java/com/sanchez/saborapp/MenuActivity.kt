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

        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "admin"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "ADMIN"

        // CA1: Mostrar datos del usuario
        binding.tvBienvenida.text = getString(R.string.saludo_usuario, nombreUsuario)
        binding.tvRol.text = getString(R.string.rol_usuario, if (rolUsuario == "ADMIN") "Administrador" else "Mozo")
        binding.tvAvatarInicial.text = nombreUsuario.firstOrNull()?.uppercase() ?: "A"

        // CA4: Si es MOZO, ocultar la opciÃ³n Reportes (solo para ADMIN)
        if (rolUsuario == "MOZO") {
            binding.cardReportes.visibility = View.GONE
        } else {
            binding.cardReportes.visibility = View.VISIBLE
        }

        // CA2: NavegaciÃ³n de prototipos
        binding.cardPlatos.setOnClickListener {
            Toast.makeText(this, "Platos (Disponible en Sprint 2)", Toast.LENGTH_SHORT).show()
        }

        binding.cardMesas.setOnClickListener {
            Toast.makeText(this, "Mesas (Disponible en Sprint 2)", Toast.LENGTH_SHORT).show()
        }

        binding.cardPedidos.setOnClickListener {
            Toast.makeText(this, "Pedidos (Disponible en Sprint 3)", Toast.LENGTH_SHORT).show()
        }

        binding.cardReportes.setOnClickListener {
            Toast.makeText(this, "Reportes (Disponible en Sprint 4)", Toast.LENGTH_SHORT).show()
        }

        // CA3: Salir y volver al Login
        binding.btnSalir.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
