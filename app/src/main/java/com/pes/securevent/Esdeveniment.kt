package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.pes.securevent.MainActivity.Companion.LlistaEvents
import com.pes.securevent.MainActivity.Companion.UsuariActiu
import kotlinx.android.synthetic.main.activity_esdeveniment.*

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

        actionBar.title = ETitle
        titleE.text = ETitle
        IDE.text = EId
        imageE.setImageResource(EImageView)
        LocE.text= ELoc
        DateE.text = EDate
        HourE.text = EHour
        MinPriceE.text = EPriceMin
        MaxPriceE.text = EPriceMax

        val btn_click_me = findViewById<Button>(R.id.buttonGoToPaypal)
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

        /*
        Get de la room para devolver cols y rows

        val url = "https://securevent.herokuapp.com/rooms/probando_20"
        var room = JSONObject()
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                room = response
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })

        requestQueue?.add(request)

        println(room)

         */

        val intent = Intent(this, BuyPaypal::class.java)
        intent.putExtra("eventID", IDE.text)
        intent.putExtra("qtt", MinPriceE.text)
        intent.putExtra("roomName", LocE.text)
        startActivity(intent)
    }
}

