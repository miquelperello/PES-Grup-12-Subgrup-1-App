package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_esdeveniment.*
import org.json.JSONException

class MyPastEvent : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_past_event)

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

        ratingBarChange()
    }

    fun ratingBarChange() {
        val ratingBar = findViewById<RatingBar>(R.id.RatingBar)
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, p1, _ ->
                val string : String = getString(R.string.GiveRating) + " " + p1
                Toast.makeText(this@MyPastEvent, string, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun goToMaps(view: View) {
        //Fem una crida per saber la localització de la sala
        var requestQueue: RequestQueue? = null

        val url = "https://securevent.herokuapp.com/rooms/" + LocE.text //<- events

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

                intent.putExtra("loc",event.getString("street")) //passem el carrer

                startActivity(intent)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })

        requestQueue?.add(request)



    }



}