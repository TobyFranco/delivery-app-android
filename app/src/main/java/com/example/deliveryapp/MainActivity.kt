package com.example.deliveryapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deliveryapp.databinding.ActivityMainBinding
import com.example.deliveryapp.ui.adapter.PedidoAdapter
import com.example.deliveryapp.util.LogUtils
import com.example.deliveryapp.viewmodel.PedidoViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PedidoViewModel by viewModels()
    private lateinit var adapter: PedidoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LogUtils.initialize(this)
        LogUtils.logInfo("MainActivity iniciada")

        // Configurar título
        supportActionBar?.title = "Pedidos Delivery"

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = PedidoAdapter(
            onItemClick = { pedido ->
                Toast.makeText(this, "Pedido #${pedido.id}: ${pedido.nombreCliente}", Toast.LENGTH_SHORT).show()
            },
            onSyncClick = { pedido ->
                viewModel.sincronizarPedido(pedido)
            }
        )

        binding.recyclerViewPedidos.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewModel.allPedidos.observe(this) { pedidos ->
            adapter.submitList(pedidos)

            if (pedidos.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.recyclerViewPedidos.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.recyclerViewPedidos.visibility = View.VISIBLE
            }

            LogUtils.logInfo("Lista actualizada: ${pedidos.size} pedidos")
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.operationStatus.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(this, it.getOrNull(), Toast.LENGTH_SHORT).show()
                    LogUtils.logInfo("Operación exitosa: ${it.getOrNull()}")
                } else {
                    Toast.makeText(
                        this,
                        "Error: ${it.exceptionOrNull()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    LogUtils.logError("Error: ${it.exceptionOrNull()?.message}")
                }
                viewModel.clearOperationStatus()
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAgregarPedido.setOnClickListener {
            val intent = Intent(this, AgregarPedidoActivity::class.java)
            startActivity(intent)
            LogUtils.logInfo("Navegando a AgregarPedidoActivity")
        }

        binding.btnSincronizar.setOnClickListener {
            LogUtils.logInfo("Iniciando sincronización masiva")
            viewModel.sincronizarTodosPendientes()
        }
    }

    override fun onResume() {
        super.onResume()
        LogUtils.logInfo("MainActivity en primer plano")
    }
}