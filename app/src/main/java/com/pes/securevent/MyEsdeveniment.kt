package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_esdeveniment.*
import org.json.JSONException

class MyEsdeveniment : AppCompatActivity() {


    var localitzacioid : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myesdeveniment)

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
        val logo = intent.getStringExtra("Elogo")


        actionBar.title = ETitle
        titleE.text = ETitle
        IDE.text = EId
        Picasso.get().load(logo).into(imageE);
        localitzacioid = ELoc
        LocE.text= ELoc!!.split("_")[0]
        DateE.text = EDate
        HourE.text = EHour
        HourEnd.text = EHourEnd

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun share(view: View) {
        if (MainActivity.UsuariActiu) {
            val message: String = resources.getString(R.string.MessageAssist)
            val title: String? = titleE.text as String?
            val loc: String = resources.getString(R.string.MessageLoc)
            val LocE: String? = LocE.text as String?
            val download: String = resources.getString(R.string.MessageDownload)
            //val image = Uri.parse()

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "$message $title $loc $LocE ?\n\n$download")
            //intent.putExtra(Intent.EXTRA_STREAM, image)
            intent.type = "*/*"

            startActivity(Intent.createChooser(intent, "Please select app: "))
        }
        else {
            Snackbar.make(view, resources.getString(R.string.MessageSignIn2), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    fun goToRoomVisualization(view: View) {

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
                event = response.getString("seats")
                val intent = Intent(this, EventDetails::class.java)
                intent.putExtra("eventID", IDE.text)
                intent.putExtra("roomName", LocE.text)
                intent.putExtra("matrix", event)
                intent.putExtra("user_id", user_id)
                intent.putExtra("CanBuy", false)
                startActivity(intent)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })

        requestQueue?.add(request)

    }

    fun goToMaps(view: View) {
        //Fem una crida per saber la localització de la sala
        var requestQueue: RequestQueue? = null

        val url = "https://securevent.herokuapp.com/rooms/" + localitzacioid //<- events

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var user_id :String
        pref.apply{
            user_id = (getString("ID", "").toString())
        }

        requestQueue = Volley.newRequestQueue(this)

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