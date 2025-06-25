package com.asparrin.carlos.estiloya.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.asparrin.carlos.estiloya.R

data class Banner(
    val id: Int,
    val imagen: Int,
    val titulo: String,
    val descripcion: String
)

class BannersAdapter(
    private val banners: List<Banner>,
    private val onBannerClick: (Banner) -> Unit
) : RecyclerView.Adapter<BannersAdapter.BannerViewHolder>() {

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBanner: ImageView = itemView.findViewById(R.id.ivBanner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        
        holder.ivBanner.setImageResource(banner.imagen)
        
        holder.itemView.setOnClickListener {
            onBannerClick(banner)
        }
    }

    override fun getItemCount(): Int = banners.size
} 