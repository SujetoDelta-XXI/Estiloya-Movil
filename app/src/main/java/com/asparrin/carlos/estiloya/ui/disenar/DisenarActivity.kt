package com.asparrin.carlos.estiloya.ui.disenar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.asparrin.carlos.estiloya.BuildConfig
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.databinding.ActivityDisenarBinding
import com.asparrin.carlos.estiloya.ui.base.BaseActivity
import com.asparrin.carlos.estiloya.ui.disenar.api.*
import com.asparrin.carlos.estiloya.viewModel.CustomDesignViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class DisenarActivity : BaseActivity() {

    private lateinit var binding: ActivityDisenarBinding
    private lateinit var viewModel: CustomDesignViewModel
    private var lastBitmap = null as android.graphics.Bitmap?
    private var lastPrompt = ""

    override fun getLayoutResourceId(): Int = R.layout.activity_disenar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentFrame = findViewById<android.widget.FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = com.asparrin.carlos.estiloya.databinding.ActivityDisenarBinding.bind(child)
        viewModel = androidx.lifecycle.ViewModelProvider(this)[com.asparrin.carlos.estiloya.viewModel.CustomDesignViewModel::class.java]
        setupListeners()
        setupViewModelObservers()
    }

    private fun setupListeners() {
        binding.etPrompt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                generarImagen()
                true
            } else false
        }

        binding.btnGenerar.setOnClickListener { generarImagen() }
        binding.btnReiniciar.setOnClickListener {
            binding.etPrompt.text.clear()
            binding.ivResultado.setImageBitmap(null)
            lastBitmap = null
        }
        binding.btnGuardar.setOnClickListener {
            guardarDiseno()
        }
    }

    private fun setupViewModelObservers() {
        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun generarImagen() {
        val promptText = binding.etPrompt.text.toString().trim()
        if (promptText.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa una instrucción para generar la imagen", Toast.LENGTH_SHORT).show()
            return
        }
        lastPrompt = promptText
        val prompt = promptText

        binding.btnGenerar.apply {
            isEnabled = false
            text = "Generando…"
        }
        binding.progressBarImagen.visibility = android.view.View.VISIBLE
        binding.ivResultado.setImageBitmap(null)

        lifecycleScope.launch {
            try {
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                    ),
                    generationConfig = GeminiGenerationConfig(
                        responseModalities = listOf("TEXT", "IMAGE")
                    )
                )
                val url = "v1beta/models/gemini-2.0-flash-preview-image-generation:generateContent?key=${BuildConfig.GEMINI_API_KEY}"
                val response = DisenarService.api.generarImagen(url, request)

                if (response.isSuccessful) {
                    val gemini = response.body()
                    Log.d("GeminiRaw", gemini.toString())
                    val base64 = gemini
                        ?.candidates
                        ?.flatMap { it.content.parts }
                        ?.firstOrNull { it.inlineData?.data != null }
                        ?.inlineData
                        ?.data
                    if (base64 != null) {
                        val bytes = Base64.decode(base64, Base64.DEFAULT)
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        if (bmp != null) {
                            binding.ivResultado.setImageBitmap(bmp)
                            lastBitmap = bmp
                        } else {
                            Toast.makeText(this@DisenarActivity, "No se pudo decodificar la imagen", Toast.LENGTH_SHORT).show()
                            Log.e("DisenarActivity", "Bitmap nulo tras decodificar")
                        }
                    } else {
                        Toast.makeText(this@DisenarActivity, "No se generó ninguna imagen", Toast.LENGTH_SHORT).show()
                        Log.e("DisenarActivity", "No se encontró inlineData.data")
                    }
                } else {
                    val err = response.errorBody()?.string()
                    Toast.makeText(this@DisenarActivity, "Error ${response.code()}: $err", Toast.LENGTH_LONG).show()
                    Log.e("DisenarActivity", "HTTP ${response.code()} – $err")
                }
            } catch (e: HttpException) {
                Toast.makeText(this@DisenarActivity, "HTTP exception: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("DisenarActivity", "HTTP Exception", e)
            } catch (e: Exception) {
                Toast.makeText(this@DisenarActivity, "Error al generar imagen: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.e("DisenarActivity", "Exception", e)
            } finally {
                try {
                    binding.btnGenerar.apply {
                        isEnabled = true
                        text = "Generar imagen"
                    }
                    binding.progressBarImagen.visibility = android.view.View.GONE
                } catch (e: Exception) {
                    Log.e("DisenarActivity", "Error en finally block", e)
                }
            }
        }
    }

    private fun guardarDiseno() {
        if (lastBitmap == null) {
            Toast.makeText(this, "Primero genera una imagen", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (lastPrompt.isEmpty()) {
            Toast.makeText(this, "No hay descripción para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir bitmap a Base64 para enviar al servidor
        val outputStream = java.io.ByteArrayOutputStream()
        lastBitmap!!.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
        val base64Image = android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.DEFAULT)
        
        // Guardar el diseño
        viewModel.guardarDiseno(lastPrompt, "data:image/png;base64,$base64Image")
        
        Toast.makeText(this, "Diseño guardado exitosamente", Toast.LENGTH_SHORT).show()
    }

    private fun guardarImagenEnGaleria(bitmap: android.graphics.Bitmap) {
        val sdf = java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
        val fileName = "estiloya_${sdf.format(java.util.Date())}.png"
        val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
        val file = java.io.File(downloadsDir, fileName)
        try {
            java.io.FileOutputStream(file).use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
            }
            Toast.makeText(this, "Imagen descargada en Descargas", Toast.LENGTH_SHORT).show()
        } catch (e: java.io.IOException) {
            Toast.makeText(this, "Error al descargar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
