package com.example.deliveryapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.deliveryapp.data.local.entity.PedidoEntity

@Dao
interface PedidoDao {

    @Query("SELECT * FROM pedidos ORDER BY fechaCreacion DESC")
    fun getAllPedidos(): LiveData<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE id = :pedidoId")
    suspend fun getPedidoById(pedidoId: Int): PedidoEntity?

    @Query("SELECT * FROM pedidos WHERE sincronizado = 0")
    suspend fun getPedidosNoSincronizados(): List<PedidoEntity>

    @Query("SELECT * FROM pedidos WHERE estadoPedido = :estado")
    fun getPedidosByEstado(estado: String): LiveData<List<PedidoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedido(pedido: PedidoEntity): Long

    @Update
    suspend fun updatePedido(pedido: PedidoEntity)

    @Delete
    suspend fun deletePedido(pedido: PedidoEntity)

    @Query("UPDATE pedidos SET sincronizado = 1 WHERE id = :pedidoId")
    suspend fun marcarComoSincronizado(pedidoId: Int)

    @Query("UPDATE pedidos SET estadoPedido = :nuevoEstado, fechaModificacion = :fecha WHERE id = :pedidoId")
    suspend fun actualizarEstado(pedidoId: Int, nuevoEstado: String, fecha: Long)

    @Query("DELETE FROM pedidos")
    suspend fun deleteAllPedidos()
}