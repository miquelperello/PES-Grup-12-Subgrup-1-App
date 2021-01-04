package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.pes.securevent.MainActivity.Companion.UsuariActiu
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_esdeveniment.*
import org.json.JSONException

class Esdeveniment : AppCompatActivity() {

    var canBuy = true;
    var localitzacioid : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esdeveniment)

        val actionBar : ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val intent = intent
        val ETitle = intent.getStringExtra("ETitle")
        val EId = intent.getStringExtra("EId")
        val ELoc = intent.getStringExtra("ELoc")
        val EDate = intent.getStringExtra("EDate")
        val EHour = intent.getStringExtra("EHour")
        val EHourEnd = intent.getStringExtra("EHourEnd")
        val EPriceMin = intent.getStringExtra("EPriceMin")
        val EPriceMax = intent.getStringExtra("EPriceMax")
        val logo = intent.getStringExtra("Elogo")

        actionBar.title = ETitle
        titleE.text = ETitle
        IDE.text = EId
        Picasso.get().load(logo).into(imageE)
        LocE.text= ELoc!!.split("_")[0]
        localitzacioid = ELoc
        DateE.text = EDate
        HourE.text = EHour
        HourEnd.text = EHourEnd
        MinPriceE.text = EPriceMin
        MaxPriceE.text = EPriceMax

        val btn_pay = findViewById<Button>(R.id.buttonGoToPaypal)
        btn_pay.setOnClickListener { view->
            if (UsuariActiu) {
                goToBuy(view)
            }
            else {
                Snackbar.make(view, resources.getString(R.string.MessageSignIn), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        }

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var user_id :String
        pref.apply{
            user_id = (getString("ID", "").toString())
        }
        val url = "https://securevent.herokuapp.com/reservations/" + IDE.text + "_" + user_id

        val requestQueue: RequestQueue? = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, url, null, {
            this.canBuy = false
        }, { error -> error.printStackTrace() })

        requestQueue?.add(request)
    }

    fun share(view: View) {
        if (UsuariActiu) {
            val message: String = resources.getString(R.string.MessageAssist)
            val title: String? = titleE.text as String?
            val loc: String = resources.getString(R.string.MessageLoc)
            val LocE: String? = LocE.text as String?
            val download: String = resources.getString(R.string.MessageDownload) + " https://tinyurl.com/secureventapp"
            //val image = Uri.parse()

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "$message $title $loc $LocE ?\n\n$download")
            //intent.putExtra(Intent.EXTRA_STREAM, image)
            intent.type = "text/html"

            startActivity(Intent.createChooser(intent, "Please select app: "))
        }
        else {
            Snackbar.make(view, resources.getString(R.string.MessageSignIn2), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    fun goToBuy(view: View) {

        var event: String?
        //Get de la room para devolver cols y rows

        val url = "https://securevent.herokuapp.com/events/" + IDE.text //<- events

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var user_id :String
        pref.apply{
            user_id = (getString("ID", "").toString())
        }

        val requestQueue: RequestQueue? = Volley.newRequestQueue(this)


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
               intent.putExtra("hourEnd", HourEnd.text)
               intent.putExtra("date", DateE.text)
               startActivity(intent)

           } catch (e: JSONException) {
               e.printStackTrace()
           }
        }, { error -> error.printStackTrace() })

        requestQueue?.add(request)

    }

    fun goToMaps(view: View) {
        //Fem una crida per saber la localització de la sala

        val url = "https://securevent.herokuapp.com/rooms/$localitzacioid" //<- events

        val requestQueue: RequestQueue? = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(Request.Method.GET, url, null, { response ->
            try {
                val intent = Intent(this, MapsActivity::class.java)
                val event = response.getJSONObject(0)

                intent.putExtra("loc", event.getString("street")) //passem el carrer

                startActivity(intent)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })

        requestQueue?.add(request)



    }

}

