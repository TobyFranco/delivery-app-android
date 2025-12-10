package com.example.deliveryapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Datos del cliente
    val nombreCliente: String,
    val telefonoCliente: String,
    val direccionCliente: String,
    val emailCliente: String?,

    // Datos del pedido
    val descripcionPedido: String,
    val montoPedido: Double,
    val estadoPedido: String, // "Pendiente", "En Camino", "Entregado", "Cancelado"

    // Imagen del pedido
    val imagenPath: String?, // Ruta local de la imagen

    // Control de sincronización
    val sincronizado: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaModificacion: Long = System.currentTimeMillis()
)