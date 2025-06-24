package com.asparrin.carlos.estiloya.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.databinding.ActivityHomeBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity
import com.asparrin.carlos.estiloya.ui.productos.ProductosActivity

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun getLayoutResourceId(): Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Asociar el binding al contenido inyectado
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityHomeBinding.bind(child)

        // Navegar a Productos cuando hagan clic en el icono
        binding.iconProductos.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
    }
}
