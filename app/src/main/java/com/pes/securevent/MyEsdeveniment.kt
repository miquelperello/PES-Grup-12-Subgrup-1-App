package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_esdeveniment.*
import org.json.JSONException

class MyEsdeveniment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myesdeveniment)

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

        actionBar.title = ETitle
        titleE.text = ETitle
        IDE.text = EId
        imageE.setImageResource(EImageView)
        LocE.text= ELoc
        DateE.text = EDate
        HourE.text = EHour
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
                event  = response.getString("seats")
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
}