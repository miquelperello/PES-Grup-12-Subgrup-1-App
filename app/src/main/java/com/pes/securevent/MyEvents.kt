package com.pes.securevent

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_my_events.*
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MyEvents : Fragment() {

    private var requestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        var tokenMongoPost :String
        pref.apply{
            tokenMongoPost = (getString("TOKEN", "").toString())

        }

        val url = "https://securevent.herokuapp.com/clients/agenda"
        requestQueue = Volley.newRequestQueue(activity?.applicationContext)
        val arrayList = ArrayList<Model>()

        val request = @SuppressLint("SimpleDateFormat")
        object: JsonArrayRequest(Method.GET, url, null, { response ->
            try {
                for (i in 0 until response.length()) {
                    val event = response.getJSONObject(i)
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val strDate: Date? = sdf.parse(event.getString("date"))
                    val isSameDay: Boolean = strDate?.day == Date().day && strDate.month == Date().month && strDate.year == Date().year
                    if (Date().before(strDate) || isSameDay) {
                        arrayList.add(
                                Model(
                                        event.getString("name"),
                                        event.getString("_id"),
                                        event.getString("logo"),
                                        event.getString("id_room"),
                                        event.getString("date"),
                                        event.getString("hourIni"),
                                        event.getString("hourEnd"),
                                        event.getString("minPrice"),
                                        event.getString("maxPrice")
                                )
                        )
                    }
                }

                val myAdapter = (activity?.applicationContext?.let { MyAdapterMyEvents(arrayList, it, false) })

                recyclerViewE.layoutManager = LinearLayoutManager(activity)
                recyclerViewE.adapter = myAdapter


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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_events, container, false)
    }

}