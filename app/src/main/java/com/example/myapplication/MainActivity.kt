package com.example.myapplication

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException


class MainActivity : AppCompatActivity() {
    private var requestQueue: RequestQueue? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.my_toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        toolbar?.setTitle("Events")

        val url = "https://securevent.herokuapp.com/events"
        requestQueue = Volley.newRequestQueue(this)
        println("hola")
        val arrayList = ArrayList<Model>()

        val request = JsonArrayRequest(Request.Method.GET, url, null, { response ->
            try {
                for (i in 0 until response.length()) {
                    val event = response.getJSONObject(i)
                    arrayList.add(
                        Model(
                            event.getString("name"),
                            event.getString("street"),
                            R.drawable.icon,
                            event.getString("street"),
                            event.getString("date"),
                            event.getString("hourIni"),
                            event.getString("minPrice"),
                            event.getString("maxPrice")
                        )
                    )
                }

                val myAdapter = (MyAdapter(arrayList, this))

                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = myAdapter


            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })
        requestQueue?.add(request)
    }
}

