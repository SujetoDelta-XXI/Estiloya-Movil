package com.asparrin.carlos.estiloya.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.ui.home.Banner

class BannersAdapter(
    private val banners: List<Banner>,
    private val onBannerClick: (Banner) -> Unit
) : RecyclerView.Adapter<BannersAdapter.BannerViewHolder>() {

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivBanner)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        
        holder.imageView.setImageResource(banner.imagen)
        holder.tvTitulo.text = banner.titulo
        holder.tvDescripcion.text = banner.descripcion
        
        holder.itemView.setOnClickListener {
            onBannerClick(banner)
        }
    }

    override fun getItemCount(): Int = banners.size
} 