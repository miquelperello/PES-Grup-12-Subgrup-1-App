package com.pes.securevent

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps2)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val extras = intent.extras
        val street: String? = extras?.getString("loc")
        val addresses: List<Address> = getAddress(street)
        if(addresses.isEmpty())
            Snackbar.make(findViewById(R.id.map), R.string.LocNotFound, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        else {
            val location = LatLng(addresses[0].latitude, addresses[0].longitude)
            mMap.addMarker(MarkerOptions().position(location).title("Marker in " + addresses[0].locality))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        }
    }

    private fun getAddress(street: String?): List<Address> {
        val result = StringBuilder()
        var addresses: List<Address>? = null
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            addresses = geocoder.getFromLocationName(street, 1);
            println(addresses);

        } catch (e: IOException) {
            println("fail");
        }
        return addresses!!
    }

}
