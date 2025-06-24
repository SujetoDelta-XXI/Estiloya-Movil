package com.asparrin.carlos.estiloya.ui.productos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.data.mock.MockProducto
import com.asparrin.carlos.estiloya.data.model.Producto
import com.asparrin.carlos.estiloya.databinding.ActivityProductosBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity

class ProductosActivity : BaseActivity() {

    private lateinit var binding: ActivityProductosBinding
    private lateinit var adapter: ProductosAdapter
    private var productosOriginales: List<Producto> = emptyList()
    private var productosFiltrados: List<Producto> = emptyList()

    override fun getLayoutResourceId(): Int = R.layout.activity_productos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Asociar el binding al contenido inyectado
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityProductosBinding.bind(child)

        setupRecyclerView()
        setupFiltros()
        loadProductos()
    }

    private fun setupRecyclerView() {
        // Configurar GridLayoutManager con 2 columnas
        val layoutManager = GridLayoutManager(this, 2)
        binding.rvProductos.layoutManager = layoutManager
        
        // Configurar el adaptador
        adapter = ProductosAdapter(
            productos = emptyList(),
            onProductoClick = { producto ->
                // Navegar al detalle del producto
                val intent = Intent(this, DetalleProductoActivity::class.java)
                intent.putExtra("producto", producto)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                Toast.makeText(this, "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            },
            onComprarClick = { producto ->
                Toast.makeText(this, "Comprando: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.rvProductos.adapter = adapter
    }

    private fun setupFiltros() {
        // Configurar spinner de categorías
        val categorias = listOf(getString(R.string.filter_all_categories), "Anime", "Comics", "Película", "Serie", "Videojuegos", "Música", "Deporte")
        val adapterCategoria = ArrayAdapter(this, R.layout.spinner_item_filter, categorias)
        adapterCategoria.setDropDownViewResource(R.layout.spinner_dropdown_item_filter)
        binding.spinnerCategoria.adapter = adapterCategoria

        // Configurar spinner de tipos
        val tipos = listOf(getString(R.string.filter_all_types), "Polo", "Polera")
        val adapterTipo = ArrayAdapter(this, R.layout.spinner_item_filter, tipos)
        adapterTipo.setDropDownViewResource(R.layout.spinner_dropdown_item_filter)
        binding.spinnerTipo.adapter = adapterTipo

        // Configurar botón de aplicar filtros
        binding.btnAplicarFiltros.setOnClickListener {
            aplicarFiltros()
        }
    }

    private fun loadProductos() {
        productosOriginales = MockProducto.getListaProductos()
        productosFiltrados = productosOriginales
        actualizarAdapter()
    }

    private fun aplicarFiltros() {
        val precioMinStr = binding.etPrecioMin.text.toString()
        val precioMaxStr = binding.etPrecioMax.text.toString()
        val categoriaSeleccionada = binding.spinnerCategoria.selectedItem.toString()
        val tipoSeleccionado = binding.spinnerTipo.selectedItem.toString()

        productosFiltrados = productosOriginales.filter { producto ->
            var cumpleFiltros = true

            // Filtro por precio mínimo
            if (precioMinStr.isNotEmpty()) {
                val precioMin = precioMinStr.toDoubleOrNull() ?: 0.0
                val precioFinal = if (producto.descuento > 0) {
                    producto.precio * (1 - producto.descuento / 100.0)
                } else {
                    producto.precio
                }
                cumpleFiltros = cumpleFiltros && precioFinal >= precioMin
            }

            // Filtro por precio máximo
            if (precioMaxStr.isNotEmpty()) {
                val precioMax = precioMaxStr.toDoubleOrNull() ?: Double.MAX_VALUE
                val precioFinal = if (producto.descuento > 0) {
                    producto.precio * (1 - producto.descuento / 100.0)
                } else {
                    producto.precio
                }
                cumpleFiltros = cumpleFiltros && precioFinal <= precioMax
            }

            // Filtro por categoría
            if (categoriaSeleccionada != getString(R.string.filter_all_categories)) {
                cumpleFiltros = cumpleFiltros && producto.categoria == categoriaSeleccionada
            }

            // Filtro por tipo
            if (tipoSeleccionado != getString(R.string.filter_all_types)) {
                cumpleFiltros = cumpleFiltros && producto.tipo == tipoSeleccionado
            }

            cumpleFiltros
        }

        actualizarAdapter()
        
        if (productosFiltrados.isEmpty()) {
            Toast.makeText(this, getString(R.string.filter_no_results), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.filter_results_found, productosFiltrados.size), Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarAdapter() {
        adapter = ProductosAdapter(
            productos = productosFiltrados,
            onProductoClick = { producto ->
                // Navegar al detalle del producto
                val intent = Intent(this, DetalleProductoActivity::class.java)
                intent.putExtra("producto", producto)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                Toast.makeText(this, "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            },
            onComprarClick = { producto ->
                Toast.makeText(this, "Comprando: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvProductos.adapter = adapter
    }
}
