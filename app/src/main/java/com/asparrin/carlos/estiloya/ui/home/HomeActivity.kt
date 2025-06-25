package com.asparrin.carlos.estiloya.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.databinding.ActivityHomeBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity
import com.asparrin.carlos.estiloya.ui.components.Banner
import com.asparrin.carlos.estiloya.ui.components.BannersAdapter
import com.asparrin.carlos.estiloya.ui.components.Categoria
import com.asparrin.carlos.estiloya.ui.components.CategoriasAdapter
import com.asparrin.carlos.estiloya.ui.productos.ProductosActivity
import com.asparrin.carlos.estiloya.ui.productos.ProductosAdapter
import com.google.android.material.tabs.TabLayoutMediator

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var bannersAdapter: BannersAdapter
    private lateinit var categoriasAdapter: CategoriasAdapter
    private lateinit var destacadosAdapter: ProductosAdapter
    private lateinit var masVendidosAdapter: ProductosAdapter

    override fun getLayoutResourceId(): Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Asociar el binding al contenido inyectado
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityHomeBinding.bind(child)

        setupToolbar()
        setupSearchView()
        setupBanners()
        setupQuickActions()
        setupCategorias()
        setupProductosDestacados()
        setupMasVendidos()
        setupFloatingActionButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupSearchView() {
        // Mostrar/ocultar barra de búsqueda
        binding.btnSearch.setOnClickListener {
            if (binding.searchContainer.visibility == View.VISIBLE) {
                binding.searchContainer.visibility = View.GONE
            } else {
                binding.searchContainer.visibility = View.VISIBLE
                binding.etSearch.requestFocus()
            }
        }

        // Acción de búsqueda
        binding.btnSearchAction.setOnClickListener {
            val query = binding.etSearch.text.toString()
            if (query.isNotEmpty()) {
                // Aquí implementarías la búsqueda
                Toast.makeText(this, "Buscando: $query", Toast.LENGTH_SHORT).show()
            }
        }

        // Abrir menú de usuario
        binding.btnProfile.setOnClickListener {
            // Aquí implementarías la apertura del menú de usuario
            Toast.makeText(this, "Menú de usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBanners() {
        val banners = listOf(
            Banner(1, R.drawable.imagen_background, "Oferta Especial", "Hasta 50% de descuento"),
            Banner(2, R.drawable.imagen_background, "Nuevos Productos", "Descubre las últimas tendencias"),
            Banner(3, R.drawable.imagen_background, "Envío Gratis", "En compras superiores a $50")
        )

        bannersAdapter = BannersAdapter(banners) { banner ->
            Toast.makeText(this, "Banner: ${banner.titulo}", Toast.LENGTH_SHORT).show()
        }

        binding.viewPagerBanners.adapter = bannersAdapter

        // Configurar indicador de páginas
        TabLayoutMediator(binding.tabLayoutBanners, binding.viewPagerBanners) { _, _ ->
            // No necesitamos hacer nada aquí
        }.attach()

        // Auto-scroll de banners
        binding.viewPagerBanners.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Aquí podrías implementar lógica adicional
            }
        })
    }

    private fun setupQuickActions() {
        binding.btnCategorias.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        binding.btnOfertas.setOnClickListener {
            Toast.makeText(this, "Ver ofertas", Toast.LENGTH_SHORT).show()
        }

        binding.btnNuevos.setOnClickListener {
            Toast.makeText(this, "Ver nuevos productos", Toast.LENGTH_SHORT).show()
        }

        binding.btnFavoritos.setOnClickListener {
            Toast.makeText(this, "Ver favoritos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCategorias() {
        val categorias = listOf(
            Categoria(1, "Ropa", R.drawable.ic_categoria, R.drawable.bg_circle_primary),
            Categoria(2, "Zapatos", R.drawable.ic_categoria, R.drawable.bg_circle_accent),
            Categoria(3, "Accesorios", R.drawable.ic_categoria, R.drawable.bg_circle_success),
            Categoria(4, "Bolsos", R.drawable.ic_categoria, R.drawable.bg_circle_warning),
            Categoria(5, "Joyas", R.drawable.ic_categoria, R.drawable.bg_circle_primary)
        )

        categoriasAdapter = CategoriasAdapter(categorias) { categoria ->
            Toast.makeText(this, "Categoría: ${categoria.nombre}", Toast.LENGTH_SHORT).show()
        }

        binding.rvCategorias.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriasAdapter
        }
    }

    private fun setupProductosDestacados() {
        // Usar el mismo adaptador de productos pero con layout de grid
        destacadosAdapter = ProductosAdapter(
            productos = emptyList(),
            onProductoClick = { producto ->
                // Navegar al detalle del producto
                val intent = Intent(this, com.asparrin.carlos.estiloya.ui.productos.DetalleProductoActivity::class.java)
                intent.putExtra("producto", producto)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                Toast.makeText(this, "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            },
            onComprarClick = { producto ->
                Toast.makeText(this, "Comprar: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvDestacados.apply {
            layoutManager = GridLayoutManager(this@HomeActivity, 2)
            adapter = destacadosAdapter
        }

        // Aquí cargarías los productos destacados desde la API
        loadProductosDestacados()
    }

    private fun setupMasVendidos() {
        masVendidosAdapter = ProductosAdapter(
            productos = emptyList(),
            onProductoClick = { producto ->
                // Navegar al detalle del producto
                val intent = Intent(this, com.asparrin.carlos.estiloya.ui.productos.DetalleProductoActivity::class.java)
                intent.putExtra("producto", producto)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                Toast.makeText(this, "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            },
            onComprarClick = { producto ->
                Toast.makeText(this, "Comprar: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvMasVendidos.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = masVendidosAdapter
        }

        // Aquí cargarías los productos más vendidos desde la API
        loadMasVendidos()
    }

    private fun setupFloatingActionButton() {
        binding.fabCart.setOnClickListener {
            Toast.makeText(this, "Ver carrito", Toast.LENGTH_SHORT).show()
        }

        // Actualizar badge del carrito
        updateCartBadge(3) // Ejemplo con 3 items
    }

    private fun loadProductosDestacados() {
        // Aquí implementarías la carga de productos destacados desde la API
        // Por ahora usamos datos de ejemplo
        val productosDestacados = listOf<com.asparrin.carlos.estiloya.data.model.Producto>(
            // Usar productos de ejemplo o cargar desde la API
        )
        // destacadosAdapter.updateData(productosDestacados)
    }

    private fun loadMasVendidos() {
        // Aquí implementarías la carga de productos más vendidos desde la API
        // Por ahora usamos datos de ejemplo
        val masVendidos = listOf<com.asparrin.carlos.estiloya.data.model.Producto>(
            // Usar productos de ejemplo o cargar desde la API
        )
        // masVendidosAdapter.updateData(masVendidos)
    }

    private fun updateCartBadge(count: Int) {
        if (count > 0) {
            binding.tvCartBadge.text = count.toString()
            binding.tvCartBadge.visibility = View.VISIBLE
        } else {
            binding.tvCartBadge.visibility = View.GONE
        }
    }
}
