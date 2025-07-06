package com.asparrin.carlos.estiloya.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.data.model.Producto
import com.asparrin.carlos.estiloya.databinding.ItemProductoBinding
import com.bumptech.glide.Glide

class ProductAdapter(
    private var productos: List<Producto>,
    private val onProductoClick: (Producto) -> Unit,
    private val onAgregarClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(private val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            producto: Producto,
            onProductoClick: (Producto) -> Unit,
            onAgregarClick: (Producto) -> Unit
        ) {
            // Configurar nombre
            binding.textNombre.text = producto.nombre
            
            // Configurar imagen
            Glide.with(binding.root.context)
                .load(producto.imagen)
                .placeholder(R.drawable.ic_producto)
                .error(R.drawable.ic_producto)
                .into(binding.imageProducto)
            
            // Descuento y precios
            val precio = producto.precio ?: java.math.BigDecimal.ZERO
            if (producto.descuentoPorcentajeCalculado > 0) {
                binding.textDescuento.apply {
                    visibility = View.VISIBLE
                    text = "-${producto.descuentoPorcentajeCalculado}%"
                }
                binding.textPrecioOriginal.apply {
                    visibility = View.VISIBLE
                    text = "S/ ${"%.2f".format(precio)}"
                    paintFlags = paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                }
                binding.textPrecio.text = "S/ ${"%.2f".format(producto.precioConDescuento)}"
            } else {
                binding.textDescuento.visibility = View.GONE
                binding.textPrecioOriginal.visibility = View.GONE
                binding.textPrecio.text = "S/ ${"%.2f".format(precio)}"
            }
            
            // Configurar click listeners
            binding.root.setOnClickListener { onProductoClick(producto) }
            binding.btnAgregar.setOnClickListener { onAgregarClick(producto) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto, onProductoClick, onAgregarClick)
    }

    override fun getItemCount(): Int = productos.size

    fun updateData(nuevosProductos: List<Producto>) {
        productos = nuevosProductos
        notifyDataSetChanged()
    }
} 