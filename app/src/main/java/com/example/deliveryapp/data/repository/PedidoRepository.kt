package com.example.deliveryapp.data.repository

import androidx.lifecycle.LiveData
import com.example.deliveryapp.data.local.dao.PedidoDao
import com.example.deliveryapp.data.local.entity.PedidoEntity
import com.example.deliveryapp.data.remote.api.ApiService
import com.example.deliveryapp.model.PedidoRequest
import com.example.deliveryapp.util.ImageUtils
import com.example.deliveryapp.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PedidoRepository(
    private val pedidoDao: PedidoDao,
    private val apiService: ApiService
) {

    val allPedidos: LiveData<List<PedidoEntity>> = pedidoDao.getAllPedidos()

    suspend fun insertPedido(pedido: PedidoEntity): Long {
        return withContext(Dispatchers.IO) {
            try {
                val id = pedidoDao.insertPedido(pedido)
                LogUtils.logInfo("Pedido creado localmente con ID: $id")
                id
            } catch (e: Exception) {
                LogUtils.logError("Error al insertar pedido", e)
                throw e
            }
        }
    }

    suspend fun updatePedido(pedido: PedidoEntity) {
        withContext(Dispatchers.IO) {
            try {
                val updatedPedido = pedido.copy(
                    fechaModificacion = System.currentTimeMillis(),
                    sincronizado = false
                )
                pedidoDao.updatePedido(updatedPedido)
                LogUtils.logInfo("Pedido actualizado: ID ${pedido.id}")
            } catch (e: Exception) {
                LogUtils.logError("Error al actualizar pedido", e)
                throw e
            }
        }
    }

    suspend fun deletePedido(pedido: PedidoEntity) {
        withContext(Dispatchers.IO) {
            try {
                pedidoDao.deletePedido(pedido)
                LogUtils.logInfo("Pedido eliminado: ID ${pedido.id}")
            } catch (e: Exception) {
                LogUtils.logError("Error al eliminar pedido", e)
                throw e
            }
        }
    }

    suspend fun getPedidoById(id: Int): PedidoEntity? {
        return withContext(Dispatchers.IO) {
            pedidoDao.getPedidoById(id)
        }
    }

    fun getPedidosByEstado(estado: String): LiveData<List<PedidoEntity>> {
        return pedidoDao.getPedidosByEstado(estado)
    }

    suspend fun sincronizarPedido(pedido: PedidoEntity): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                LogUtils.logInfo("Iniciando sincronización del pedido ID: ${pedido.id}")

                val imagenBase64 = pedido.imagenPath?.let { path ->
                    ImageUtils.imageToBase64(path)
                }

                val request = PedidoRequest(
                    id = pedido.id,
                    nombreCliente = pedido.nombreCliente,
                    telefonoCliente = pedido.telefonoCliente,
                    direccionCliente = pedido.direccionCliente,
                    emailCliente = pedido.emailCliente,
                    descripcionPedido = pedido.descripcionPedido,
                    montoPedido = pedido.montoPedido,
                    estadoPedido = pedido.estadoPedido,
                    imagenBase64 = imagenBase64,
                    fechaCreacion = pedido.fechaCreacion,
                    fechaModificacion = pedido.fechaModificacion
                )

                val response = apiService.enviarPedido(request)

                if (response.isSuccessful) {
                    pedidoDao.marcarComoSincronizado(pedido.id)
                    LogUtils.logInfo("Pedido ${pedido.id} sincronizado exitosamente")
                    Result.success("Pedido sincronizado correctamente")
                } else {
                    LogUtils.logError("Error HTTP al sincronizar pedido: ${response.code()}")
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }

            } catch (e: Exception) {
                LogUtils.logError("Error al sincronizar pedido", e)
                Result.failure(e)
            }
        }
    }

    suspend fun sincronizarPedidosPendientes(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val pedidosNoSincronizados = pedidoDao.getPedidosNoSincronizados()
                var exitosos = 0
                var fallidos = 0

                LogUtils.logInfo("Sincronizando ${pedidosNoSincronizados.size} pedidos pendientes")

                pedidosNoSincronizados.forEach { pedido ->
                    val result = sincronizarPedido(pedido)
                    if (result.isSuccess) exitosos++ else fallidos++
                }

                LogUtils.logInfo("Sincronización completa: $exitosos exitosos, $fallidos fallidos")
                Result.success("Sincronizados: $exitosos, Fallidos: $fallidos")

            } catch (e: Exception) {
                LogUtils.logError("Error en sincronización masiva", e)
                Result.failure(e)
            }
        }
    }

    suspend fun actualizarEstadoPedido(pedidoId: Int, nuevoEstado: String) {
        withContext(Dispatchers.IO) {
            try {
                pedidoDao.actualizarEstado(
                    pedidoId,
                    nuevoEstado,
                    System.currentTimeMillis()
                )
                LogUtils.logInfo("Estado actualizado: Pedido $pedidoId -> $nuevoEstado")
            } catch (e: Exception) {
                LogUtils.logError("Error al actualizar estado", e)
                throw e
            }
        }
    }
}