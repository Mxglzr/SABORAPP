package com.sanchez.saborapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanchez.saborapp.databinding.ItemMesaBinding
import com.sanchez.saborapp.model.Mesa

class MesaAdapter(
    private var lista: List<Mesa>,
    private val onItemClick: (Mesa) -> Unit
) : RecyclerView.Adapter<MesaAdapter.MesaViewHolder>() {

    inner class MesaViewHolder(val binding: ItemMesaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val binding = ItemMesaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MesaViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        val mesa = lista[position]
        with(holder.binding) {
            tvNumeroMesa.text = "Mesa ${mesa.numero}"
            tvCapacidadMesa.text = "Cap: ${mesa.capacidad} pers."

            if (mesa.estado.uppercase() == "OCUPADA") {
                tvEstadoMesa.text = "Ocupada"
                tvEstadoMesa.setTextColor(Color.parseColor("#E53935"))
                ivIconoMesa.setColorFilter(Color.parseColor("#E53935"))
                cardMesa.strokeColor = Color.parseColor("#FF5722")
                cardMesa.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
            } else {
                tvEstadoMesa.text = "Libre"
                tvEstadoMesa.setTextColor(Color.parseColor("#4CAF50"))
                ivIconoMesa.setColorFilter(Color.parseColor("#4CAF50"))
                cardMesa.strokeColor = Color.parseColor("#E0E0E0")
                cardMesa.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            root.setOnClickListener { onItemClick(mesa) }
        }
    }

    fun actualizarLista(nuevaLista: List<Mesa>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
