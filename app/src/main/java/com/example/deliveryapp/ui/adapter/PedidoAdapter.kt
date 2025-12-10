package com.example.deliveryapp.ui.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entity.PedidoEntity
import com.example.deliveryapp.databinding.ItemPedidoBinding
import java.text.NumberFormat
import java.util.Locale

class PedidoAdapter(
    private val onItemClick: (PedidoEntity) -> Unit,
    private val onSyncClick: (PedidoEntity) -> Unit
) : ListAdapter<PedidoEntity, PedidoAdapter.PedidoViewHolder>(PedidoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val binding = ItemPedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PedidoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = getItem(position)
        holder.bind(pedido)
    }

    inner class PedidoViewHolder(
        private val binding: ItemPedidoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pedido: PedidoEntity) {
            binding.apply {
                tvNombreCliente.text = pedido.nombreCliente
                tvTelefono.text = "📞 ${pedido.telefonoCliente}"
                tvDescripcion.text = pedido.descripcionPedido

                val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "PY"))
                tvMonto.text = formatoMoneda.format(pedido.montoPedido)

                chipEstado.text = pedido.estadoPedido
                chipEstado.setChipBackgroundColorResource(
                    when (pedido.estadoPedido) {
                        "Pendiente" -> R.color.chip_pendiente
                        "En Camino" -> R.color.chip_en_camino
                        "Entregado" -> R.color.chip_entregado
                        "Cancelado" -> R.color.chip_cancelado
                        else -> R.color.chip_pendiente
                    }
                )

                if (pedido.imagenPath != null) {
                    try {
                        val bitmap = BitmapFactory.decodeFile(pedido.imagenPath)
                        ivPedidoImagen.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        ivPedidoImagen.setImageResource(android.R.drawable.ic_menu_camera)
                    }
                } else {
                    ivPedidoImagen.setImageResource(android.R.drawable.ic_menu_camera)
                }

                if (pedido.sincronizado) {
                    ivSincronizado.visibility = View.GONE
                } else {
                    ivSincronizado.visibility = View.VISIBLE
                    ivSincronizado.setOnClickListener {
                        onSyncClick(pedido)
                    }
                }

                root.setOnClickListener {
                    onItemClick(pedido)
                }
            }
        }
    }

    private class PedidoDiffCallback : DiffUtil.ItemCallback<PedidoEntity>() {
        override fun areItemsTheSame(oldItem: PedidoEntity, newItem: PedidoEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PedidoEntity, newItem: PedidoEntity): Boolean {
            return oldItem == newItem
        }
    }
}