package com.asparrin.carlos.estiloya.ui.disenar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.data.model.CustomDesign
import com.asparrin.carlos.estiloya.databinding.ActivityMisDisenosBinding
import com.asparrin.carlos.estiloya.ui.components.CustomDesignAdapter
import com.asparrin.carlos.estiloya.viewModel.CustomDesignViewModel
import com.asparrin.carlos.estiloya.ui.base.BaseActivity

class MisDisenosActivity : BaseActivity() {

    private lateinit var binding: ActivityMisDisenosBinding
    private lateinit var viewModel: CustomDesignViewModel
    private lateinit var adapter: CustomDesignAdapter

    override fun getLayoutResourceId(): Int = R.layout.activity_mis_disenos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrame = findViewById<android.widget.FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityMisDisenosBinding.bind(child)

        setupRecyclerView()
        setupViewModel()
        setupListeners()
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, com.asparrin.carlos.estiloya.ui.home.HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar diseños cuando se regrese a esta actividad
        viewModel.cargarMisDisenos()
    }

    private fun setupRecyclerView() {
        adapter = CustomDesignAdapter(
            onItemClick = { diseno ->
                mostrarDetallesDiseno(diseno)
            }
        )
        binding.rvDisenos.layoutManager = LinearLayoutManager(this)
        binding.rvDisenos.adapter = adapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[CustomDesignViewModel::class.java]

        // Observar cambios en los diseños
        viewModel.disenos.observe(this) { disenos ->
            adapter.actualizarDisenos(disenos)
            actualizarEstadoVacio(disenos.isEmpty())
        }

        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observar errores
        viewModel.error.observe(this) { error ->
            if (error != null) {
                binding.tvError.text = error
                binding.tvError.visibility = View.VISIBLE
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            } else {
                binding.tvError.visibility = View.GONE
            }
        }

        // Cargar diseños al iniciar
        viewModel.cargarMisDisenos()
    }

    private fun setupListeners() {
        binding.fabNuevoDiseno.setOnClickListener {
            // Navegar a la actividad de crear diseño
            val intent = Intent(this, DisenarActivity::class.java)
            startActivity(intent)
        }
    }

    private fun actualizarEstadoVacio(isEmpty: Boolean) {
        Log.d("MisDisenosActivity", "Actualizando estado vacío: isEmpty=$isEmpty")
        if (isEmpty) {
            binding.llEmptyState.visibility = View.VISIBLE
            binding.rvDisenos.visibility = View.GONE
        } else {
            binding.llEmptyState.visibility = View.GONE
            binding.rvDisenos.visibility = View.VISIBLE
        }
        Log.d("MisDisenosActivity", "Estado vacío: ${if (isEmpty) "VISIBLE" else "GONE"}, RecyclerView: ${if (isEmpty) "GONE" else "VISIBLE"}")
    }

    private fun mostrarDetallesDiseno(diseno: CustomDesign) {
        // Crear un diálogo o navegar a una actividad de detalles
        val mensaje = """
            Diseño #${diseno.id}
            
            Descripción: ${diseno.descripcion}
            Estado: ${diseno.estado.uppercase()}
            Fecha: ${diseno.fechaCreacion}
            
            ${obtenerMensajeEstado(diseno.estado)}
        """.trimIndent()

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

        // Aquí podrías abrir una actividad de detalles más elaborada
        // Intent(this, DetalleDisenoActivity::class.java).apply {
        //     putExtra("diseno_id", diseno.id)
        //     startActivity(this)
        // }
    }

    private fun obtenerMensajeEstado(estado: String): String {
        return when (estado.lowercase()) {
            "aprobado" -> "✅ Tu diseño ha sido aprobado y está disponible para compra."
            "denegado" -> "❌ Tu diseño no cumple con nuestras políticas. Revisa las reglas."
            "pendiente" -> "⏳ Tu diseño está siendo revisado por nuestro equipo."
            else -> "Estado desconocido"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}