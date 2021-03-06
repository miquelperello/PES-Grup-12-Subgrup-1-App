package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val runnable = Runnable {
        if (!isFinishing) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            carregaUser()
            finish()
        }
    }

    private fun carregaUser() {


        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var check: Boolean
        pref.apply{
            check = getString("EMAIL", "") != ""
        }

        if (check) {
            MainActivity.UsuariActiu = true
            pref.apply {
                val firstname = getString("NAME", "") ?: ""
                val secondname = getString("SURNAME", "") ?: ""
                val email_ = getString("EMAIL", "") ?: ""
                val url_ = getString("IMAGE", "") ?: ""
                MainActivity.usuari = UserG(firstname, secondname, email_, "token", url_)

            }
        }

    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 1500)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}
