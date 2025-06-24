package com.asparrin.carlos.estiloya.ui.base

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import com.asparrin.carlos.estiloya.R
import com.asparrin.carlos.estiloya.databinding.ComponentHeaderBinding
import com.asparrin.carlos.estiloya.databinding.ComponentNavBinding
import com.asparrin.carlos.estiloya.databinding.PopupUsuarioBinding
import com.asparrin.carlos.estiloya.ui.auth.LoginActivity
import com.asparrin.carlos.estiloya.ui.home.HomeActivity
import com.asparrin.carlos.estiloya.ui.productos.ProductosActivity
import com.asparrin.carlos.estiloya.ui.disenar.DisenarActivity
import com.asparrin.carlos.estiloya.utils.SessionManager

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // 0) Aplicar el modo (claro/oscuro) guardado antes de inflar cualquier vista
        val sess = SessionManager(this)
        val isDarkPref = sess.isDarkModePref()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkPref)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        session = sess
        setContentViewWithBase(getLayoutResourceId())
        setupNavigation()  // conserva si quieres la lógica de finish() en nav
    }

    abstract fun getLayoutResourceId(): Int

    protected fun setContentViewWithBase(layoutResID: Int) {
        // 1) Carga el layout base con header y nav
        super.setContentView(R.layout.activity_base)

        // 2) Inyecta el contenido hijo
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        val child = LayoutInflater.from(this)
            .inflate(layoutResID, contentFrame, false)
        contentFrame.addView(child)

        // 3) Configura los componentes comunes
        setupHeader()
        setupNav()
    }

    private fun setupHeader() {
        val headerView = findViewById<View>(R.id.headerSection)
        val hb = ComponentHeaderBinding.bind(headerView)

        val isDark = (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

        hb.iconUser.apply {
            setImageResource(
                if (isDark) R.drawable.ic_user_light
                else R.drawable.ic_user_dark
            )
            setOnClickListener { mostrarPopupUsuario(this) }
        }
        hb.iconCart.apply {
            setImageResource(
                if (isDark) R.drawable.ic_cart_light
                else R.drawable.ic_cart_dark
            )
            setOnClickListener {
                startActivity(Intent(this@BaseActivity, ProductosActivity::class.java))
            }
        }
    }

    private fun setupNav() {
        val navView = findViewById<View>(R.id.navSection)
        val nb = ComponentNavBinding.bind(navView)

        val isDark = (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

        nb.iconHome.apply {
            setImageResource(
                if (isDark) R.drawable.ic_home_light
                else R.drawable.ic_home_dark
            )
        }
        nb.iconProductos.apply {
            setImageResource(
                if (isDark) R.drawable.ic_productos_light
                else R.drawable.ic_productos_dark
            )
        }
        nb.iconDisenar.apply {
            setImageResource(
                if (isDark) R.drawable.ic_estilo_light
                else R.drawable.ic_estilo_dark
            )
        }

        nb.homeSection.setOnClickListener {
            if (this !is HomeActivity) {
                startActivity(Intent(this@BaseActivity, HomeActivity::class.java))
                finish()
            }
        }
        nb.productosSection.setOnClickListener {
            if (this !is ProductosActivity) {
                startActivity(Intent(this@BaseActivity, ProductosActivity::class.java))
                finish()
            }
        }
        nb.disenarSection.setOnClickListener {
            if (this !is DisenarActivity) {
                startActivity(Intent(this@BaseActivity, DisenarActivity::class.java))
                finish()
            }
        }
    }

    private fun mostrarPopupUsuario(anchor: View) {
        val popupBinding = PopupUsuarioBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this@BaseActivity,
                    R.drawable.popup_background
                )
            )
            elevation = 10f
            showAsDropDown(anchor, 0, anchor.height)
        }

        // Login / Logout
        if (session.estaLogueado()) {
            popupBinding.opLogout.text = "Cerrar sesión"
            popupBinding.opLogout.setOnClickListener {
                session.cerrarSesion()
                popupWindow.dismiss()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        } else {
            popupBinding.opLogout.text = "Iniciar sesión"
            popupBinding.opLogout.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                popupWindow.dismiss()
            }
        }

        // Estado inicial del switch y label
        val currentNight = (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        popupBinding.switchTheme.isChecked = currentNight
        popupBinding.tvModo.text =
            if (currentNight) "Modo oscuro" else "Modo claro"

        // Al cambiar el switch, guardar pref y recrear UI
        popupBinding.switchTheme.setOnCheckedChangeListener { _, checked ->
            session.saveDarkModePref(checked)
            AppCompatDelegate.setDefaultNightMode(
                if (checked)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
            popupWindow.dismiss()
            this.recreate()
        }
    }

    private fun setupNavigation() {
        // He eliminado el listener de iconUser aquí para no sobrescribir el de setupHeader().
        findViewById<View>(R.id.iconCart)?.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        // Navegación del navSection ya está en setupNav()
    }
}
