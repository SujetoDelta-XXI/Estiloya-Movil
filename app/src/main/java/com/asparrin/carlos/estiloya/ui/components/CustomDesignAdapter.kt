package com.asparrin.carlos.estiloya.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.data.model.CustomDesign
import com.asparrin.carlos.estiloya.databinding.ItemCustomDesignBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class CustomDesignAdapter(
    private var disenos: List<CustomDesign> = emptyList(),
    private val onItemClick: (CustomDesign) -> Unit = {}
) : RecyclerView.Adapter<CustomDesignAdapter.CustomDesignViewHolder>() {

    class CustomDesignViewHolder(
        private val binding: ItemCustomDesignBinding,
        private val onItemClick: (CustomDesign) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(diseno: CustomDesign) {
            Log.d("CustomDesignAdapter", "Vinculando diseño: ID=${diseno.id}, Descripción=${diseno.descripcion}")
            binding.apply {
                // Cargar imagen - manejar tanto URLs como base64
                cargarImagen(diseno.urlImagen)

                // Configurar descripción
                tvDescripcion.text = diseno.descripcion

                // Configurar fecha
                tvFecha.text = formatearFecha(diseno.fechaCreacion)

                // Configurar estado con color
                tvEstado.text = diseno.estado.uppercase()
                tvEstado.setTextColor(
                    when (diseno.estado.lowercase()) {
                        "aprobado" -> root.context.getColor(android.R.color.holo_green_dark)
                        "denegado" -> root.context.getColor(android.R.color.holo_red_dark)
                        else -> root.context.getColor(android.R.color.holo_orange_dark)
                    }
                )

                // Configurar click listener
                root.setOnClickListener { onItemClick(diseno) }
            }
        }

        private fun cargarImagen(urlImagen: String) {
            if (urlImagen.startsWith("data:image/")) {
                // Es una imagen base64
                cargarImagenBase64(urlImagen)
            } else {
                // Es una URL normal
                Glide.with(binding.root.context)
                    .load(urlImagen)
                    .placeholder(R.drawable.bg_banner_placeholder)
                    .error(R.drawable.bg_banner_placeholder)
                    .into(binding.ivDiseno)
            }
        }

        private fun cargarImagenBase64(base64String: String) {
            try {
                Log.d("CustomDesignAdapter", "Cargando imagen base64, longitud: ${base64String.length}")
                // Extraer el base64 puro (remover el prefijo data:image/png;base64,)
                val base64Data = if (base64String.contains(",")) {
                    base64String.substring(base64String.indexOf(",") + 1)
                } else {
                    base64String
                }
                
                Log.d("CustomDesignAdapter", "Base64 puro extraído, longitud: ${base64Data.length}")
                
                // Decodificar base64 a bytes
                val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                Log.d("CustomDesignAdapter", "Bytes decodificados: ${decodedBytes.size}")
                
                // Convertir bytes a Bitmap
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                
                if (bitmap != null) {
                    binding.ivDiseno.setImageBitmap(bitmap)
                    Log.d("CustomDesignAdapter", "Imagen base64 cargada exitosamente")
                } else {
                    binding.ivDiseno.setImageResource(R.drawable.bg_banner_placeholder)
                    Log.e("CustomDesignAdapter", "Error: Bitmap es null después de decodificar")
                }
            } catch (e: Exception) {
                // En caso de error, mostrar placeholder
                binding.ivDiseno.setImageResource(R.drawable.bg_banner_placeholder)
                Log.e("CustomDesignAdapter", "Error cargando imagen base64: ${e.message}", e)
            }
        }

        private fun formatearFecha(fechaString: String): String {
            return try {
                // Asumiendo que la fecha viene en formato ISO
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fecha = inputFormat.parse(fechaString)
                fecha?.let { outputFormat.format(it) } ?: fechaString
            } catch (e: Exception) {
                fechaString
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDesignViewHolder {
        val binding = ItemCustomDesignBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomDesignViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: CustomDesignViewHolder, position: Int) {
        Log.d("CustomDesignAdapter", "Vinculando item en posición $position")
        holder.bind(disenos[position])
    }

    override fun getItemCount(): Int {
        Log.d("CustomDesignAdapter", "Item count: ${disenos.size}")
        return disenos.size
    }

    fun actualizarDisenos(nuevosDisenos: List<CustomDesign>) {
        disenos = nuevosDisenos
        Log.d("CustomDesignAdapter", "Actualizando diseños: ${disenos.size} diseños")
        notifyDataSetChanged()
    }
} 