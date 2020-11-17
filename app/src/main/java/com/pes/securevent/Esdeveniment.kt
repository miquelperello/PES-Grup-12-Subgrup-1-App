package com.pes.securevent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.snackbar.Snackbar
import com.pes.securevent.MainActivity.Companion.LlistaEvents
import com.pes.securevent.MainActivity.Companion.UsuariActiu
import com.pes.securevent.MainActivity.Companion.usuari
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
        val EDesc = intent.getStringExtra("EDesc")
        val EImageView = intent.getIntExtra("EImage", 0)
        val ELoc = intent.getStringExtra("ELoc")
        val EDate = intent.getStringExtra("EDate")
        val EHour = intent.getStringExtra("EHour")
        val EPriceMin = intent.getStringExtra("EPriceMin")
        val EPriceMax = intent.getStringExtra("EPriceMax")

        actionBar.setTitle(ETitle)
        titleE.text = ETitle
        descE.text = EDesc
        imageE.setImageResource(EImageView)
        LocE.text= ELoc
        DateE.text = EDate
        HourE.text = EHour
        MinPriceE.text = EPriceMin
        MaxPriceE.text = EPriceMax

        val btn_click_me = findViewById(R.id.buttonEvent) as Button
        btn_click_me.setOnClickListener {view->
            println (UsuariActiu)

            if (UsuariActiu) {
                Snackbar.make(view, getResources().getString(R.string.MessageInscripcioEvent), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                //Crida POST {idUser: idEvent}
                LlistaEvents.add(titleE.toString())

            }
            else {

                Snackbar.make(view, getResources().getString(R.string.MessageSignIn), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()

            }
        }
    }



    // get reference to button

// set on-click listener

}