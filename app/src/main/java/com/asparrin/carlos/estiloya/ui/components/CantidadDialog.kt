package com.asparrin.carlos.estiloya.ui.components

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.data.model.Producto
import com.asparrin.carlos.estiloya.databinding.DialogCantidadProductoBinding
import com.bumptech.glide.Glide
import java.math.BigDecimal

class CantidadDialog(
    private val context: Context,
    private val producto: Producto,
    private val onConfirmar: (Int) -> Unit
) {
    
    private var dialog: Dialog? = null
    private var cantidad = 1
    private lateinit var binding: DialogCantidadProductoBinding
    
    fun mostrar() {
        dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        
        binding = DialogCantidadProductoBinding.inflate(LayoutInflater.from(context))
        dialog?.setContentView(binding.root)
        
        configurarUI()
        configurarListeners()
        
        dialog?.show()
    }
    
    private fun configurarUI() {
        // Configurar información del producto
        binding.textNombre.text = producto.nombre
        
        // Configurar precio
        val precio = if (producto.descuentoPorcentajeCalculado > 0) {
            producto.precioConDescuento
        } else {
            producto.precio
        }
        binding.textPrecio.text = "S/ ${"%.2f".format(precio)}"
        
        // Configurar imagen
        Glide.with(context)
            .load(producto.imagen)
            .placeholder(R.drawable.ic_producto)
            .error(R.drawable.ic_producto)
            .into(binding.imageProducto)
        
        // Configurar cantidad inicial
        actualizarCantidad()
    }
    
    private fun configurarListeners() {
        // Botón menos
        binding.btnMenos.setOnClickListener {
            if (cantidad > 1) {
                cantidad--
                actualizarCantidad()
            }
        }
        
        // Botón más
        binding.btnMas.setOnClickListener {
            if (cantidad < producto.stock) {
                cantidad++
                actualizarCantidad()
            }
        }
        
        // Botón cancelar
        binding.btnCancelar.setOnClickListener {
            dialog?.dismiss()
        }
        
        // Botón agregar
        binding.btnAgregar.setOnClickListener {
            onConfirmar(cantidad)
            dialog?.dismiss()
        }
        
        // Actualizar estado inicial de botones
        actualizarEstadoBotones()
    }
    
    private fun actualizarCantidad() {
        binding.textCantidad.text = cantidad.toString()
        
        // Calcular subtotal
        val precio = if (producto.descuentoPorcentajeCalculado > 0) {
            producto.precioConDescuento
        } else {
            producto.precio
        }
        val subtotal = precio.multiply(BigDecimal.valueOf(cantidad.toLong()))
        binding.textSubtotal.text = "Subtotal: S/ ${"%.2f".format(subtotal)}"
        
        actualizarEstadoBotones()
    }
    
    private fun actualizarEstadoBotones() {
        // Deshabilitar botón menos si cantidad es 1
        binding.btnMenos.isEnabled = cantidad > 1
        
        // Deshabilitar botón más si cantidad es igual al stock
        binding.btnMas.isEnabled = cantidad < producto.stock
        
        // Cambiar color del botón más si está deshabilitado
        if (cantidad >= producto.stock) {
            binding.btnMas.alpha = 0.5f
        } else {
            binding.btnMas.alpha = 1.0f
        }
    }
    
    fun cerrar() {
        dialog?.dismiss()
    }
} 