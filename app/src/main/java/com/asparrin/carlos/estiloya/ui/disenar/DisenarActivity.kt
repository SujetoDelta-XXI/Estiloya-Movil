package com.asparrin.carlos.estiloya.ui.disenar

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.asparrin.carlos.estiloya.ui.disenar.api.DisenarService
import com.asparrin.carlos.estiloya.ui.disenar.api.StabilityImageRequest
import com.asparrin.carlos.estiloya.ui.disenar.api.TextPrompt
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class DisenarActivity : BaseActivity() {

    private lateinit var binding: ActivityDisenarBinding
    private var lastBitmap: Bitmap? = null

    override fun getLayoutResourceId(): Int = R.layout.activity_disenar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Asociar el binding al contenido inyectado
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        val child = contentFrame.getChildAt(0)
        binding = ActivityDisenarBinding.bind(child)

        setupListeners()
    }

    private fun setupListeners() {
        binding.etPrompt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                generarImagen()
                true
            } else {
                false
            }
        }

        binding.btnGenerar.setOnClickListener {
            generarImagen()
        }

        binding.btnReiniciar.setOnClickListener {
            binding.etPrompt.text.clear()
            binding.ivResultado.setImageBitmap(null)
            lastBitmap = null
        }

        binding.btnGuardar.setOnClickListener {
            // Sin funcionalidad por ahora
        }

        binding.btnDescargar.setOnClickListener {
            if (lastBitmap != null) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                } else {
                    guardarImagenEnGaleria(lastBitmap!!)
                }
            }
        }
    }

    private fun generarImagen() {
        val promptText = binding.etPrompt.text.toString().trim()
        if (promptText.isEmpty()) {
            Toast.makeText(this, "Escribe tu prompt primero", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnGenerar.isEnabled = false
        binding.btnGenerar.text = "Generando…"

        lifecycleScope.launch {
            try {
                // Construye la petición con tu prompt
                val request = StabilityImageRequest(
                    textPrompts = listOf(TextPrompt(text = promptText)),
                    cfg_scale = 7.0f,
                    height = 1024,
                    width = 1024,
                    samples = 1
                )

                // Llama al servicio; ya no es necesario pasar apiKey
                val response = DisenarService.api.generarImagen(
                    request = request
                )

                val artifact = response.artifacts.firstOrNull()
                if (artifact != null) {
                    // Decodifica Base64 y muestra en el ImageView
                    val imageBytes = Base64.decode(artifact.base64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    binding.ivResultado.setImageBitmap(bitmap)
                    lastBitmap = bitmap
                } else {
                    Toast.makeText(
                        this@DisenarActivity,
                        "No se generó ninguna imagen",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: HttpException) {
                // Lee el body de error para diagnóstico
                val errBody = e.response()?.errorBody()?.string()
                Log.e("DisenarActivity", "HTTP ${e.code()} – $errBody")
                Toast.makeText(
                    this@DisenarActivity,
                    "Error ${e.code()}: $errBody",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                Log.e("DisenarActivity", "Error generando imagen", e)
                Toast.makeText(
                    this@DisenarActivity,
                    "Error al generar imagen: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {
                binding.btnGenerar.isEnabled = true
                binding.btnGenerar.text = "Generar imagen"
            }
        }
    }

    private fun guardarImagenEnGaleria(bitmap: Bitmap) {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val fileName = "estiloya_${sdf.format(Date())}.png"
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(picturesDir, fileName)
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(this, "Imagen guardada en Galería", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Error al guardar imagen: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
