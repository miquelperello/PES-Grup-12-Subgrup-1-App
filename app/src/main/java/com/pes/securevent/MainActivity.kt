package com.pes.securevent

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.android.volley.RequestQueue
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var requestQueue: RequestQueue? = null

    lateinit var Events: Events
    lateinit var MyEvents: MyEvents
    lateinit var SignIn: SignIn


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

        Events = Events()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, Events)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

/*
        val toolbar = findViewById(R.id.my_toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        toolbar?.setTitle("Events")*/

        /*val url = "https://securevent.herokuapp.com/events"
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
        requestQueue?.add(request)*/
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        when (menuItem.itemId) {
            R.id.events -> {
                Events = Events()
                toolBar.title = getResources().getString(R.string.Events);
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, Events)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.myevents -> {
                MyEvents = MyEvents()
                toolBar.title = getResources().getString(R.string.MyEvents);
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, MyEvents)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.signin -> {
                SignIn = SignIn()
                toolBar.title = getResources().getString(R.string.SignIn);
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, SignIn)
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