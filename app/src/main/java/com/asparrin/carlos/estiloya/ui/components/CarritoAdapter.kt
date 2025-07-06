package com.asparrin.carlos.estiloya.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.data.model.CarritoItem
import com.asparrin.carlos.estiloya.databinding.ItemCarritoBinding
import com.bumptech.glide.Glide

class CarritoAdapter(
    private var items: List<CarritoItem>,
    private val onCantidadChange: (Long, Int) -> Unit,
    private val onEliminar: (Long) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(private val binding: ItemCarritoBinding) : RecyclerView.ViewHolder(binding.root) {
        
        private fun calcularSubtotal(precio: Double, cantidad: Int): Double {
            return precio * cantidad
        }
        
        private fun calcularPrecioConDescuento(precio: Double, descuento: Double): Double {
            return precio - (precio * descuento / 100.0)
        }
        
        fun bind(
            item: CarritoItem,
            onCantidadChange: (Long, Int) -> Unit,
            onEliminar: (Long) -> Unit
        ) {
            // Configurar nombre
            binding.textNombre.text = item.nombre
            
            // Configurar imagen
            Glide.with(binding.root.context)
                .load(item.imagen)
                .placeholder(R.drawable.ic_producto)
                .error(R.drawable.ic_producto)
                .into(binding.imageProducto)
            
            // Calcular precio con descuento una sola vez
            val precioConDescuento = calcularPrecioConDescuento(item.precio, item.descuento)
            
            // Configurar precio unitario con descuento aplicado
            binding.textPrecioUnitario.text = "S/ ${"%.2f".format(precioConDescuento)}"
            
            // Configurar cantidad
            binding.textCantidad.text = item.cantidad.toString()
            
            // Configurar subtotal (precio con descuento * cantidad)
            val subtotalCalculado = calcularSubtotal(precioConDescuento, item.cantidad)
            binding.textSubtotal.text = "S/ ${"%.2f".format(subtotalCalculado)}"
            
            // Configurar botón menos
            binding.btnMenos.setOnClickListener {
                val nuevaCantidad = item.cantidad - 1
                if (nuevaCantidad >= 0) {
                    onCantidadChange(item.productoId, nuevaCantidad)
                }
            }
            
            // Configurar botón más
            binding.btnMas.setOnClickListener {
                val nuevaCantidad = item.cantidad + 1
                onCantidadChange(item.productoId, nuevaCantidad)
            }
            
            // Configurar botón eliminar
            binding.btnEliminar.setOnClickListener {
                onEliminar(item.id)
            }
            
            // Deshabilitar botón menos si cantidad es 1
            binding.btnMenos.isEnabled = item.cantidad > 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = ItemCarritoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onCantidadChange, onEliminar)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(nuevosItems: List<CarritoItem>) {
        val oldItems = items
        items = nuevosItems
        
        // Usar DiffUtil para actualización eficiente
        if (oldItems.isNotEmpty() && nuevosItems.isNotEmpty()) {
            // Si hay cambios, actualizar todo el dataset
            notifyDataSetChanged()
        } else {
            // Si es la primera carga o se vació el carrito
            notifyDataSetChanged()
        }
    }
} 