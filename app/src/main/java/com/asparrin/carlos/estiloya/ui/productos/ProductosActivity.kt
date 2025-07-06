package com.asparrin.carlos.estiloya.ui.productos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.api.ApiClient
import com.asparrin.carlos.estiloya.api.CategoriaService
import com.asparrin.carlos.estiloya.api.ProductService
import com.asparrin.carlos.estiloya.data.model.Categoria
import com.asparrin.carlos.estiloya.data.model.Producto
import com.asparrin.carlos.estiloya.data.model.PaginatedResponse
import com.asparrin.carlos.estiloya.databinding.ActivityProductosBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity
import com.asparrin.carlos.estiloya.ui.components.CantidadDialog
import androidx.lifecycle.ViewModelProvider
import com.asparrin.carlos.estiloya.viewModel.CarritoViewModel
import com.asparrin.carlos.estiloya.viewModel.ProductosViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductosActivity : BaseActivity() {

    private lateinit var binding: ActivityProductosBinding
    private lateinit var adapter: ProductosAdapter
    private var productosOriginales: List<Producto> = emptyList()
    private var productosFiltrados: List<Producto> = emptyList()
    private var productosBuscados: List<Producto> = emptyList()
    private var categorias: List<Categoria> = emptyList()

    // Estados de filtros rápidos
    private var filtroMenos50Activo = false
    private var filtroPocasUnidadesActivo = false
    private var filtroOfertaActivo = false
    private var filtroNuevosActivo = false
    
    // Variable para marcar si hay filtros pendientes de aplicar
    private var filtrosPendientes = false

    // Servicios
    private val categoriaService: CategoriaService by lazy {
        ApiClient.createCategoriaService(this)
    }
    
    // ViewModels
    private lateinit var carritoViewModel: CarritoViewModel
    private lateinit var productosViewModel: ProductosViewModel

    override fun getLayoutResourceId(): Int = R.layout.activity_productos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Asociar el binding al contenido inyectado
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityProductosBinding.bind(child)

        // Inicializar ViewModels
        carritoViewModel = ViewModelProvider(this)[CarritoViewModel::class.java]
        productosViewModel = ViewModelProvider(this)[ProductosViewModel::class.java]

        setupRecyclerView()
        setupBuscador()
        setupFiltrosDesplegables()
        procesarParametrosEntrada()
        setupFiltrosRapidos()
        cargarCategorias()
        fetchProductosFromApi()
        
        setupClickListeners()
        setupObservers()
        
        // Cargar productos
        productosViewModel.cargarProductos(this)
        
        // Cargar carrito para actualizar badge
        carritoViewModel.cargarCarrito(this)
    }

    private fun procesarParametrosEntrada() {
        intent.extras?.let { extras ->
            if (extras.getBoolean("mostrarOfertas", false)) {
                filtroOfertaActivo = true
                filtrosPendientes = true
            }
            if (extras.getBoolean("mostrarNuevos", false)) {
                filtroNuevosActivo = true
                filtrosPendientes = true
            }
            if (extras.getBoolean("menosDe50", false)) {
                filtroMenos50Activo = true
                binding.etPrecioMax.setText("50")
                filtrosPendientes = true
            }
            if (extras.getBoolean("pocasUnidades", false)) {
                filtroPocasUnidadesActivo = true
                filtrosPendientes = true
            }
            val categoriaNombre = extras.getString("categoria")
            if (!categoriaNombre.isNullOrEmpty()) {
                val categoriaIndex =
                    categorias.indexOfFirst { categoria -> categoria.nombre == categoriaNombre }
                if (categoriaIndex != -1) {
                    // Marcar que hay filtro de categoría pendiente
                    filtrosPendientes = true
                }
            }
        }
        
        // Si hay cualquier parámetro de entrada, marcar filtros pendientes
        if (intent.extras != null && intent.extras!!.size() > 0) {
            filtrosPendientes = true
        }
    }

    private fun setupRecyclerView() {
        // Configurar GridLayoutManager con 2 columnas
        val layoutManager = GridLayoutManager(this, 2)
        binding.rvProductos.layoutManager = layoutManager
        
        // Configurar el adaptador con funcionalidad de carrito
        adapter = ProductosAdapter(
            productos = emptyList(),
            onProductoClick = { producto ->
                // Navegar al detalle del producto
                val intent = Intent(this, DetalleProductoActivity::class.java)
                intent.putExtra("producto", producto)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                mostrarDialogoCantidad(producto)
            }
        )
        
        binding.rvProductos.adapter = adapter
    }

    private fun setupBuscador() {
        // Configurar búsqueda en tiempo real
        binding.etBuscarProducto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                buscarProductos(s.toString())
            }
        })

        // Configurar botón de búsqueda
        binding.btnBuscarProducto.setOnClickListener {
            val query = binding.etBuscarProducto.text.toString()
            buscarProductos(query)
        }
    }

    private fun buscarProductos(query: String) {
        if (query.isEmpty()) {
            // Si no hay búsqueda, mostrar productos filtrados
            productosBuscados = productosFiltrados
        } else {
            // Filtrar por nombre del producto (búsqueda insensible a mayúsculas/minúsculas)
            productosBuscados = productosFiltrados.filter { producto ->
                producto.nombre.contains(query, ignoreCase = true)
            }
        }
        
        actualizarAdapter()
        
        // Mostrar mensaje de resultados
        if (productosBuscados.isEmpty() && query.isNotEmpty()) {
            Toast.makeText(this, "No se encontraron productos con '$query'", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFiltrosDesplegables() {
        // Configurar spinner de tipos inmediatamente
        val tipos = listOf(getString(R.string.filter_all_types), "Polo", "Polera")
        val adapterTipo = ArrayAdapter(this, R.layout.spinner_item_filter, tipos)
        adapterTipo.setDropDownViewResource(R.layout.spinner_dropdown_item_filter)
        binding.spinnerTipo.adapter = adapterTipo
    }

    private fun cargarCategorias() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = categoriaService.getCategorias()
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        categorias = response.body() ?: emptyList()
                        setupSpinnerCategorias()
                    } else {
                        // Fallback a categorías mock
                        categorias = listOf(
                            Categoria(1, "Anime"),
                            Categoria(2, "Comics"),
                            Categoria(3, "Película"),
                            Categoria(4, "Serie"),
                            Categoria(5, "Videojuegos"),
                            Categoria(6, "Música"),
                            Categoria(7, "Deporte")
                        )
                        setupSpinnerCategorias()
                        Toast.makeText(this@ProductosActivity, getString(R.string.error_loading_categories_products), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Fallback a categorías mock
                    categorias = listOf(
                        Categoria(1, "Anime"),
                        Categoria(2, "Comics"),
                        Categoria(3, "Película"),
                        Categoria(4, "Serie"),
                        Categoria(5, "Videojuegos"),
                        Categoria(6, "Música"),
                        Categoria(7, "Deporte")
                    )
                    setupSpinnerCategorias()
                    Toast.makeText(this@ProductosActivity, getString(R.string.network_error_categories_products), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSpinnerCategorias() {
        val categoriasList = listOf(getString(R.string.filter_all_categories)) + categorias.map { it.nombre }
        val adapterCategoria = ArrayAdapter(this, R.layout.spinner_item_filter, categoriasList)
        adapterCategoria.setDropDownViewResource(R.layout.spinner_dropdown_item_filter)
        binding.spinnerCategoria.adapter = adapterCategoria
        
        // Configurar selección de categoría si viene del intent
        intent.extras?.getString("categoria")?.let { categoriaNombre ->
            val categoriaIndex = categorias.indexOfFirst { categoria -> categoria.nombre == categoriaNombre }
            if (categoriaIndex != -1) {
                binding.spinnerCategoria.setSelection(categoriaIndex + 1)
            }
        }
        
        // Aplicar filtros pendientes si los hay (sin importar si el panel está visible)
        if (filtrosPendientes) {
            aplicarFiltros()
            filtrosPendientes = false
        }
    }

    private fun setupFiltrosRapidos() {
        binding.checkMenos50.setOnCheckedChangeListener { _, isChecked ->
            filtroMenos50Activo = isChecked
            if (isChecked) binding.etPrecioMax.setText("50")
            // No aplicar filtros automáticamente aquí
        }
        binding.checkPocasUnidades.setOnCheckedChangeListener { _, isChecked ->
            filtroPocasUnidadesActivo = isChecked
            // No aplicar filtros automáticamente aquí
        }
        binding.checkOferta.setOnCheckedChangeListener { _, isChecked ->
            filtroOfertaActivo = isChecked
            // No aplicar filtros automáticamente aquí
        }
        binding.checkNuevos.setOnCheckedChangeListener { _, isChecked ->
            filtroNuevosActivo = isChecked
            // No aplicar filtros automáticamente aquí
        }
        
        // Actualizar estado visual de los CheckBox según los filtros activos
        binding.checkMenos50.isChecked = filtroMenos50Activo
        binding.checkPocasUnidades.isChecked = filtroPocasUnidadesActivo
        binding.checkOferta.isChecked = filtroOfertaActivo
        binding.checkNuevos.isChecked = filtroNuevosActivo
    }

    private fun limpiarFiltros() {
        // Limpiar campos de precio
        binding.etPrecioMin.setText("")
        binding.etPrecioMax.setText("")
        // Resetear spinners
        binding.spinnerCategoria.setSelection(0)
        binding.spinnerTipo.setSelection(0)
        // Desactivar filtros rápidos
        binding.checkMenos50.isChecked = false
        binding.checkPocasUnidades.isChecked = false
        binding.checkOferta.isChecked = false
        binding.checkNuevos.isChecked = false
        filtroMenos50Activo = false
        filtroPocasUnidadesActivo = false
        filtroOfertaActivo = false
        filtroNuevosActivo = false
        // Aplicar filtros (que ahora no tendrán restricciones)
        aplicarFiltros()
        Toast.makeText(this, getString(R.string.filter_cleared), Toast.LENGTH_SHORT).show()
    }

    private fun fetchProductosFromApi() {
        binding.progressBar.visibility = View.VISIBLE
        val service = ApiClient.createProductService(this)
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.listAll()
                
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val paginatedResponse = response.body()
                        if (paginatedResponse != null && paginatedResponse.content.isNotEmpty()) {
                            productosOriginales = paginatedResponse.content
                            productosFiltrados = productosOriginales
                            productosBuscados = productosFiltrados
                            actualizarAdapter()
                            
                            // Aplicar filtros pendientes después de cargar productos
                            if (filtrosPendientes) {
                                aplicarFiltros()
                                filtrosPendientes = false
                            }
                            
                            Toast.makeText(
                                this@ProductosActivity,
                                "Productos cargados: ${paginatedResponse.content.size}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@ProductosActivity,
                                "No se encontraron productos",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        val errorMessage = "Error del servidor: ${response.code()} - ${response.message()}"
                        Toast.makeText(
                            this@ProductosActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                        // Log del error para debug
                        println("Error HTTP: ${response.code()} - ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ProductosActivity,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    // Log del error para debug
                    println("Error de excepción: ${e.message}")
                }
            }
        }
    }

    private fun aplicarFiltros() {
        val precioMinStr = binding.etPrecioMin.text.toString()
        val precioMaxStr = binding.etPrecioMax.text.toString()
        
        // Verificar que los spinners estén configurados antes de acceder a sus valores
        val categoriaSeleccionada = if (binding.spinnerCategoria.adapter != null) {
            binding.spinnerCategoria.selectedItem?.toString() ?: getString(R.string.filter_all_categories)
        } else {
            getString(R.string.filter_all_categories)
        }
        
        val tipoSeleccionado = if (binding.spinnerTipo.adapter != null) {
            binding.spinnerTipo.selectedItem?.toString() ?: getString(R.string.filter_all_types)
        } else {
            getString(R.string.filter_all_types)
        }

        productosFiltrados = productosOriginales.filter { producto ->
            var cumpleFiltros = true

            // Filtro por precio mínimo
            if (precioMinStr.isNotEmpty()) {
                val precioMin = precioMinStr.toDoubleOrNull() ?: 0.0
                val precioFinal = producto.precioConDescuento.toDouble()
                cumpleFiltros = cumpleFiltros && precioFinal >= precioMin
            }

            // Filtro por precio máximo
            if (precioMaxStr.isNotEmpty()) {
                val precioMax = precioMaxStr.toDoubleOrNull() ?: Double.MAX_VALUE
                val precioFinal = producto.precioConDescuento.toDouble()
                cumpleFiltros = cumpleFiltros && precioFinal <= precioMax
            }

            // Filtro por categoría
            if (categoriaSeleccionada != getString(R.string.filter_all_categories)) {
                cumpleFiltros = cumpleFiltros && producto.categoriaNombre == categoriaSeleccionada
            }

            // Filtro por tipo
            if (tipoSeleccionado != getString(R.string.filter_all_types)) {
                cumpleFiltros = cumpleFiltros && producto.tipo == tipoSeleccionado
            }

            // Filtro de ofertas (productos con descuento)
            if (filtroOfertaActivo) {
                cumpleFiltros = cumpleFiltros && producto.descuentoPorcentajeCalculado > 0
            }

            // Filtro de productos nuevos (últimos 3 para datos mock)
            if (filtroNuevosActivo) {
                val indice = productosOriginales.indexOf(producto)
                cumpleFiltros = cumpleFiltros && indice >= productosOriginales.size - 3
            }

            // Filtro de pocas unidades (stock bajo)
            if (filtroPocasUnidadesActivo) {
                cumpleFiltros = cumpleFiltros && producto.stock <= 5
            }

            cumpleFiltros
        }

        productosBuscados = productosFiltrados
        actualizarAdapter()
    }

    private fun actualizarAdapter() {
        adapter.updateData(productosBuscados)
    }
    
    /**
     * Mostrar diálogo para seleccionar cantidad del producto
     */
    private fun mostrarDialogoCantidad(producto: Producto) {
        val cantidadDialog = CantidadDialog(this, producto) { cantidad ->
            carritoViewModel.agregarProducto(this, producto.id, cantidad)
        }
        cantidadDialog.mostrar()
    }
    
    /**
     * Actualizar badge del carrito
     */
    private fun actualizarBadgeCarrito(totalItems: Int) {
        if (totalItems > 0) {
            binding.badgeCarrito.apply {
                text = totalItems.toString()
                visibility = View.VISIBLE
            }
        } else {
            binding.badgeCarrito.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        // Botón mostrar/ocultar filtros
        binding.btnMostrarFiltros.setOnClickListener {
            toggleFiltros()
        }

        // Botón aplicar filtros
        binding.btnAplicarFiltros.setOnClickListener {
            aplicarFiltros()
        }

        // Botón limpiar filtros
        binding.btnLimpiarFiltros.setOnClickListener {
            limpiarFiltros()
        }
        
        // FAB del carrito
        binding.fabCarrito.setOnClickListener {
            val intent = Intent(this, com.asparrin.carlos.estiloya.ui.carrito.CarritoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun toggleFiltros() {
        if (binding.panelFiltros.visibility == View.VISIBLE) {
            binding.panelFiltros.visibility = View.GONE
            binding.btnMostrarFiltros.text = getString(R.string.filter_show)
        } else {
            binding.panelFiltros.visibility = View.VISIBLE
            binding.btnMostrarFiltros.text = getString(R.string.filter_hide)
            
            // Actualizar estado visual de los CheckBox cuando se muestra el panel
            binding.checkMenos50.isChecked = filtroMenos50Activo
            binding.checkPocasUnidades.isChecked = filtroPocasUnidadesActivo
            binding.checkOferta.isChecked = filtroOfertaActivo
            binding.checkNuevos.isChecked = filtroNuevosActivo
        }
    }

    private fun setupObservers() {
        // Observar productos
        productosViewModel.productos.observe(this) { productos ->
            productosOriginales = productos
            aplicarFiltros()
        }

        // Observar estado de carga
        productosViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observar errores
        productosViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
        
        // Observar carrito para actualizar badge
        carritoViewModel.carritoItems.observe(this) { items ->
            val totalItems = items.sumOf { it.cantidad }
            actualizarBadgeCarrito(totalItems)
        }
        
        // Observar mensajes de éxito del carrito
        carritoViewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                carritoViewModel.limpiarMensajes()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar carrito al volver a la pantalla
        carritoViewModel.cargarCarrito(this)
    }
}
