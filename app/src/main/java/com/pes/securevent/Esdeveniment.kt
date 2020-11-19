package com.pes.securevent

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
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


        val btn_click_me = findViewById(R.id.buttonEvent) as Button
        btn_click_me.setOnClickListener { view->

            if (UsuariActiu) {
                Snackbar.make(view, getResources().getString(R.string.MessageInscripcioEvent), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                //Crida POST {tokenUser: idEvent}

                PostEvent()
                LlistaEvents.add(titleE.toString())

            }
            else {

                Snackbar.make(view, getResources().getString(R.string.MessageSignIn), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()

            }
        }
    }

    fun PostEvent() {


        val url = "https://securevent.herokuapp.com/reservations"


        //creem objecte JSON per fer la crida POST
        val params = JSONObject()
        params.put("id_event", IDE.text);



        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var tokenMongoPost :String
        pref.apply{
            tokenMongoPost = (getString("TOKEN", "").toString())

        }

        // Volley post request with parameters
        val request = object : JsonObjectRequest(Request.Method.POST, url, params,
                { response ->
                    // Process the json
                    try {
                        Log.i("Registration", "Response $response")
                    } catch (e: Exception) {
                        Log.e("Registration", "Response $e")

                    }

                }, {
            // Error in request -- ja estem registrats
            println("Volley error: $it")

        }) {
            override fun getHeaders(): Map<String, String> {

                val headers = HashMap<String, String>()
                headers.put("Authorization", "Token $tokenMongoPost")

                return headers
            }

        }


        // Volley request policy, only one time request to avoid duplicate transaction
        request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,
                1f
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)



    }



}