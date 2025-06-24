package com.asparrin.carlos.estiloya.ui.productos

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.data.model.Producto
import com.asparrin.carlos.estiloya.databinding.ItemProductoBinding
import com.bumptech.glide.Glide

class ProductosAdapter(
    private val productos: List<Producto>,
    private val onProductoClick: (Producto) -> Unit,
    private val onAgregarClick: (Producto) -> Unit,
    private val onComprarClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(private val binding: ItemProductoBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            producto: Producto,
            onProductoClick: (Producto) -> Unit,
            onAgregarClick: (Producto) -> Unit,
            onComprarClick: (Producto) -> Unit
        ) {
            // Configurar nombre
            binding.textNombre.text = producto.nombre
            
            // Configurar imagen
            Glide.with(binding.root.context)
                .load(producto.imagenUrl)
                .placeholder(R.drawable.ic_producto_dark)
                .error(R.drawable.ic_producto_dark)
                .into(binding.imageProducto)
            
            // Configurar precios y descuento
            if (producto.descuento > 0) {
                // Mostrar descuento
                binding.textDescuento.visibility = View.VISIBLE
                binding.textDescuento.text = "-${producto.descuento}%"
                
                // Mostrar precio original tachado
                binding.textPrecioOriginal.visibility = View.VISIBLE
                binding.textPrecioOriginal.text = "S/ ${String.format("%.2f", producto.precio)}"
                binding.textPrecioOriginal.paintFlags = 
                    binding.textPrecioOriginal.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                
                // Calcular y mostrar precio con descuento
                val precioConDescuento = producto.precio * (1 - producto.descuento / 100.0)
                binding.textPrecio.text = "S/ ${String.format("%.2f", precioConDescuento)}"
            } else {
                // Sin descuento
                binding.textDescuento.visibility = View.GONE
                binding.textPrecioOriginal.visibility = View.GONE
                binding.textPrecio.text = "S/ ${String.format("%.2f", producto.precio)}"
            }
            
            // Configurar click listeners
            binding.root.setOnClickListener { onProductoClick(producto) }
            binding.btnAgregar.setOnClickListener { onAgregarClick(producto) }
            binding.btnComprar.setOnClickListener { onComprarClick(producto) }
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
        holder.bind(producto, onProductoClick, onAgregarClick, onComprarClick)
    }

    override fun getItemCount(): Int = productos.size
}
