package com.sanchez.saborapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanchez.saborapp.databinding.ItemReportePlatoBinding
import com.sanchez.saborapp.model.TopPlato

class TopPlatoAdapter(
    private var lista: List<TopPlato>,
    private var maxVendido: Int = 1
) : RecyclerView.Adapter<TopPlatoAdapter.TopPlatoViewHolder>() {

    inner class TopPlatoViewHolder(val binding: ItemReportePlatoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPlatoViewHolder {
        val binding = ItemReportePlatoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopPlatoViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: TopPlatoViewHolder, position: Int) {
        val item = lista[position]
        holder.binding.tvNombreTopPlato.text = item.nombre
        holder.binding.tvCantidadTopPlato.text = item.total_vendido.toString()

        holder.binding.pbCantidadVenta.max = if (maxVendido > 0) maxVendido else 1
        holder.binding.pbCantidadVenta.progress = item.total_vendido
    }

    fun actualizarLista(nuevaLista: List<TopPlato>) {
        lista = nuevaLista
        maxVendido = lista.maxOfOrNull { it.total_vendido } ?: 1
        notifyDataSetChanged()
    }
}
