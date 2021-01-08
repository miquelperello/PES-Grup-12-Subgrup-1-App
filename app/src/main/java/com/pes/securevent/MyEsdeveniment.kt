package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
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
import kotlinx.android.synthetic.main.activity_esdeveniment.DateE
import kotlinx.android.synthetic.main.activity_esdeveniment.HourE
import kotlinx.android.synthetic.main.activity_esdeveniment.HourEnd
import kotlinx.android.synthetic.main.activity_esdeveniment.IDE
import kotlinx.android.synthetic.main.activity_esdeveniment.LocE
import kotlinx.android.synthetic.main.activity_esdeveniment.imageE
import kotlinx.android.synthetic.main.activity_esdeveniment.titleE
import kotlinx.android.synthetic.main.activity_myesdeveniment.*
import org.json.JSONException
import java.util.*

class MyEsdeveniment : AppCompatActivity() {


    var localitzacioid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myesdeveniment)

        val actionBar : ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)


        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var user_id :String
        pref.apply{
            user_id = (getString("ID", "").toString())
        }


        val intent = intent
        val title = intent.getStringExtra("ETitle")
        val id = intent.getStringExtra("EId")
        val loc = intent.getStringExtra("ELoc")
        val date = intent.getStringExtra("EDate")
        val hourIni = intent.getStringExtra("EHour")
        val hourEnd = intent.getStringExtra("EHourEnd")
        val logo = intent.getStringExtra("Elogo")
        val resId = id + "_"  + user_id

        getSeats(resId)

        if(logo == "")
            Picasso.get().load(R.drawable.icon).into(imageE)
        else
            Picasso.get().load(logo).into(imageE)

        actionBar.title = title
        titleE.text = title
        IDE.text = id
        localitzacioid = loc
        LocE.text= loc!!.split("_")[0]
        DateE.text = date
        HourE.text = hourIni
        HourEnd.text = hourEnd
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getSeats(id: String) {

        val url = "https://securevent.herokuapp.com/reservations/$id"
        var seats: String? = ""

        val requestQueue: RequestQueue? = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                for (seat in response.getJSONArray("seat_number").toString().split(",")) {
                    val s = seat.split("-")
                    seats += resources.getString(R.string.Row) + ": " + s[0].dropWhile { c -> c == '"' || c == '[' } + ", " +
                            resources.getString(R.string.Col) + ": " + s[1][0]+ "\n"
                }
                Seats.text = seats

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })

        requestQueue?.add(request)
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
            intent.type = "text/html"

            startActivity(Intent.createChooser(intent, "Please select app: "))
        }
        else {
            Snackbar.make(view, resources.getString(R.string.MessageSignIn2), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    fun goToRoomVisualization(view: View) {

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

        val url = "https://securevent.herokuapp.com/rooms/$localitzacioid"

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

    fun goToCalendar(view: View) {

        val date = DateE.text?.split('-')!!
        val hourIni = HourE.text?.split(":")!!
        val hourEnd = HourEnd.text?.split(":")!!

        val startMillis: Long = Calendar.getInstance().run {
            set(date[0].toInt(), date[1].toInt()-1, date[2].toInt(), hourIni[0].toInt(), hourIni[1].toInt())
            timeInMillis
        }
        val endMillis: Long = Calendar.getInstance().run {
            set(date[0].toInt(), date[1].toInt()-1, date[2].toInt(), hourEnd[0].toInt(), hourEnd[1].toInt())
            timeInMillis
        }

        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            .putExtra(CalendarContract.Events.TITLE, titleE.text)
            .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
            .putExtra(CalendarContract.Events.EVENT_LOCATION, LocE.text)
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        startActivity(intent)
    }



}