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

    //Gestió Users

    //var UsuariActiu=false as  Boolean

    companion object {
        lateinit var usuari: UserG

         var UsuariActiu :Boolean = false

         val LlistaEvents: MutableList<String> = ArrayList()
    }



    //Gestió Navigation Drawer
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

        //nav_view.getMenu().clear(); //clear old inflated items.
        //nav_view.inflateMenu(R.layout.header_log); //inflate new items

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
