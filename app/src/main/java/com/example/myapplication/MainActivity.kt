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
                            R.mipmap.ic_launcher,
                            event.getString("street"),
                            event.getString("date"),
                            event.getString("hourIni"),
                            event.getString("price_range")
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

/*
        val arrayList = ArrayList<Model>()
        arrayList.add(Model("Event1", "Tipus1", R.mipmap.ic_launcher))
        arrayList.add(Model("Event2", "Tipus2", R.mipmap.ic_launcher))
        arrayList.add(Model("Event3", "Tipus3", R.mipmap.ic_launcher))
        arrayList.add(Model("Event4", "Tipus4", R.mipmap.ic_launcher))
        arrayList.add(Model("Event5", "Tipus5", R.mipmap.ic_launcher))
        arrayList.add(Model("Event6", "Tipus6", R.mipmap.ic_launcher))
        arrayList.add(Model("Event7", "Tipus7", R.mipmap.ic_launcher))
        arrayList.add(Model("Event8", "Tipus8", R.mipmap.ic_launcher))
        arrayList.add(Model("Event9", "Tipus9", R.mipmap.ic_launcher))
        */



        /*findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()*/
        }


    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }*/
}

