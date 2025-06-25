package com.asparrin.carlos.estiloya.ui.productos

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.data.model.Producto
import com.asparrin.carlos.estiloya.databinding.ActivityDetalleProductoBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity
import com.bumptech.glide.Glide

class DetalleProductoActivity : BaseActivity() {

    private lateinit var binding: ActivityDetalleProductoBinding
    private var producto: Producto? = null

    override fun getLayoutResourceId(): Int = R.layout.activity_detalle_producto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Asociar el binding al contenido inyectado
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityDetalleProductoBinding.bind(child)

        // Obtener el producto pasado como extra
        producto = intent.getParcelableExtra("producto")
        
        if (producto != null) {
            mostrarDetallesProducto(producto!!)
        } else {
            Toast.makeText(this, "Error al cargar el producto", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupListeners()
    }

    private fun mostrarDetallesProducto(producto: Producto) {
        // Configurar imagen
        Glide.with(this)
            .load(producto.imagenUrl)
            .placeholder(R.drawable.ic_producto_dark)
            .error(R.drawable.ic_producto_dark)
            .into(binding.imageProducto)

        // Configurar información básica
        binding.textNombre.text = producto.nombre
        binding.textDescripcion.text = producto.descripcion
        binding.textCategoria.text = producto.categoria
        binding.textTipo.text = producto.tipo
        binding.textId.text = producto.id.toString()

        // Configurar precios y descuento
        if (producto.descuentoPorcentajeCalculado > 0) {
            // Mostrar descuento
            binding.textDescuento.visibility = android.view.View.VISIBLE
            binding.textDescuento.text = "-${producto.descuentoPorcentajeCalculado}%"
            
            // Mostrar precio original tachado
            binding.textPrecioOriginal.visibility = android.view.View.VISIBLE
            binding.textPrecioOriginal.text = "S/ ${String.format("%.2f", producto.precio)}"
            binding.textPrecioOriginal.paintFlags = 
                binding.textPrecioOriginal.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            
            // Calcular y mostrar precio con descuento
            val precioConDescuento = producto.precioConDescuento
            binding.textPrecio.text = "S/ ${String.format("%.2f", precioConDescuento)}"
        } else {
            // Sin descuento
            binding.textDescuento.visibility = android.view.View.GONE
            binding.textPrecioOriginal.visibility = android.view.View.GONE
            binding.textPrecio.text = "S/ ${String.format("%.2f", producto.precio)}"
        }
    }

    private fun setupListeners() {
        binding.btnAgregar.setOnClickListener {
            Toast.makeText(this, "Agregado al carrito: ${producto?.nombre}", Toast.LENGTH_SHORT).show()
        }

        binding.btnComprar.setOnClickListener {
            Toast.makeText(this, "Comprando: ${producto?.nombre}", Toast.LENGTH_SHORT).show()
        }
    }
} 