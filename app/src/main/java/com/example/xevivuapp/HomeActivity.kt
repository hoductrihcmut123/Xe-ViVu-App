package com.example.xevivuapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.xevivuapp.common.utils.Utils
import com.example.xevivuapp.common.utils.Utils.isCheckLocationPermission
import com.example.xevivuapp.common.utils.showLocationPermissionDialog
import com.example.xevivuapp.databinding.ActivityHomeBinding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONArray
import org.json.JSONException

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null

    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    private var mCurrentLatitude: Double = 0.0
    private var mCurrentLongitude: Double = 0.0
    private var mCurrentAddress: String = "123"
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var originLatLng: LatLng? = null
    private var originAddress: String? = null
    private var destinationLatLng: LatLng? = null
    private var destinationAddress: String? = null

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Places.initialize(applicationContext, BuildConfig.MAP_KEY)
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setCountries("VN")
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {}

            override fun onPlaceSelected(place: Place) {
                val address = place.address
                val id = place.id
                val latLng = place.latLng!!
                val marker = addMarker(latLng)
                if (marker != null) {
                    marker.title = address
                }
                if (marker != null) {
                    marker.snippet = id
                }
                with(binding){
                    title.text = getString(R.string.SelectPickUpPoint)
                    title.isVisible = true
                }
                mGoogleMap?.clear()
                zoomOnMap(latLng)
            }
        })

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = this.let { LocationServices.getFusedLocationProviderClient(it) }

        binding.currentLocationButton.setOnClickListener {
            if (this@HomeActivity.isCheckLocationPermission()) {
                mGoogleMap?.clear()
                mFusedLocationClient?.lastLocation
                    ?.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            mCurrentLatitude = location.latitude
                            mCurrentLongitude = location.longitude
                        }
                        zoomOnMap(LatLng(mCurrentLatitude, mCurrentLongitude), 18f)
                        addMarker(LatLng(mCurrentLatitude, mCurrentLongitude))
                        findAddress(LatLng(mCurrentLatitude, mCurrentLongitude))
                        direction(LatLng(mCurrentLatitude, mCurrentLongitude), LatLng(10.778787, 106.7570348))
                    }
            } else {
                showLocationPermissionDialog()
            }
        }

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        binding.logoutButton.setOnClickListener {
            if (currentUser != null) {
                auth.signOut()
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        mGoogleMap!!.setMaxZoomPreference(20.0f)
        mGoogleMap!!.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
        )

        mGoogleMap?.setOnMapLongClickListener { position ->
            mGoogleMap?.clear()
            addMarker(position)
        }
        mGoogleMap?.setOnMarkerClickListener { marker ->
            marker.remove()
            false
        }
    }

    private fun addMarker(position: LatLng): Marker? {
        return mGoogleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .title("Custom Marker")
                .draggable(true)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(resources, R.drawable.marker_icon),
                            150,
                            150,
                            false
                        )
                    )
                )
        )
    }

    private fun zoomOnMap(latLng: LatLng, zoomRate: Float = 14f) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, zoomRate)
        mGoogleMap?.animateCamera(newLatLngZoom)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.ExitTheApp))
            .setMessage(getString(R.string.AreYouExit))
            .setNegativeButton(getString(R.string.Exit)) { _, _ ->
                super.onBackPressed() // Call the default back button action
            }
            .setPositiveButton(getString(R.string.Return), null)
            .show()
    }

    override fun onStop() {
        super.onStop()
        auth.signOut()
    }

    private fun direction(origin: LatLng, destination: LatLng, mode: String = "driving") {
        val requestQueue = Volley.newRequestQueue(this)
        val url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
            .buildUpon()
            .appendQueryParameter("destination", "${destination.latitude}, ${destination.longitude}")
            .appendQueryParameter("origin", "${origin.latitude}, ${origin.longitude}")
            .appendQueryParameter("mode", mode)
            .appendQueryParameter("key", BuildConfig.MAP_KEY)
            .toString()

        Log.d("DirectionsRequest", "URL: $url")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,  // Method: GET, POST, PUT, DELETE
            url,
            null,
            { response ->
                Log.d("DirectionsResponse", "Response: ${response.toString(2)}")
                try {
                    val status = response.getString("status")
                    if (status.equals("OK")) {
                        val routes: JSONArray = response.getJSONArray("routes")

                        var points: ArrayList<LatLng>
                        var polylineOptions: PolylineOptions? = null

                        for (i in 0 until routes.length()) {
                            points = ArrayList()
                            polylineOptions = PolylineOptions()
                            val legs: JSONArray = routes.getJSONObject(i).getJSONArray("legs")

                            for (j in 0 until legs.length()) {
                                val steps: JSONArray = legs.getJSONObject(j).getJSONArray("steps")

                                for (k in 0 until steps.length()) {
                                    val polyline = steps.getJSONObject(k).getJSONObject("polyline")
                                        .getString("points")
                                    val list: List<LatLng> = Utils.decodePolyLine(polyline)

                                    for (l in list.indices) {
                                        val position = LatLng(list[l].latitude, list[l].longitude)
                                        points.add(position)
                                    }
                                }
                            }
                            polylineOptions.addAll(points)
                            polylineOptions.width(20F)
                            polylineOptions.color(
                                ContextCompat.getColor(
                                    this,
                                    R.color.button_background
                                )
                            )
                            polylineOptions.geodesic(true)
                        }
                        if (polylineOptions != null) {
                            mGoogleMap?.addPolyline(polylineOptions)
                        }
                        addMarker(origin)
                        addMarker(destination)

                        val bounds: LatLngBounds = LatLngBounds.builder()
                            .include(origin)
                            .include(destination)
                            .build()
                        val point = Point()
                        windowManager.defaultDisplay.getSize(point)
                        mGoogleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                point.x,
                                150,
                                30
                            )
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error: VolleyError ->
                error.printStackTrace()
            }
        )
        val retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.setRetryPolicy(retryPolicy)
        requestQueue.add(jsonObjectRequest)
    }

    private fun findAddress(latLng: LatLng) {
        val url = Uri.parse("https://maps.googleapis.com/maps/api/geocode/json")
            .buildUpon()
            .appendQueryParameter("latlng", "${latLng.latitude},${latLng.longitude}")
            .appendQueryParameter("key", BuildConfig.MAP_KEY)
            .toString()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val results = response.getJSONArray("results")
                if (results.length() > 0) {
                    mCurrentAddress = results.getJSONObject(0).getString("formatted_address")
                    with(binding) {
                        title.text = mCurrentAddress
                        title.isVisible = true
                        subTitle.isVisible = true
                    }
                    Log.d("Geocode", "Address: $mCurrentAddress")
                } else {
                    Log.d("Geocode", "Address not found")
                }
            },
            { error ->
                error.printStackTrace()
                Log.e("Geocode", "Lỗi: ${error.message}")
            }
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }
}
