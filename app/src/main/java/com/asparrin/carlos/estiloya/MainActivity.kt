package com.asparrin.carlos.estiloya

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.asparrin.carlos.estiloya.databinding.ActivityMainBinding
import com.asparrin.carlos.estiloya.ui.auth.LoginActivity
import com.asparrin.carlos.estiloya.ui.auth.RegisterActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.button4.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // Methods for onClick attributes in layout
    fun onLoginClicked(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun onRegistroClicked(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}

