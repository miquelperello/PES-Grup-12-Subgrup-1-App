package com.pes.securevent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_esdeveniment.*
import kotlinx.android.synthetic.main.fragment_my_events.*
import org.json.JSONException
import org.json.JSONObject

class MyPastEvent : AppCompatActivity() {

    private var requestQueue: RequestQueue? = null
    private var rating: Float? = null
    private var voted: Boolean = false
    private var revId: String? = null
    var localitzacioid : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_past_event)

        val actionBar : ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val intent = intent
        val title = intent.getStringExtra("ETitle")
        val id = intent.getStringExtra("EId")
        val loc = intent.getStringExtra("ELoc")
        val date = intent.getStringExtra("EDate")
        val hour = intent.getStringExtra("EHour")
        val hourEnd = intent.getStringExtra("EHourEnd")
        val logo = intent.getStringExtra("Elogo")

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
        HourE.text = hour
        HourEnd.text = hourEnd

        findIfVoted()

        ratingBarChange()
    }

    private fun findIfVoted() {

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var tokenMongoPost :String
        var username: String
        pref.apply{
            tokenMongoPost = (getString("TOKEN", "").toString())
            username = getString("USERNAME", "").toString()

        }

        val url = "https://securevent.herokuapp.com/ratings"
        requestQueue = Volley.newRequestQueue(this)

        val request = object: JsonArrayRequest(Method.GET, url, null, { response ->
            try {
                for (i in 0 until response.length()) {
                    val rating = response.getJSONObject(i)
                    revId = username + "_" + IDE.text
                    if(rating.getString("_id") == revId) {
                        val rBar = findViewById<RatingBar>(R.id.RatingBar)
                        rBar.rating = rating.getString("rate").toFloat()
                        val comment = findViewById<EditText>(R.id.commentText)
                        comment.setText(rating.getString("comment"))
                        voted = true
                        break
                    }

                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })
        {
            override fun getHeaders(): Map<String, String> {
                // Create HashMap of your Headers as the example provided below

                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token $tokenMongoPost"

                return headers
            }
        }

        requestQueue?.add(request)
    }

    private fun ratingBarChange() {
        val ratingBar = findViewById<RatingBar>(R.id.RatingBar)
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, p1, _ ->
                rating = p1
            }
    }

    fun submitRating(view: View) {
        val comment = findViewById<EditText>(R.id.commentText)
        if(voted)
            putRating(rating, comment.text.toString())
        else
            postRating(rating, comment.text.toString())
        Toast.makeText(this@MyPastEvent, R.string.GiveRating, Toast.LENGTH_SHORT).show()
    }

    private fun putRating(rating:Float?, comment: String) {
        val url = "https://securevent.herokuapp.com/ratings/$revId"

        val pref = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        var username :String
        var tokenMongoPost :String

        pref.apply{
            username = (getString("NAME", "").toString())
            tokenMongoPost = (getString("TOKEN", "").toString())
        }

        //creem objecte JSON per fer la crida POST
        val params = JSONObject()
        params.put("id_event", IDE.text)
        params.put("rate", rating)
        params.put("comment", comment)
        params.put("author", username)

        val request : JsonObjectRequest

        // Volley post request with parameters

        request = object : JsonObjectRequest(Method.PUT, url, params,
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
                headers["Authorization"] = "Token $tokenMongoPost"
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

    private fun postRating(rating: Float?, comment: String) {
        val url = "https://securevent.herokuapp.com/ratings"

        val pref = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        var username :String
        var tokenMongoPost :String

        pref.apply{
            username = (getString("NAME", "").toString())
            tokenMongoPost = (getString("TOKEN", "").toString())
        }

        //creem objecte JSON per fer la crida POST
        val params = JSONObject()
        params.put("id_event", IDE.text)
        params.put("rate", rating)
        params.put("comment", comment)
        params.put("author", username)

        val request : JsonObjectRequest

        // Volley post request with parameters

        request = object : JsonObjectRequest(Method.POST, url, params,
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
                headers["Authorization"] = "Token $tokenMongoPost"
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun goToMaps(view: View) {
        //Fem una crida per saber la localització de la sala

        val url = "https://securevent.herokuapp.com/rooms/$localitzacioid" //<- events

        val requestQueue: RequestQueue? = Volley.newRequestQueue(this)

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