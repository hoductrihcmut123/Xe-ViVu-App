package com.example.xevivuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import com.example.xevivuapp.databinding.ActivityHomeBinding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null

    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    @OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Places.initialize(applicationContext,getString(R.string.google_map_api_key))
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object :PlaceSelectionListener{
            override fun onError(p0: Status) {
                Toast.makeText(this@HomeActivity,"Có lỗi xảy ra khi tìm kiếm", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {
                val add = place.address
                val id = place.id
                val latLng = place.latLng!!
                val marker = addMarker(latLng)
                marker.title = add
                marker.snippet = id
                zoomOnMap(latLng)
            }
        })

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.currentLocationButton.setOnClickListener {
            mGoogleMap?.clear()
            zoomOnMap(LatLng(10.772770613876723, 106.65925362724845))
            addMarker(LatLng(10.772770613876723, 106.65925362724845))
        }

        auth= FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        binding.logoutButton.setOnClickListener{
            if(currentUser != null) {
                auth.signOut()
            }
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        mGoogleMap?.setOnMapLongClickListener { position ->
            mGoogleMap?.clear()
            addMarker(position)
        }
        mGoogleMap?.setOnMarkerClickListener { marker ->
            marker.remove()
            false
        }
    }

    private fun addMarker(position: LatLng): Marker {
        val marker = mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Custom Marker")
            .draggable(true)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)))    // Custom Marker
        return marker!!
    }

    private fun zoomOnMap(latLng: LatLng){
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
        mGoogleMap?.animateCamera(newLatLngZoom)
    }

    override fun onStop() {
        super.onStop()
        auth.signOut()
    }
}