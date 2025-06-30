package com.asparrin.carlos.estiloya.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.api.ApiClient
import com.asparrin.carlos.estiloya.api.CategoriaService
import com.asparrin.carlos.estiloya.data.model.Categoria
import com.asparrin.carlos.estiloya.databinding.ActivityHomeBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity
import com.asparrin.carlos.estiloya.ui.components.BannersAdapter
import com.asparrin.carlos.estiloya.ui.components.CategoriasAdapter
import com.asparrin.carlos.estiloya.ui.components.ProductAdapter
import com.asparrin.carlos.estiloya.ui.productos.DetalleProductoActivity
import com.asparrin.carlos.estiloya.ui.productos.ProductosActivity
import com.asparrin.carlos.estiloya.viewModel.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Clases de datos para el Home
data class Banner(
    val id: Int,
    val imagen: Int,
    val titulo: String,
    val descripcion: String
)

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var bannersAdapter: BannersAdapter
    private lateinit var categoriasAdapter: CategoriasAdapter
    
    // Adaptadores para las diferentes secciones de productos
    private lateinit var ofertasDelDiaAdapter: ProductAdapter
    private lateinit var ofertasDeLaSemanaAdapter: ProductAdapter
    private lateinit var masVendidosAdapter: ProductAdapter
    private lateinit var nuevosProductosAdapter: ProductAdapter
    private lateinit var productosFiltradosAdapter: ProductAdapter
    
    // ViewModel
    private val homeViewModel: HomeViewModel by lazy {
        androidx.lifecycle.ViewModelProvider(this)[HomeViewModel::class.java]
    }

    // Servicio de categorías
    private val categoriaService: CategoriaService by lazy {
        ApiClient.retrofit.create(CategoriaService::class.java)
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Asociar el binding al contenido inyectado
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityHomeBinding.bind(child)

        setupBanners()
        setupCategorias()
        setupProductAdapters()
        setupClickListeners()
        setupObservers()
        
        // Cargar datos
        homeViewModel.cargarTodosLosDatos()
        cargarCategorias()
    }

    private fun setupBanners() {
        val banners = listOf(
            Banner(1, R.drawable.imagen_background, "Ofertas Especiales", "Descuentos hasta 50%"),
            Banner(2, R.drawable.imagen_background, "Nuevos Productos", "Descubre las últimas tendencias"),
            Banner(3, R.drawable.imagen_background, "Envío Gratis", "En compras superiores a S/ 100")
        )

        bannersAdapter = BannersAdapter(banners) { banner ->
            Toast.makeText(this, "Banner clickeado: ${banner.titulo}", Toast.LENGTH_SHORT).show()
        }

        binding.viewPagerBanners.apply {
            adapter = bannersAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
    }

    private fun setupCategorias() {
        // Configurar el adaptador de categorías
        categoriasAdapter = CategoriasAdapter(emptyList()) { categoria ->
            // Navegar a productos con filtro de categoría
            val intent = Intent(this, ProductosActivity::class.java)
            intent.putExtra("categoria", categoria.nombre)
            startActivity(intent)
        }

        binding.rvCategorias.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriasAdapter
        }
    }

    private fun cargarCategorias() {
        binding.progressCategorias.visibility = View.VISIBLE
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = categoriaService.getCategorias()
                
                withContext(Dispatchers.Main) {
                    binding.progressCategorias.visibility = View.GONE
                    
                    if (response.isSuccessful) {
                        val categorias = response.body() ?: emptyList()
                        categoriasAdapter.updateData(categorias)
                    } else {
                        // Fallback a categorías mock
                        val categoriasMock = listOf(
                            Categoria(1, "Anime"),
                            Categoria(2, "Comics"),
                            Categoria(3, "Películas"),
                            Categoria(4, "Series"),
                            Categoria(5, "Videojuegos")
                        )
                        categoriasAdapter.updateData(categoriasMock)
                        Toast.makeText(this@HomeActivity, getString(R.string.error_loading_categories), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressCategorias.visibility = View.GONE
                    // Fallback a categorías mock
                    val categoriasMock = listOf(
                        Categoria(1, "Anime"),
                        Categoria(2, "Comics"),
                        Categoria(3, "Películas"),
                        Categoria(4, "Series"),
                        Categoria(5, "Videojuegos")
                    )
                    categoriasAdapter.updateData(categoriasMock)
                    Toast.makeText(this@HomeActivity, getString(R.string.network_error_categories), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupProductAdapters() {
        // Configurar adaptador para Ofertas del Día
        ofertasDelDiaAdapter = ProductAdapter(
            productos = emptyList(),
            onProductoClick = { producto ->
                val intent = Intent(this, DetalleProductoActivity::class.java)
                intent.putExtra("producto", producto)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                Toast.makeText(this, "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvOfertasDelDia.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = ofertasDelDiaAdapter
        }

        // Configurar adaptador para Ofertas de la Semana
        ofertasDeLaSemanaAdapter = ProductAdapter(
            productos = emptyList(),
            onProductoClick = { producto ->
                val intent = Intent(this, DetalleProductoActivity::class.java)
                intent.putExtra("producto", producto)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                Toast.makeText(this, "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvOfertasDeLaSemana.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = ofertasDeLaSemanaAdapter
        }

        // Configurar adaptador para Más Vendidos
        masVendidosAdapter = ProductAdapter(
            productos = emptyList(),
            onProductoClick = { producto ->
                val intent = Intent(this, DetalleProductoActivity::class.java)
                intent.putExtra("producto", producto)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                Toast.makeText(this, "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvMasVendidos.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = masVendidosAdapter
        }

        // Configurar adaptador para Nuevos Productos
        nuevosProductosAdapter = ProductAdapter(
            productos = emptyList(),
            onProductoClick = { producto ->
                val intent = Intent(this, DetalleProductoActivity::class.java)
                intent.putExtra("producto", producto)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                Toast.makeText(this, "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvNuevosProductos.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = nuevosProductosAdapter
        }

        // Configurar adaptador para Productos Filtrados
        productosFiltradosAdapter = ProductAdapter(
            productos = emptyList(),
            onProductoClick = { producto ->
                val intent = Intent(this, DetalleProductoActivity::class.java)
                intent.putExtra("producto", producto)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                Toast.makeText(this, "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupClickListeners() {
        // Menos de 50
        binding.btnMenosDe50.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            intent.putExtra("menosDe50", true)
            startActivity(intent)
        }
        // Pocas Unidades
        binding.btnPocasUnidades.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            intent.putExtra("pocasUnidades", true)
            startActivity(intent)
        }
        // Oferta
        binding.btnOfertas.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            intent.putExtra("mostrarOfertas", true)
            startActivity(intent)
        }
        // Nuevos
        binding.btnNuevos.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            intent.putExtra("mostrarNuevos", true)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        // Observar ofertas del día
        homeViewModel.ofertasDelDia.observe(this) { productos ->
            ofertasDelDiaAdapter.updateData(productos)
        }

        homeViewModel.isLoadingOfertasDia.observe(this) { isLoading ->
            binding.progressOfertasDelDia.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.errorOfertasDia.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // Observar ofertas de la semana
        homeViewModel.ofertasDeLaSemana.observe(this) { productos ->
            ofertasDeLaSemanaAdapter.updateData(productos)
        }

        homeViewModel.isLoadingOfertasSemana.observe(this) { isLoading ->
            binding.progressOfertasDeLaSemana.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.errorOfertasSemana.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // Observar productos más vendidos
        homeViewModel.productosMasVendidos.observe(this) { productos ->
            masVendidosAdapter.updateData(productos)
        }

        homeViewModel.isLoadingMasVendidos.observe(this) { isLoading ->
            binding.progressMasVendidos.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.errorMasVendidos.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // Observar nuevos productos
        homeViewModel.nuevosProductos.observe(this) { productos ->
            nuevosProductosAdapter.updateData(productos)
        }

        homeViewModel.isLoadingNuevos.observe(this) { isLoading ->
            binding.progressNuevosProductos.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.errorNuevos.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarProductosFiltrados(productos: List<com.asparrin.carlos.estiloya.data.model.Producto>) {
        // Actualizar el adaptador con los productos filtrados
        productosFiltradosAdapter.updateData(productos)

        // Actualizar el título según los filtros activos
        val titulo = when {
            homeViewModel.filtroDescuentoActivo.value == true && homeViewModel.filtroNuevoActivo.value == true ->
                getString(R.string.filtered_products_discount_and_new)
            homeViewModel.filtroDescuentoActivo.value == true ->
                getString(R.string.filtered_products_with_discount)
            homeViewModel.filtroNuevoActivo.value == true ->
                getString(R.string.filtered_products_new)
            else -> getString(R.string.filtered_products)
        }

        // Mostrar mensaje informativo
        val mensaje = when {
            homeViewModel.filtroDescuentoActivo.value == true && homeViewModel.filtroNuevoActivo.value == true ->
                getString(R.string.showing_discount_and_new_products)
            homeViewModel.filtroDescuentoActivo.value == true ->
                getString(R.string.showing_discount_products)
            homeViewModel.filtroNuevoActivo.value == true ->
                getString(R.string.showing_new_products)
            else -> ""
        }
        
        if (mensaje.isNotEmpty()) {
            Toast.makeText(this, "$mensaje (${productos.size} productos)", Toast.LENGTH_SHORT).show()
        }
    }
}
