package com.pes.securevent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.pes.securevent.MainActivity.Companion.UsuariActiu
import kotlinx.android.synthetic.main.activity_esdeveniment.*
import org.json.JSONException

class Esdeveniment : AppCompatActivity() {

    var canBuy = true;

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

        val btn_pay = findViewById<Button>(R.id.buttonGoToPaypal)
        btn_pay.setOnClickListener { view->
            if (UsuariActiu) {
                goToBuy(view)
            }
            else {
                Snackbar.make(view, getResources().getString(R.string.MessageSignIn), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        }

        val btn_share = findViewById<Button>(R.id.buttonShare)
        btn_share.setOnClickListener { view->
            if (UsuariActiu) {
                val message: String? = getResources().getString(R.string.MessageAssist)
                val title: String? = ETitle
                val loc: String? = getResources().getString(R.string.MessageLoc)
                val LocE: String? = ELoc
                val download: String? = getResources().getString(R.string.MessageDownload)
                //val image = Uri.parse()

                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, "$message $title $loc $LocE ?\n\n$download")
                //intent.putExtra(Intent.EXTRA_STREAM, image)
                intent.type = "*/*"

                startActivity(Intent.createChooser(intent, "Please select app: "))
            }
            else {
                Snackbar.make(view, getResources().getString(R.string.MessageSignIn2), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        }

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var user_id :String
        pref.apply{
            user_id = (getString("ID", "").toString())
        }
        var requestQueue: RequestQueue? = null
        val url = "https://securevent.herokuapp.com/reservations/" + IDE.text + "_" + user_id

        requestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, url, null, {
            this.canBuy = false;
        }, { error -> error.printStackTrace() })

        requestQueue?.add(request)
    }

    fun goToBuy(view: View) {

        var requestQueue: RequestQueue? = null
        var event : String? = null
        //Get de la room para devolver cols y rows

        val url = "https://securevent.herokuapp.com/events/" + IDE.text //<- events

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var user_id :String
        pref.apply{
            user_id = (getString("ID", "").toString())
        }
     
        requestQueue = Volley.newRequestQueue(this)


        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
           try {
               event  = response.getString("seats")
               val intent = Intent(this, EventDetails::class.java)
               intent.putExtra("eventID", IDE.text)
               intent.putExtra("qtt", MinPriceE.text)
               intent.putExtra("roomName", LocE.text)
               intent.putExtra("matrix", event)
               intent.putExtra("user_id", user_id)
               intent.putExtra("CanBuy", canBuy)
               intent.putExtra("eventName", titleE.text)
               intent.putExtra("hourIni", HourE.text)
               intent.putExtra("date", DateE.text)
               startActivity(intent)

           } catch (e: JSONException) {
               e.printStackTrace()
           }
        }, { error -> error.printStackTrace() })

        requestQueue?.add(request)

    }

}

