package com.asparrin.carlos.estiloya.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asparrin.carlos.estiloya.R

data class Categoria(
    val id: Int,
    val nombre: String,
    val icono: Int,
    val color: Int
)

class CategoriasAdapter(
    private val categorias: List<Categoria>,
    private val onCategoriaClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriasAdapter.CategoriaViewHolder>() {

    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcono: ImageView = itemView.findViewById(R.id.ivCategoriaIcono)
        val tvNombre: TextView = itemView.findViewById(R.id.tvCategoriaNombre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        
        holder.ivIcono.setImageResource(categoria.icono)
        holder.ivIcono.setBackgroundResource(categoria.color)
        holder.tvNombre.text = categoria.nombre
        
        holder.itemView.setOnClickListener {
            onCategoriaClick(categoria)
        }
    }

    override fun getItemCount(): Int = categorias.size
} 