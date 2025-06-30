package com.asparrin.carlos.estiloya.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.data.model.Categoria

class CategoriasAdapter(
    private var categorias: List<Categoria>,
    private val onCategoriaClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriasAdapter.CategoriaViewHolder>() {

    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcono: ImageView = itemView.findViewById(R.id.ivIcono)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        
        // Usar icono por defecto y background basado en el ID
        val iconoRes = R.drawable.ic_categoria
        val backgroundRes = when (categoria.id % 5) {
            0L -> R.drawable.bg_circle_primary
            1L -> R.drawable.bg_circle_accent
            2L -> R.drawable.bg_circle_success
            3L -> R.drawable.bg_circle_warning
            else -> R.drawable.bg_circle_primary
        }
        
        holder.ivIcono.setImageResource(iconoRes)
        holder.ivIcono.setBackgroundResource(backgroundRes)
        holder.tvNombre.text = categoria.nombre
        
        holder.itemView.setOnClickListener {
            onCategoriaClick(categoria)
        }
    }

    override fun getItemCount(): Int = categorias.size

    fun updateData(nuevasCategorias: List<Categoria>) {
        categorias = nuevasCategorias
        notifyDataSetChanged()
    }
} 