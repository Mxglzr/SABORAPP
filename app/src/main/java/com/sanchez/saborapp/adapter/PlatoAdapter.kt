package com.sanchez.saborapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanchez.saborapp.databinding.ItemPlatoBinding
import com.sanchez.saborapp.model.Plato

class PlatoAdapter(
    private var lista: List<Plato>,
    private val onItemClick: (Plato) -> Unit
) : RecyclerView.Adapter<PlatoAdapter.PlatoViewHolder>() {

    inner class PlatoViewHolder(val binding: ItemPlatoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatoViewHolder {
        val binding = ItemPlatoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlatoViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: PlatoViewHolder, position: Int) {
        val plato = lista[position]
        with(holder.binding) {
            tvNombrePlato.text = plato.nombre
            tvDetallePlato.text = "${plato.categoria} · S/ ${String.format("%.2f", plato.precio)}"

            if (plato.disponible == 1) {
                tvBadgeEstado.text = "Disponible"
                tvBadgeEstado.setTextColor(Color.parseColor("#424242"))
                cardBadge.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
                cardPlato.strokeColor = Color.parseColor("#E0E0E0")
            } else {
                tvBadgeEstado.text = "Agotado"
                tvBadgeEstado.setTextColor(Color.parseColor("#D32F2F"))
                cardBadge.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
                cardPlato.strokeColor = Color.parseColor("#FFCDD2")
            }

            root.setOnClickListener { onItemClick(plato) }
        }
    }

    fun actualizarLista(nuevaLista: List<Plato>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
