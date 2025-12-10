package com.example.deliveryapp.model

import com.google.gson.annotations.SerializedName

data class PedidoRequest(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre_cliente")
    val nombreCliente: String,

    @SerializedName("telefono_cliente")
    val telefonoCliente: String,

    @SerializedName("direccion_cliente")
    val direccionCliente: String,

    @SerializedName("email_cliente")
    val emailCliente: String?,

    @SerializedName("descripcion_pedido")
    val descripcionPedido: String,

    @SerializedName("monto_pedido")
    val montoPedido: Double,

    @SerializedName("estado_pedido")
    val estadoPedido: String,

    @SerializedName("imagen_base64")
    val imagenBase64: String?,

    @SerializedName("fecha_creacion")
    val fechaCreacion: Long,

    @SerializedName("fecha_modificacion")
    val fechaModificacion: Long
)

data class PedidoResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: Any?
)