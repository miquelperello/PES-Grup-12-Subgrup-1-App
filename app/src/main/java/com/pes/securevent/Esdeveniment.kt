package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.pes.securevent.MainActivity.Companion.LlistaEvents
import com.pes.securevent.MainActivity.Companion.UsuariActiu
import com.pes.securevent.MainActivity.Companion.usuari
import kotlinx.android.synthetic.main.activity_esdeveniment.*
import org.json.JSONObject

class Esdeveniment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esdeveniment)

        val actionBar : ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val intent = intent
        val ETitle = intent.getStringExtra("ETitle")
        val EId = intent.getStringExtra("EId")
        val EImageView = intent.getIntExtra("EImage", 0)
        val ELoc = intent.getStringExtra("ELoc")
        val EDate = intent.getStringExtra("EDate")
        val EHour = intent.getStringExtra("EHour")
        val EPriceMin = intent.getStringExtra("EPriceMin")
        val EPriceMax = intent.getStringExtra("EPriceMax")

        actionBar.setTitle(ETitle)
        titleE.text = ETitle
        IDE.text = EId
        imageE.setImageResource(EImageView)
        LocE.text= ELoc
        DateE.text = EDate
        HourE.text = EHour
        MinPriceE.text = EPriceMin
        MaxPriceE.text = EPriceMax

        val btn_click_me = findViewById(R.id.buttonGoToPaypal) as Button
        btn_click_me.setOnClickListener { view->
            if (UsuariActiu) {

                //new activity!
                Snackbar.make(view, getResources().getString(R.string.MessageInscripcioEvent), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                goToPaypal(view)

                LlistaEvents.add(titleE.toString())
            }
            else {
                Snackbar.make(view, getResources().getString(R.string.MessageSignIn), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        }
    }


    fun goToPaypal(view: View) {
        val intent = Intent(this, BuyPaypal::class.java)
        intent.putExtra("eventID", IDE.text)
        intent.putExtra("qtt", MinPriceE.text)
        startActivity(intent)
    }
}