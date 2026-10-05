package com.sanchez.saborapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanchez.saborapp.databinding.ItemDetallePedidoBinding
import com.sanchez.saborapp.model.DetallePedido

class DetallePedidoAdapter(
    private var lista: List<DetallePedido>
) : RecyclerView.Adapter<DetallePedidoAdapter.DetalleViewHolder>() {

    inner class DetalleViewHolder(val binding: ItemDetallePedidoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val binding = ItemDetallePedidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetalleViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        val item = lista[position]
        holder.binding.tvDescripcionDetalle.text = "${item.cantidad} x ${item.nombre_plato}"
        holder.binding.tvSubtotalDetalle.text = String.format("%.2f", item.subtotal)
    }

    fun actualizarLista(nuevaLista: List<DetallePedido>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
