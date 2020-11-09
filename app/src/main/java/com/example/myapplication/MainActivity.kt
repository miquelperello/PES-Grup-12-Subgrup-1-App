package com.example.myapplication

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONException


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var requestQueue: RequestQueue? = null

    lateinit var Meus: Events
    lateinit var Features: Features


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(toolBar)

        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            (R.string.open),
            (R.string.close)
        ){

        }

        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        Meus = Events()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, Meus)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

/*
        val toolbar = findViewById(R.id.my_toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        toolbar?.setTitle("Events")*/

        val url = "https://securevent.herokuapp.com/events"
        requestQueue = Volley.newRequestQueue(this)

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

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        when (menuItem.itemId) {
            R.id.home -> {
                Meus = Events()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, Meus)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.settings -> {
                Features = Features()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, Features)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true;

    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }
}
