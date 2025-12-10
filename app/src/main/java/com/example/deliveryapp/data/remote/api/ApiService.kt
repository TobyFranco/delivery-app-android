package com.example.deliveryapp.data.remote.api

import com.example.deliveryapp.model.PedidoRequest
import com.example.deliveryapp.model.PedidoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("pedidos/crear")
    suspend fun enviarPedido(
        @Body pedido: PedidoRequest
    ): Response<PedidoResponse>

    @POST("pedidos/actualizar")
    suspend fun actualizarPedido(
        @Body pedido: PedidoRequest
    ): Response<PedidoResponse>
}