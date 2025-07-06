package com.asparrin.carlos.estiloya.ui.carrito

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.databinding.ActivityCarritoBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity
import com.asparrin.carlos.estiloya.ui.productos.ProductosActivity
import com.asparrin.carlos.estiloya.ui.components.CarritoAdapter
import com.asparrin.carlos.estiloya.viewModel.CarritoViewModel

class CarritoActivity : BaseActivity() {

    private lateinit var binding: ActivityCarritoBinding
    private lateinit var viewModel: CarritoViewModel
    private lateinit var adapter: CarritoAdapter

    override fun getLayoutResourceId(): Int = R.layout.activity_carrito

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Asociar el binding al contenido inyectado
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityCarritoBinding.bind(child)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        // Cargar carrito al iniciar
        viewModel.cargarCarrito(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[CarritoViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = CarritoAdapter(
            items = emptyList(),
            onCantidadChange = { productoId, cantidad ->
                viewModel.actualizarCantidad(this, productoId, cantidad)
            },
            onEliminar = { itemId ->
                mostrarDialogoConfirmarEliminacion(itemId)
            }
        )

        binding.rvCarrito.apply {
            layoutManager = LinearLayoutManager(this@CarritoActivity)
            adapter = this@CarritoActivity.adapter
        }
    }

    private fun setupObservers() {
        // Observar items del carrito
        viewModel.carritoItems.observe(this) { items ->
            adapter.updateData(items)
            actualizarEstadoCarrito(items.isEmpty())
            
            // Cargar resumen de compra cuando hay items en el carrito
            if (items.isNotEmpty()) {
                viewModel.cargarResumenCompra(this)
            }
        }

        // Observar resumen de compra
        viewModel.resumenCompra.observe(this) { resumen ->
            resumen?.data?.let { data ->
                binding.textSubtotal.text = "S/ ${"%.2f".format(data.subtotal)}"
                binding.textDescuento.text = "S/ ${"%.2f".format(data.descuento)}"
                binding.textTotal.text = "S/ ${"%.2f".format(data.total)}"
                binding.textCantidadItems.text = "${data.cantidadItems} items"
            }
        }

        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observar mensajes de error
        viewModel.error.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.limpiarMensajes()
            }
        }

        // Observar mensajes de éxito
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.limpiarMensajes()
                
                // Si es un mensaje de compra finalizada, cerrar la actividad
                if (it.contains("finalizada")) {
                    finish()
                }
            }
        }
    }

    private fun setupListeners() {
        // Botón ir a productos
        binding.btnIrProductos.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botón finalizar compra
        binding.btnFinalizarCompra.setOnClickListener {
            mostrarDialogoConfirmarCompra()
        }
        
        // Botón vaciar carrito
        binding.btnVaciarCarrito.setOnClickListener {
            mostrarDialogoVaciarCarrito()
        }
    }

    private fun actualizarEstadoCarrito(estaVacio: Boolean) {
        if (estaVacio) {
            binding.layoutCarritoVacio.visibility = View.VISIBLE
            binding.rvCarrito.visibility = View.GONE
            binding.layoutResumen.visibility = View.GONE
            binding.btnVaciarCarrito.visibility = View.GONE
        } else {
            binding.layoutCarritoVacio.visibility = View.GONE
            binding.rvCarrito.visibility = View.VISIBLE
            binding.layoutResumen.visibility = View.VISIBLE
            binding.btnVaciarCarrito.visibility = View.VISIBLE
        }
    }

    private fun mostrarDialogoConfirmarEliminacion(itemId: Long) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que quieres eliminar este producto del carrito?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarProducto(this, itemId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoConfirmarCompra() {
        val resumen = viewModel.resumenCompra.value
        if (resumen?.data != null) {
            AlertDialog.Builder(this)
                .setTitle("Finalizar Compra")
                .setMessage("¿Estás seguro de que quieres finalizar la compra por S/ ${"%.2f".format(resumen.data.total)}?")
                .setPositiveButton("Finalizar") { _, _ ->
                    viewModel.finalizarCompra(this)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun mostrarDialogoVaciarCarrito() {
        AlertDialog.Builder(this)
            .setTitle("Vaciar Carrito")
            .setMessage("¿Estás seguro de que quieres vaciar el carrito?")
            .setPositiveButton("Vaciar") { _, _ ->
                viewModel.vaciarCarrito(this)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Recargar carrito al volver a la pantalla
        viewModel.cargarCarrito(this)
    }
} 