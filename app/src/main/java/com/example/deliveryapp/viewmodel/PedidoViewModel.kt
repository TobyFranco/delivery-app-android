package com.example.deliveryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.local.database.AppDatabase
import com.example.deliveryapp.data.local.entity.PedidoEntity
import com.example.deliveryapp.data.remote.api.RetrofitClient
import com.example.deliveryapp.data.repository.PedidoRepository
import com.example.deliveryapp.util.LogUtils
import kotlinx.coroutines.launch

class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PedidoRepository
    val allPedidos: LiveData<List<PedidoEntity>>

    private val _operationStatus = MutableLiveData<Result<String>>()
    val operationStatus: LiveData<Result<String>> = _operationStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val pedidoDao = AppDatabase.getDatabase(application).pedidoDao()
        repository = PedidoRepository(pedidoDao, RetrofitClient.apiService)
        allPedidos = repository.allPedidos
        LogUtils.logInfo("PedidoViewModel inicializado")
    }

    fun insertPedido(pedido: PedidoEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val id = repository.insertPedido(pedido)
                _operationStatus.value = Result.success("Pedido creado con ID: $id")
                LogUtils.logInfo("ViewModel: Pedido creado exitosamente")
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
                LogUtils.logError("ViewModel: Error al crear pedido", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePedido(pedido: PedidoEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updatePedido(pedido)
                _operationStatus.value = Result.success("Pedido actualizado correctamente")
                LogUtils.logInfo("ViewModel: Pedido actualizado exitosamente")
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
                LogUtils.logError("ViewModel: Error al actualizar pedido", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePedido(pedido: PedidoEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deletePedido(pedido)
                _operationStatus.value = Result.success("Pedido eliminado correctamente")
                LogUtils.logInfo("ViewModel: Pedido eliminado exitosamente")
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
                LogUtils.logError("ViewModel: Error al eliminar pedido", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sincronizarPedido(pedido: PedidoEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                LogUtils.logInfo("ViewModel: Iniciando sincronización de pedido ${pedido.id}")
                val result = repository.sincronizarPedido(pedido)
                _operationStatus.value = result
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
                LogUtils.logError("ViewModel: Error en sincronización", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sincronizarTodosPendientes() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                LogUtils.logInfo("ViewModel: Sincronizando todos los pedidos pendientes")
                val result = repository.sincronizarPedidosPendientes()
                _operationStatus.value = result
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
                LogUtils.logError("ViewModel: Error en sincronización masiva", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarEstado(pedidoId: Int, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.actualizarEstadoPedido(pedidoId, nuevoEstado)
                _operationStatus.value = Result.success("Estado actualizado a: $nuevoEstado")
                LogUtils.logInfo("ViewModel: Estado actualizado para pedido $pedidoId")
            } catch (e: Exception) {
                _operationStatus.value = Result.failure(e)
                LogUtils.logError("ViewModel: Error al actualizar estado", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPedidosByEstado(estado: String): LiveData<List<PedidoEntity>> {
        return repository.getPedidosByEstado(estado)
    }

    fun clearOperationStatus() {
        _operationStatus.value = null
    }
}