package com.example.xevivuapp.features.booking

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
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
import com.example.xevivuapp.BuildConfig
import com.example.xevivuapp.MainActivity
import com.example.xevivuapp.R
import com.example.xevivuapp.common.adapter.DriverFeedbackAdapter
import com.example.xevivuapp.common.utils.Constants
import com.example.xevivuapp.common.utils.Constants.DESIRED_NUM_OF_SPINS
import com.example.xevivuapp.common.utils.Constants.DESIRED_SECOND_PER_ONE_FULL_360_SPIN
import com.example.xevivuapp.common.utils.Constants.EFFECT_DURATION
import com.example.xevivuapp.common.utils.Utils
import com.example.xevivuapp.common.utils.Utils.calculateMinutesDifference
import com.example.xevivuapp.common.utils.Utils.calculateMoney
import com.example.xevivuapp.common.utils.Utils.convertMetersToKilometers
import com.example.xevivuapp.common.utils.Utils.convertSecondsToMinutes
import com.example.xevivuapp.common.utils.Utils.formatCurrency
import com.example.xevivuapp.common.utils.Utils.getCurrentTimeFormatted
import com.example.xevivuapp.common.utils.Utils.getHourAndMinute
import com.example.xevivuapp.common.utils.Utils.isCheckLocationPermission
import com.example.xevivuapp.common.utils.Utils.vibrateCustomPattern
import com.example.xevivuapp.common.utils.showLocationPermissionDialog
import com.example.xevivuapp.common.utils.showLocationRequestDialog
import com.example.xevivuapp.data.DriverFeedback
import com.example.xevivuapp.data.ReasonData
import com.example.xevivuapp.data.TripData
import com.example.xevivuapp.databinding.ActivityHomeBinding
import com.example.xevivuapp.features.menu.personal_info.PersonalInfoActivity
import com.example.xevivuapp.features.menu.points.PointActivity
import com.example.xevivuapp.features.menu.setting.SettingActivity
import com.example.xevivuapp.features.menu.support.SupportActivity
import com.example.xevivuapp.features.menu.trip_history.TripHistoryActivity
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import org.json.JSONArray
import org.json.JSONException
import java.util.Date

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null

    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var passengersCollection: CollectionReference
    private lateinit var driversCollection: CollectionReference
    private lateinit var tripsCollection: CollectionReference
    private var passengerID: String = ""
    private var driverID: String = ""
    private var exceptDriverID: String = ""
    private var tripID: String = ""
    private var reasonID: String = ""
    private var driverFeedbackId: String = ""
    private var isRating: Boolean = false
    private var isReceiptOpened: Boolean = false
    private var isDriverConfirm: Boolean = false
    private var isFromBottomSheetOnGoing: Boolean = false

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var originLatLng: LatLng? = null
    private var originAddress: String? = null
    private var destinationLatLng: LatLng? = null
    private var destinationAddress: String? = null
    private var showedDirection: Boolean = false
    private var distance: Double = 0.0
    private var duration: Int = 0

    private var vehicleType: String? = null
    private var paymentType: String? = null

    private lateinit var bottomSheetVehicleType: BottomSheetBehavior<View>
    private lateinit var bottomSheetPaymentType: BottomSheetBehavior<View>
    private lateinit var bottomSheetBookingInfo: BottomSheetBehavior<View>
    private lateinit var bottomSheetBookingFail: BottomSheetBehavior<View>
    private lateinit var bottomSheetBookingSuccess: BottomSheetBehavior<View>
    private lateinit var bottomSheetDriverCancel: BottomSheetBehavior<View>
    private lateinit var bottomSheetOnGoing: BottomSheetBehavior<View>
    private lateinit var bottomSheetRating: BottomSheetBehavior<View>
    private lateinit var bottomSheetCancellation: BottomSheetBehavior<View>
    private lateinit var bottomSheetReport: BottomSheetBehavior<View>
    private lateinit var bottomSheetReceipt: BottomSheetBehavior<View>
    private lateinit var bottomSheetDriverPersonalInfo: BottomSheetBehavior<View>

    private lateinit var sideSheetMenu: SideSheetBehavior<View>

    private var listenerTrip: ListenerRegistration? = null
    private var listenerDriver: ListenerRegistration? = null

    private var currentMarkerInTrip: Marker? = null

    // Adapter
    private val driverFeedbackAdapter: DriverFeedbackAdapter by lazy { DriverFeedbackAdapter() }

    // Effect
    private var lastUserCircle: Circle? = null
    private var lastPulseAnimator: ValueAnimator? = null

    // Spinning animation
    private var animator: ValueAnimator? = null

    private val mapLongClickListener = GoogleMap.OnMapLongClickListener { location ->
        mGoogleMap?.clear()
        originLatLng = LatLng(location.latitude, location.longitude)
        addDefaultMarker(location)
        findAddress(location)
    }

    private val markerClickListener = GoogleMap.OnMarkerClickListener { marker ->
        marker.remove()
        false
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        passengersCollection = firestore.collection("Passengers")
        driversCollection = firestore.collection("Drivers")
        tripsCollection = firestore.collection("Trips")
        passengerID = intent.getStringExtra("Passenger_ID").toString()

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
                mGoogleMap?.clear()
                val address = place.address
                val id = place.id
                val latLng = place.latLng!!
                val marker = addDefaultMarker(latLng)
                if (marker != null) {
                    marker.title = address
                }
                if (marker != null) {
                    marker.snippet = id
                }

                if (destinationLatLng == null) {
                    destinationLatLng = latLng
                    destinationAddress = address
                    with(binding) {
                        subTitle.text = getString(R.string.Destination, destinationAddress)
                        subTitle.isVisible = true
                        title.text = getString(R.string.SelectPickUpPoint)
                        title.isVisible = true
                        chooseLocationButton.isVisible = true
                        menuButton.isVisible = false
                        backButton.isVisible = true
                    }
                } else {
                    originLatLng = latLng
                    originAddress = address
                }
                zoomOnMap(latLng)
            }
        })

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = this.let { LocationServices.getFusedLocationProviderClient(it) }

        // Set up bottomSheetVehicleType
        bottomSheetVehicleType = BottomSheetBehavior.from(findViewById(R.id.bottomSheetVehicleType))
        bottomSheetVehicleType.peekHeight = 0
        bottomSheetVehicleType.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetVehicleType.isDraggable = false

        // Set up bottomSheetPaymentType
        bottomSheetPaymentType = BottomSheetBehavior.from(findViewById(R.id.bottomSheetPaymentType))
        bottomSheetPaymentType.peekHeight = 0
        bottomSheetPaymentType.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetPaymentType.isDraggable = false

        // Set up bottomSheetBookingInfo
        bottomSheetBookingInfo = BottomSheetBehavior.from(findViewById(R.id.bottomSheetBookingInfo))
        bottomSheetBookingInfo.peekHeight = 0
        bottomSheetBookingInfo.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBookingInfo.isDraggable = false

        // Set up bottomSheetBookingFail
        bottomSheetBookingFail = BottomSheetBehavior.from(findViewById(R.id.bottomSheetBookingFail))
        bottomSheetBookingFail.peekHeight = 0
        bottomSheetBookingFail.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBookingFail.isDraggable = false

        // Set up bottomSheetBookingSuccess
        bottomSheetBookingSuccess =
            BottomSheetBehavior.from(findViewById(R.id.bottomSheetBookingSuccess))
        bottomSheetBookingSuccess.peekHeight = 0
        bottomSheetBookingSuccess.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBookingSuccess.isDraggable = false

        // Set up bottomSheetDriverCancel
        bottomSheetDriverCancel =
            BottomSheetBehavior.from(findViewById(R.id.bottomSheetDriverCancel))
        bottomSheetDriverCancel.peekHeight = 0
        bottomSheetDriverCancel.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetDriverCancel.isDraggable = false

        // Set up bottomSheetOnGoing
        bottomSheetOnGoing = BottomSheetBehavior.from(findViewById(R.id.bottomSheetOnGoing))
        bottomSheetOnGoing.peekHeight = 0
        bottomSheetOnGoing.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetOnGoing.isDraggable = false

        // Set up bottomSheetRating
        bottomSheetRating = BottomSheetBehavior.from(findViewById(R.id.bottomSheetRating))
        bottomSheetRating.peekHeight = 0
        bottomSheetRating.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetRating.isDraggable = false

        // Set up bottomSheetCancellation
        bottomSheetCancellation =
            BottomSheetBehavior.from(findViewById(R.id.bottomSheetCancellation))
        bottomSheetCancellation.peekHeight = 0
        bottomSheetCancellation.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetCancellation.isDraggable = false

        // Set up bottomSheetReport
        bottomSheetReport = BottomSheetBehavior.from(findViewById(R.id.bottomSheetReport))
        bottomSheetReport.peekHeight = 0
        bottomSheetReport.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetReport.isDraggable = false

        // Set up bottomSheetReceipt
        bottomSheetReceipt = BottomSheetBehavior.from(findViewById(R.id.bottomSheetReceipt))
        bottomSheetReceipt.peekHeight = 0
        bottomSheetReceipt.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetReceipt.isDraggable = false

        // Set up bottomSheetDriverPersonalInfo
        bottomSheetDriverPersonalInfo =
            BottomSheetBehavior.from(findViewById(R.id.bottomSheetDriverPersonalInfo))
        bottomSheetDriverPersonalInfo.peekHeight = 0
        bottomSheetDriverPersonalInfo.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetDriverPersonalInfo.isDraggable = false

        // Set up sideSheetMenu
        sideSheetMenu = SideSheetBehavior.from(findViewById(R.id.sideSheetMenu))
        sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
        sideSheetMenu.isDraggable = true

        // Set up feedback adapter
        binding.bottomSheetDriverPersonalInfo.recyclerViewFeedback.adapter = driverFeedbackAdapter

        binding.currentLocationButton.setOnClickListener {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val client = LocationServices.getSettingsClient(this)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                if (this@HomeActivity.isCheckLocationPermission()) {
                    mGoogleMap?.clear()
                    mFusedLocationClient?.lastLocation
                        ?.addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                originLatLng = LatLng(location.latitude, location.longitude)
                            } else {
                                Toast.makeText(
                                    this,
                                    getString(R.string.WaitSecondToDetermineLocation),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            originLatLng?.let { originLatLng -> zoomOnMap(originLatLng, 18f) }
                            originLatLng?.let { originLatLng -> addDefaultMarker(originLatLng) }
                            originLatLng?.let { originLatLng ->
                                findAddress(
                                    originLatLng,
                                    isCurrentLocation = true
                                )
                            }
                        }
                } else {
                    showLocationPermissionDialog()
                }
            }
            task.addOnFailureListener {
                showLocationRequestDialog()
            }
        }

        binding.chooseLocationButton.setOnClickListener {
            if (originLatLng != null && originAddress != null) {
                with(binding) {
                    subTitle.text =
                        getString(R.string.Destination_Pickup, originAddress, destinationAddress)
                    subTitle.isVisible = true
                    title.text = getString(R.string.VehicleOptions)
                    title.isVisible = true
                    chooseLocationButton.isVisible = false
                    currentLocationButton.isVisible = false
                    searchView.isVisible = false
                }
                direction(originLatLng!!, destinationLatLng!!)
                showedDirection = true
                mGoogleMap?.setOnMapLongClickListener(null)
                mGoogleMap?.setOnMarkerClickListener(null)
                bottomSheetVehicleType.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        binding.backButton.setOnClickListener {
            resetValue()
        }

        binding.menuButton.setOnClickListener {
            binding.searchView.isVisible = false
            binding.dimOverlay.isVisible = true
            binding.sideSheetMenu.tvMenuItem1.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.yellow
                )
            )
            sideSheetMenu.state = SideSheetBehavior.STATE_EXPANDED
        }

        val sideSheetCallback = object : SideSheetCallback() {
            override fun onStateChanged(sideSheet: View, newState: Int) {}

            override fun onSlide(sideSheet: View, slideOffset: Float) {
                binding.dimOverlay.alpha = (slideOffset * 1.5).toFloat()
                if (slideOffset == 0f) {
                    binding.dimOverlay.isVisible = false
                    binding.searchView.isVisible = true
                }
            }
        }
        sideSheetMenu.addCallback(sideSheetCallback)

        with(binding.sideSheetMenu) {
            sideSheetMenuItem2.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@HomeActivity, PersonalInfoActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem3.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@HomeActivity, TripHistoryActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem4.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@HomeActivity, PointActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem5.setOnClickListener {
                Toast.makeText(
                    this@HomeActivity,
                    getString(R.string.FeatureInDevelop),
                    Toast.LENGTH_LONG
                ).show()
            }
            sideSheetMenuItem6.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@HomeActivity, SettingActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem7.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@HomeActivity, SupportActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem8.setOnClickListener {
                logout(currentUser)
            }
        }

        with(binding.bottomSheetVehicleType) {
            cardBike.setOnClickListener {
                vehicleType = Constants.BIKE
                bottomSheetVehicleType.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.title.text = getString(R.string.PaymentOptions)
                bottomSheetPaymentType.state = BottomSheetBehavior.STATE_EXPANDED
            }
            cardCar.setOnClickListener {
                vehicleType = Constants.CAR
                bottomSheetVehicleType.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.title.text = getString(R.string.PaymentOptions)
                bottomSheetPaymentType.state = BottomSheetBehavior.STATE_EXPANDED
            }
            cardMvp.setOnClickListener {
                vehicleType = Constants.MVP
                bottomSheetVehicleType.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.title.text = getString(R.string.PaymentOptions)
                bottomSheetPaymentType.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        with(binding.bottomSheetPaymentType) {
            cardCash.setOnClickListener {
                paymentType = Constants.CASH
                cardCash.cardElevation = 12f
                cardMomo.cardElevation = 0f
            }
            cardMomo.setOnClickListener {
                paymentType = Constants.MOMO
                cardMomo.elevation = 12f
                cardCash.elevation = 0f
            }
            bookingButton.setOnClickListener {
                if (paymentType != null) {
                    setUpUIWhenBooking()
                    setUpDataWhenBooking()

                    mGoogleMap?.clear()
                    // Tilt
                    val cameraPos = CameraPosition.Builder().target(originLatLng!!)
                        .tilt(45f)
                        .zoom(16f)
                        .build()
                    mGoogleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos))

                    // Start animation
                    addMarkerWithPulseAnimation()
                }
            }
        }

        with(binding.bottomSheetBookingInfo) {
            cancelButton.setOnClickListener {
                resetValue()
                bottomSheetBookingInfo.state = BottomSheetBehavior.STATE_COLLAPSED

                animator?.cancel()
                mGoogleMap?.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(mGoogleMap?.cameraPosition!!.target)
                            .tilt(0f)
                            .zoom(14f)
                            .build()
                    )
                )
                tripsCollection.document(tripID).collection("Reason").document(reasonID).delete()
                tripsCollection.document(tripID).delete()
                if (driverID.isNotEmpty()) {
                    driversCollection.document(driverID).update(
                        mapOf(
                            "trip_ID" to FieldValue.delete()
                        )
                    )
                }
                driverID = ""
                tripID = ""
                driverFeedbackId = ""
                reasonID = ""
                isRating = false
            }
        }

        with(binding.bottomSheetBookingSuccess) {
            callButton.setOnClickListener {
                driversCollection.document(driverID).get().addOnSuccessListener { document ->
                    val phone = document.getString("mobile_No")
                    val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    startActivity(phoneIntent)
                }
            }
            chatButton.setOnClickListener {
                driversCollection.document(driverID).get().addOnSuccessListener { document ->
                    val phone = document.getString("mobile_No")
                    val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phone"))
                    startActivity(smsIntent)
                }
            }
            cancelWhileArrivingButton.setOnClickListener {
                mGoogleMap?.clear()
                binding.driverFoundNotification.isVisible = false
                binding.title.isVisible = false
                bottomSheetBookingSuccess.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetCancellation.state = BottomSheetBehavior.STATE_EXPANDED

                val tripRef = firestore.collection("Trips").document(tripID)
                tripRef.update(
                    mapOf(
                        "status" to Constants.PASSENGER_CANCEL
                    )
                )
                resetAfterCancelBooking(isDeleteTrip = false)
            }
            personalInfoLayout.setOnClickListener {
                with(binding.bottomSheetDriverPersonalInfo) {
                    driversCollection.document(driverID).get()
                        .addOnSuccessListener { document ->
                            tvDriverNamePersonalInfo.text = buildString {
                                append(document.getString("lastname"))
                                append(" ")
                                append(document.getString("firstname"))
                            }
                            tvVehicleLinePersonalInfo.text = buildString {
                                append(document.getString("vehicle_Brand"))
                                append(" ")
                                append(document.getString("vehicle_Line"))
                            }
                            tvRatingAverage.text = calculateRateAverage(document).toString()
                            tvTotalDistance.text =
                                getString(R.string.TotalKm, document.getLong("totalDistance"))
                            tvTotalTripNum.text =
                                getString(R.string.TotalTrip, document.getLong("completeTripNum"))

                            when (document.getString("classify")) {
                                Constants.BIKE -> {
                                    tvVehicleTypeValue.text = getString(R.string.Bike)
                                }

                                Constants.CAR -> {
                                    tvVehicleTypeValue.text = getString(R.string.Car)
                                }

                                Constants.MVP -> {
                                    tvVehicleTypeValue.text = getString(R.string.MVP)
                                }

                                else -> {
                                    tvVehicleTypeValue.text = ""
                                }
                            }
                            tvLicensePlateValue.text = document.getString("license_Plate")
                            when (document.getBoolean("gender")) {
                                true -> tvGenderValue.text = getString(R.string.Male)
                                false -> tvGenderValue.text = getString(R.string.Female)
                                else -> tvGenderValue.text = ""
                            }
                        }
                }
                firestore.collection("DriverFeedbacks")
                    .whereEqualTo("driver_ID", driverID)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            binding.bottomSheetDriverPersonalInfo.tvNotHaveReview.isVisible = true
                            binding.bottomSheetDriverPersonalInfo.recyclerViewFeedback.isVisible =
                                false
                        } else {
                            binding.bottomSheetDriverPersonalInfo.tvNotHaveReview.isVisible = false
                            binding.bottomSheetDriverPersonalInfo.recyclerViewFeedback.isVisible =
                                true
                        }
                        driverFeedbackAdapter.updateData(documents.toObjects(DriverFeedback::class.java))
                        binding.driverFoundNotification.isVisible = false
                        bottomSheetBookingSuccess.state = BottomSheetBehavior.STATE_COLLAPSED
                        bottomSheetDriverPersonalInfo.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    .addOnFailureListener { exception ->
                        Log.w("Firestore", "Error getting documents: ", exception)
                    }
            }
        }

        with(binding.bottomSheetOnGoing) {
            ratingButton.setOnClickListener {
                if (!isRating) {
                    bottomSheetOnGoing.state = BottomSheetBehavior.STATE_COLLAPSED
                    binding.title.text = getString(R.string.SendRating)
                    with(binding.bottomSheetRating) {
                        driversCollection.document(driverID).get()
                            .addOnSuccessListener { document ->
                                tvDriverName.text = buildString {
                                    append(document.getString("lastname"))
                                    append(" ")
                                    append(document.getString("firstname"))
                                }
                                tvLicensePlate.text = document.getString("license_Plate")
                                tvVehicleType.text = buildString {
                                    append(document.getString("vehicle_Brand"))
                                    append(" ")
                                    append(document.getString("vehicle_Line"))
                                }
                            }
                    }
                    bottomSheetRating.state = BottomSheetBehavior.STATE_EXPANDED
                    binding.backButton2.isVisible = true
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.YouHaveRated),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            reportButton.setOnClickListener {
                bottomSheetOnGoing.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.title.text = getString(R.string.SendReport)
                bottomSheetReport.state = BottomSheetBehavior.STATE_EXPANDED
                binding.backButton2.isVisible = true
            }
            personalInfoLayout.setOnClickListener {
                with(binding.bottomSheetDriverPersonalInfo) {
                    driversCollection.document(driverID).get()
                        .addOnSuccessListener { document ->
                            tvDriverNamePersonalInfo.text = buildString {
                                append(document.getString("lastname"))
                                append(" ")
                                append(document.getString("firstname"))
                            }
                            tvVehicleLinePersonalInfo.text = buildString {
                                append(document.getString("vehicle_Brand"))
                                append(" ")
                                append(document.getString("vehicle_Line"))
                            }
                            tvRatingAverage.text = calculateRateAverage(document).toString()
                            tvTotalDistance.text =
                                getString(R.string.TotalKm, document.getLong("totalDistance"))
                            tvTotalTripNum.text =
                                getString(R.string.TotalTrip, document.getLong("completeTripNum"))

                            when (document.getString("classify")) {
                                Constants.BIKE -> {
                                    tvVehicleTypeValue.text = getString(R.string.Bike)
                                }

                                Constants.CAR -> {
                                    tvVehicleTypeValue.text = getString(R.string.Car)
                                }

                                Constants.MVP -> {
                                    tvVehicleTypeValue.text = getString(R.string.MVP)
                                }

                                else -> {
                                    tvVehicleTypeValue.text = ""
                                }
                            }
                            tvLicensePlateValue.text = document.getString("license_Plate")
                            when (document.getBoolean("gender")) {
                                true -> tvGenderValue.text = getString(R.string.Male)
                                false -> tvGenderValue.text = getString(R.string.Female)
                                else -> tvGenderValue.text = ""
                            }
                        }
                }
                firestore.collection("DriverFeedbacks")
                    .whereEqualTo("driver_ID", driverID)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            binding.bottomSheetDriverPersonalInfo.tvNotHaveReview.isVisible = true
                            binding.bottomSheetDriverPersonalInfo.recyclerViewFeedback.isVisible =
                                false
                        } else {
                            binding.bottomSheetDriverPersonalInfo.tvNotHaveReview.isVisible = false
                            binding.bottomSheetDriverPersonalInfo.recyclerViewFeedback.isVisible =
                                true
                        }
                        driverFeedbackAdapter.updateData(documents.toObjects(DriverFeedback::class.java))
                        isFromBottomSheetOnGoing = true
                        bottomSheetOnGoing.state = BottomSheetBehavior.STATE_COLLAPSED
                        bottomSheetDriverPersonalInfo.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    .addOnFailureListener { exception ->
                        Log.w("Firestore", "Error getting documents: ", exception)
                    }
            }
        }

        with(binding.bottomSheetDriverPersonalInfo) {
            cvRateAverage.setOnClickListener {
                Toast.makeText(
                    this@HomeActivity,
                    getString(R.string.AverageNumberOfStars),
                    Toast.LENGTH_SHORT
                ).show()
            }
            cvTotalDistance.setOnClickListener {
                Toast.makeText(
                    this@HomeActivity,
                    getString(R.string.TotalDistanceTraveled),
                    Toast.LENGTH_SHORT
                ).show()
            }
            cvTotalTripNum.setOnClickListener {
                Toast.makeText(
                    this@HomeActivity,
                    getString(R.string.NumberOfCompletedTrips),
                    Toast.LENGTH_SHORT
                ).show()
            }
            backButtonPersonalInfo.setOnClickListener {
                if (isFromBottomSheetOnGoing) {
                    bottomSheetDriverPersonalInfo.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetOnGoing.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    bottomSheetDriverPersonalInfo.state = BottomSheetBehavior.STATE_COLLAPSED
                    binding.driverFoundNotification.isVisible = true
                    bottomSheetBookingSuccess.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        with(binding.bottomSheetRating) {
            var ratingStar = 0
            ratingBar.setOnRatingChangeListener { _, rating ->
                ratingStar = rating.toInt()
                if (rating >= 4) {
                    binding.bottomSheetRating.tvExcellent.isVisible = true
                } else {
                    binding.bottomSheetRating.tvExcellent.isVisible = false
                }
            }
            feedbackButton.setOnClickListener {
                if (ratingStar > 0) {
                    val feedback = etFeedback.text.toString()
                    if (driverFeedbackId.isEmpty()) {
                        driverFeedbackId = firestore.collection("DriverFeedbacks").document().id
                    }
                    driversCollection.document(driverID).update(
                        mapOf(
                            "totalStar" to FieldValue.increment(ratingStar.toLong()),
                            "rateStarNum" to FieldValue.increment(1)
                        )
                    )
                    firestore.collection("DriverFeedbacks").document(driverFeedbackId)
                        .set(
                            mapOf(
                                "driverFeedback_ID" to driverFeedbackId,
                                "driver_ID" to driverID,
                                "passenger_ID" to passengerID,
                                "star" to ratingStar,
                                "feedback" to feedback,
                                "driverFeedbackTime" to Date().toString()
                            ),
                            SetOptions.merge()
                        )
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@HomeActivity,
                                getString(R.string.RatingSubmitted),
                                Toast.LENGTH_SHORT
                            ).show()
                            bottomSheetRating.state = BottomSheetBehavior.STATE_COLLAPSED
                            if (isReceiptOpened) {
                                bottomSheetReceipt.state = BottomSheetBehavior.STATE_EXPANDED
                                binding.title.isVisible = false
                            } else {
                                bottomSheetOnGoing.state = BottomSheetBehavior.STATE_EXPANDED
                            }

                            binding.title.text = getString(R.string.OnTheTrip)
                            binding.backButton2.isVisible = false
                            ratingStar = 0
                            etFeedback.text.clear()
                        }
                    tripsCollection.document(tripID).collection("Reason").document(reasonID)
                        .update(
                            mapOf(
                                "feedbackDriverRef" to firestore.collection("DriverFeedbacks")
                                    .document(driverFeedbackId)
                            )
                        )
                    isRating = true
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.PleaseRate),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.backButton2.setOnClickListener {
            bottomSheetRating.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetReport.state = BottomSheetBehavior.STATE_COLLAPSED
            if (isReceiptOpened) {
                bottomSheetReceipt.state = BottomSheetBehavior.STATE_EXPANDED
                binding.title.isVisible = false
            } else {
                bottomSheetOnGoing.state = BottomSheetBehavior.STATE_EXPANDED
            }
            binding.title.text = getString(R.string.OnTheTrip)
            binding.backButton2.isVisible = false
            binding.bottomSheetRating.ratingBar.rating = 0f
            binding.bottomSheetRating.etFeedback.text.clear()
            binding.bottomSheetReport.radioGroupReportReasons.clearCheck()
            binding.bottomSheetReport.etReportFeedback.text.clear()
        }

        with(binding.bottomSheetReport) {
            var reportReason = ""
            radioGroupReportReasons.setOnCheckedChangeListener { _, checkId ->
                reportReason = when (checkId) {
                    R.id.radioReportReason1 -> getString(R.string.ReportReason1)
                    R.id.radioReportReason2 -> getString(R.string.ReportReason2)
                    R.id.radioReportReason3 -> getString(R.string.ReportReason3)
                    R.id.radioReportReason4 -> getString(R.string.ReportReason4)
                    R.id.radioReportReason5 -> getString(R.string.ReportReason5)
                    else -> ""
                }
            }
            sendReportButton.setOnClickListener {
                if (reportReason.isNotEmpty()) {
                    if (driverFeedbackId.isEmpty()) {
                        driverFeedbackId = firestore.collection("DriverFeedbacks").document().id
                    }
                    val reportDetail = etReportFeedback.text.toString()
                    firestore.collection("DriverFeedbacks").document(driverFeedbackId)
                        .set(
                            mapOf(
                                "driverFeedback_ID" to driverFeedbackId,
                                "driver_ID" to driverID,
                                "passenger_ID" to passengerID,
                                "reportDriverReason" to reportReason,
                                "reportDriverReasonDetail" to reportDetail,
                                "driverFeedbackTime" to Date().toString()
                            ),
                            SetOptions.merge()
                        )
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@HomeActivity,
                                getString(R.string.ReportSubmitted),
                                Toast.LENGTH_SHORT
                            ).show()
                            bottomSheetReport.state = BottomSheetBehavior.STATE_COLLAPSED
                            if (isReceiptOpened) {
                                bottomSheetReceipt.state = BottomSheetBehavior.STATE_EXPANDED
                                binding.title.isVisible = false
                            } else {
                                bottomSheetOnGoing.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                            binding.title.text = getString(R.string.OnTheTrip)
                            binding.backButton2.isVisible = false
                            radioGroupReportReasons.clearCheck()
                            etReportFeedback.text.clear()
                        }
                    tripsCollection.document(tripID).collection("Reason").document(reasonID)
                        .update(
                            mapOf(
                                "feedbackDriverRef" to firestore.collection("DriverFeedbacks")
                                    .document(driverFeedbackId)
                            )
                        )
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.PleaseChooseReason),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        with(binding.bottomSheetCancellation) {
            var cancelReason = ""
            radioGroupReasons.setOnCheckedChangeListener { _, checkId ->
                cancelReason = when (checkId) {
                    R.id.radioReason1 -> getString(R.string.CancellationReason1)
                    R.id.radioReason2 -> getString(R.string.CancellationReason2)
                    R.id.radioReason3 -> getString(R.string.CancellationReason3)
                    R.id.radioReason4 -> getString(R.string.CancellationReason4)
                    R.id.radioReason5 -> getString(R.string.CancellationReason5)
                    else -> ""
                }
            }
            sendCancellationButton.setOnClickListener {
                if (cancelReason.isNotEmpty()) {
                    bottomSheetCancellation.state = BottomSheetBehavior.STATE_COLLAPSED
                    tripsCollection.document(tripID).collection("Reason").document(reasonID)
                        .update(
                            mapOf(
                                "passengerCancelReason" to cancelReason
                            )
                        )
                    radioGroupReasons.clearCheck()
                    tripID = ""
                    reasonID = ""
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.PleaseChooseReason),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        with(binding.bottomSheetDriverCancel) {
            findAnotherDriverButton.setOnClickListener {
                exceptDriverID = driverID
                bottomSheetDriverCancel.state = BottomSheetBehavior.STATE_COLLAPSED
                setUpUIWhenBooking()
                setUpDataWhenBooking()
                listenerDriver?.remove()

                mGoogleMap?.clear()
                // Tilt
                val cameraPos = CameraPosition.Builder().target(originLatLng!!)
                    .tilt(45f)
                    .zoom(16f)
                    .build()
                mGoogleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos))

                // Start animation
                addMarkerWithPulseAnimation()
            }
            notFindAnotherDriverButton.setOnClickListener {
                resetAfterCancelBooking(isDeleteTrip = false)
                bottomSheetDriverCancel.state = BottomSheetBehavior.STATE_COLLAPSED
                tripID = ""
            }
        }

        with(binding.bottomSheetReceipt) {
            ratingButtonReceipt.setOnClickListener {
                if (!isRating) {
                    bottomSheetReceipt.state = BottomSheetBehavior.STATE_COLLAPSED
                    binding.title.text = getString(R.string.SendRating)
                    binding.title.isVisible = true
                    with(binding.bottomSheetRating) {
                        driversCollection.document(driverID).get()
                            .addOnSuccessListener { document ->
                                tvDriverName.text = buildString {
                                    append(document.getString("lastname"))
                                    append(" ")
                                    append(document.getString("firstname"))
                                }
                                tvLicensePlate.text = document.getString("license_Plate")
                                tvVehicleType.text = buildString {
                                    append(document.getString("vehicle_Brand"))
                                    append(" ")
                                    append(document.getString("vehicle_Line"))
                                }
                            }
                    }
                    bottomSheetRating.state = BottomSheetBehavior.STATE_EXPANDED
                    binding.backButton2.isVisible = true
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.YouHaveRated),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            reportButtonReceipt.setOnClickListener {
                bottomSheetReceipt.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.title.text = getString(R.string.SendReport)
                binding.title.isVisible = true
                bottomSheetReport.state = BottomSheetBehavior.STATE_EXPANDED
                binding.backButton2.isVisible = true
            }
            completeReceiptButton.setOnClickListener {
                if (isDriverConfirm) {
                    listenerDriver?.remove()
                    mGoogleMap?.clear()
                    binding.title.isVisible = false
                    bottomSheetReceipt.state = BottomSheetBehavior.STATE_COLLAPSED
                    resetAfterCancelBooking(isDeleteTrip = false)
                    tripID = ""
                    reasonID = ""
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.ThankYou),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.WaitDriverConfirm),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        binding.bottomSheetBookingFail.tryAgainButton.setOnClickListener {
            bottomSheetBookingFail.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        mGoogleMap!!.setMaxZoomPreference(20.0f)
        mGoogleMap!!.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
        )
        mGoogleMap?.setOnMapLongClickListener(mapLongClickListener)
        mGoogleMap?.setOnMarkerClickListener(markerClickListener)
    }

    private fun addDefaultMarker(position: LatLng): Marker? {
        return mGoogleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .title("Custom Default Marker")
                .draggable(false)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(resources, R.drawable.marker_icon),
                            130,
                            130,
                            false
                        )
                    )
                )
        )
    }

    private fun addStartedMarker(position: LatLng): Marker? {
        return mGoogleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .title("Custom Started Marker")
                .draggable(false)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(resources, R.drawable.started_marker_icon),
                            50,
                            50,
                            false
                        )
                    )
                )
        )
    }

    private fun addMotoMarker(position: LatLng): Marker? {
        return mGoogleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .title("Custom Started Marker")
                .draggable(false)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(resources, R.drawable.moto_top),
                            66,
                            112,
                            false
                        )
                    )
                )
        )
    }

    private fun addCarMarker(position: LatLng): Marker? {
        return mGoogleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .title("Custom Started Marker")
                .draggable(false)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(resources, R.drawable.car_top),
                            56,
                            114,
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

    private fun addMarkerWithPulseAnimation() {
        originLatLng?.let { addDefaultMarker(it) }
        addPulsatingEffect(originLatLng)
    }

    private fun addPulsatingEffect(originLatLng: LatLng?, isStartSpinningAnimate: Boolean = true) {
        lastPulseAnimator?.cancel()
        if (lastUserCircle != null) {
            lastUserCircle?.remove()
            lastUserCircle = null
        }

        lastPulseAnimator = Utils.valueAnimate(EFFECT_DURATION) { p0 ->
            val animatedRadius = p0.animatedValue.toString().toDouble()
            if (lastUserCircle == null) {
                lastUserCircle = mGoogleMap!!.addCircle(
                    CircleOptions()
                        .center(originLatLng!!)
                        .radius(animatedRadius)
                        .strokeColor(Color.WHITE)
                        .fillColor(ContextCompat.getColor(this@HomeActivity, R.color.blue_200))
                )
            } else {
                lastUserCircle!!.radius = animatedRadius
            }
        }

        // Start rotating camera
        if (isStartSpinningAnimate) {
            startMapCameraSpinningAnimation(mGoogleMap?.cameraPosition?.target)
        }
    }

    private fun startMapCameraSpinningAnimation(target: LatLng?) {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, (DESIRED_NUM_OF_SPINS * 360).toFloat())
        animator?.duration =
            (DESIRED_NUM_OF_SPINS * DESIRED_SECOND_PER_ONE_FULL_360_SPIN * 1000).toLong()
        animator?.repeatCount = ValueAnimator.INFINITE
        animator?.repeatMode = ValueAnimator.RESTART
        animator?.interpolator = LinearInterpolator()
        animator?.startDelay = 100
        animator?.addUpdateListener { valueAnimator ->
            val newBearingValue = valueAnimator.animatedValue as Float
            mGoogleMap?.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(target!!)
                        .zoom(15f)
                        .tilt(45f)
                        .bearing(newBearingValue)
                        .build()
                )
            )
        }
        animator?.start()
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

    override fun onDestroy() {
        animator?.end()
        super.onDestroy()
    }

    private fun direction(
        origin: LatLng,
        destination: LatLng,
        mode: String = "driving",
        shouldClearMap: Boolean = true
    ) {
        if (shouldClearMap) {
            mGoogleMap?.clear()
        }
        val requestQueue = Volley.newRequestQueue(this)
        val url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
            .buildUpon()
            .appendQueryParameter(
                "destination",
                "${destination.latitude}, ${destination.longitude}"
            )
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
                                distance = legs.getJSONObject(j).getJSONObject("distance")
                                    .getString("value").toDouble()
                                duration = legs.getJSONObject(j).getJSONObject("duration")
                                    .getString("value").toInt()
                                setVehicleTypeValue()

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
                            polylineOptions.width(15F)
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
                        addStartedMarker(origin)
                        addDefaultMarker(destination)

                        val bounds: LatLngBounds = LatLngBounds.builder()
                            .include(origin)
                            .include(destination)
                            .build()
                        val point = Point()
                        windowManager.defaultDisplay.getSize(point)
                        mGoogleMap?.setPadding(60, 0, 60, 600)
                        mGoogleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                point.x,
                                200,
                                60
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

    private fun findAddress(latLng: LatLng, isCurrentLocation: Boolean = false) {
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
                    originAddress = results.getJSONObject(0).getString("formatted_address")
                    with(binding) {
                        title.text = originAddress
                        title.isVisible = true
                        if (isCurrentLocation) {
                            subTitle.text = getString(R.string.YourLocation)
                            subTitle.isVisible = true
                        } else {
                            subTitle.isVisible = false
                        }
                    }
                    Log.d("Geocode", "Address: $originAddress")
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

    private fun setVehicleTypeValue() {
        binding.bottomSheetVehicleType.moneyBike.text =
            distance.convertMetersToKilometers().calculateMoney(Constants.BIKE).formatCurrency()
        binding.bottomSheetVehicleType.moneyCar.text =
            distance.convertMetersToKilometers().calculateMoney(Constants.CAR).formatCurrency()
        binding.bottomSheetVehicleType.moneyMvp.text =
            distance.convertMetersToKilometers().calculateMoney(Constants.MVP).formatCurrency()
        binding.bottomSheetVehicleType.TvEstimateTime.text =
            getString(R.string.TripTime, duration.convertSecondsToMinutes().toString())
    }

    private fun resetValue() {
        if (destinationLatLng != null && !showedDirection) {
            with(binding) {
                subTitle.text = ""
                subTitle.isVisible = false
                title.text = ""
                title.isVisible = false
                chooseLocationButton.isVisible = false
                menuButton.isVisible = true
                backButton.isVisible = false
            }
        } else {
            with(binding) {
                subTitle.text = ""
                subTitle.isVisible = false
                title.text = ""
                title.isVisible = false
                chooseLocationButton.isVisible = false
                currentLocationButton.isVisible = true
                searchView.isVisible = true
                menuButton.isVisible = true
                backButton.isVisible = false
                bottomSheetPaymentType.cardMomo.elevation = 1f
                bottomSheetPaymentType.cardCash.elevation = 1f
            }
            showedDirection = false
            bottomSheetVehicleType.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetPaymentType.state = BottomSheetBehavior.STATE_COLLAPSED
            mGoogleMap?.setOnMapLongClickListener(mapLongClickListener)
            mGoogleMap?.setOnMarkerClickListener(markerClickListener)
            mGoogleMap?.setPadding(0, 0, 0, 0)
        }
        mGoogleMap?.clear()
        destinationLatLng = null
        destinationAddress = null
        originLatLng = null
        originAddress = null
        distance = 0.0
        duration = 0
        vehicleType = null
        paymentType = null
    }

    private fun setUpUIWhenBooking() {
        with(binding) {
            bottomSheetPaymentType.cardMomo.elevation = 1f
            bottomSheetPaymentType.cardCash.elevation = 1f
            this@HomeActivity.bottomSheetPaymentType.state = BottomSheetBehavior.STATE_COLLAPSED

            title.isVisible = false
            subTitle.isVisible = false
            backButton.isVisible = false

            this@HomeActivity.bottomSheetBookingInfo.state = BottomSheetBehavior.STATE_EXPANDED
            with(bottomSheetBookingInfo) {
                tvTimeBooking.text = getCurrentTimeFormatted()
                tvOriginAddress.text = originAddress
                tvDestinationAddress.text = destinationAddress
                when (paymentType) {
                    Constants.CASH -> tvPaymentType.text = getString(R.string.Cash)
                    Constants.MOMO -> tvPaymentType.text = getString(R.string.Momo)
                }
            }
        }
    }

    private fun setUpDataWhenBooking() {
        tripID = tripsCollection.document().id
        val tripData = TripData(
            trip_ID = tripID,
            originLatLng = GeoPoint(originLatLng!!.latitude, originLatLng!!.longitude),
            originAddress = originAddress,
            destinationLatLng = GeoPoint(
                destinationLatLng!!.latitude,
                destinationLatLng!!.longitude
            ),
            destinationAddress = destinationAddress,
            vehicleType = vehicleType,
            paymentType = paymentType,
            price = distance.convertMetersToKilometers().calculateMoney(vehicleType!!),
            distance = distance,
            duration = duration,
            bookingTime = Date().toString(),
            status = Constants.NEW,
            passenger_ID = passengerID
        )
        tripsCollection.document(tripID).set(tripData)
            .addOnSuccessListener {
                reasonID =
                    tripsCollection.document(tripID).collection("Reason").document().id
                val reasonData = ReasonData(
                    reason_ID = reasonID
                )
                tripsCollection.document(tripID).collection("Reason").document(reasonID)
                    .set(reasonData)
                tripsCollection.document(tripID).update(
                    mapOf(
                        "reason_ID" to reasonID
                    )
                )
                findDriver(tripID)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@HomeActivity,
                    "Database Error: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun findDriver(tripID: String, volume: Int = 1) {
        if (volume <= 3) {
            val center = GeoLocation(originLatLng?.latitude!!, originLatLng?.longitude!!)
            val radiusInM =
                ((Constants.MIN_RADIUS + ((volume - 1) * Constants.INCREASE_RADIUS_RATIO)) * Constants.KM)

            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
            val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
            for (b in bounds) {
                val q = firestore.collection("Drivers")
                    .orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash)
                tasks.add(q.get())
            }

            Tasks.whenAllComplete(tasks)
                .addOnCompleteListener {
                    val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                    for (task in tasks) {
                        val snap = task.result
                        for (doc in snap!!.documents) {
                            val driverLat = doc.getDouble("current_Lat") ?: 0.0
                            val driverLng = doc.getDouble("current_Lng") ?: 0.0

                            // We have to filter out a few false positives due to GeoHash
                            // accuracy, but most will match
                            val docLocation = GeoLocation(driverLat, driverLng)
                            val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                            if (distanceInM <= radiusInM) {
                                matchingDocs.add(doc)
                            }
                        }
                    }
                    // except driverID in matchingDocs
                    if (exceptDriverID.isNotEmpty()) {
                        matchingDocs.removeIf { it.id == exceptDriverID }
                    }
                    // Filter by vehicle type
                    matchingDocs.removeIf { it.getString("classify") != vehicleType }

                    // matchingDocs contains the results
                    if (matchingDocs.isNotEmpty()) {
                        notifyDrivers(matchingDocs, tripID)
                    } else {
                        findDriver(tripID, volume + 1)
                    }
                }
        } else {
            resetAfterCancelBooking()
        }
    }

    private fun notifyDrivers(
        matchingDocs: MutableList<DocumentSnapshot>,
        tripID: String
    ) {
        val tripRef = tripsCollection.document(tripID)

        tripRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val status = document.getString("status")
                if (status == Constants.NEW) {
                    notifyEachDriver(matchingDocs, tripID)
                }
            }
        }.addOnFailureListener { e ->
            Log.w("TripStatus", "Error getting document", e)
        }

        listenerTrip = tripRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("FirestoreListener", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val status = snapshot.getString("status")
                if (status == Constants.REFUSE) {
                    if (matchingDocs.size > 1) {
                        matchingDocs.removeFirst()
                        notifyEachDriver(matchingDocs, tripID)
                    } else {
                        vibrateCustomPattern(this, times = 1, duration = 250L, interval = 500L)
                        resetAfterCancelBooking()
                    }
                }
                if (status == Constants.ACCEPT) {
                    vibrateCustomPattern(this, times = 1, duration = 250L, interval = 500L)
                    animator?.cancel()
                    mGoogleMap?.moveCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder().target(originLatLng!!)
                                .tilt(0f)
                                .zoom(15f)
                                .build()
                        )
                    )

                    driverID = snapshot.getString("driver_ID")!!
                    binding.title.isVisible = true
                    binding.title.text = getString(R.string.DriverIsComing)
                    binding.driverFoundNotificationContent.text = getString(R.string.DriverFound)
                    binding.driverFoundNotification.isVisible = true
                    bottomSheetBookingInfo.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetBookingSuccess.state = BottomSheetBehavior.STATE_EXPANDED
                    with(binding.bottomSheetBookingSuccess) {
                        driversCollection.document(driverID).get()
                            .addOnSuccessListener { document ->
                                tvDriverName.text = buildString {
                                    append(document.getString("lastname"))
                                    append(" ")
                                    append(document.getString("firstname"))
                                }
                                tvRating.text = calculateRateAverage(document).toString()
                                tvLicensePlate.text = document.getString("license_Plate")
                                tvVehicleType.text = buildString {
                                    append(document.getString("vehicle_Brand"))
                                    append(" ")
                                    append(document.getString("vehicle_Line"))
                                }
                            }
                    }
                    addDefaultMarker(originLatLng!!)
                    listenerDriverLocation(driverID)
                }
                if (status == Constants.DRIVER_CANCEL || status == Constants.DRIVER_CANCEL_EMERGENCY) {
                    vibrateCustomPattern(this, times = 1, duration = 250L, interval = 500L)
                    listenerDriver?.remove()
                    mGoogleMap?.clear()
                    binding.driverFoundNotification.isVisible = false
                    binding.title.isVisible = false
                    bottomSheetBookingSuccess.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetOnGoing.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetRating.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetReport.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetDriverCancel.state = BottomSheetBehavior.STATE_EXPANDED
                }
                if (status == Constants.PICK_UP_POINT) {
                    vibrateCustomPattern(this, times = 3, duration = 250L, interval = 400L)
                    binding.title.text = getString(R.string.DriverArrived)
                    binding.driverFoundNotificationContent.text =
                        getString(R.string.DriverArrivedNotification)
                }
                if (status == Constants.GOING) {
                    binding.title.text = getString(R.string.OnTheTrip)
                    binding.driverFoundNotification.isVisible = false
                    bottomSheetBookingSuccess.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetOnGoing.state = BottomSheetBehavior.STATE_EXPANDED

                    listenerDriver?.remove()
                    animator?.cancel()
                    mGoogleMap?.moveCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder().target(originLatLng!!)
                                .tilt(0f)
                                .zoom(14f)
                                .build()
                        )
                    )
                    mGoogleMap?.clear()
                    listenerDriverInTripLocation(driverID)

                    direction(originLatLng!!, destinationLatLng!!, shouldClearMap = false)

                    with(binding.bottomSheetOnGoing) {
                        driversCollection.document(driverID).get()
                            .addOnSuccessListener { document ->
                                tvDriverName.text = buildString {
                                    append(document.getString("lastname"))
                                    append(" ")
                                    append(document.getString("firstname"))
                                }
                                tvRating.text = calculateRateAverage(document).toString()
                                tvLicensePlate.text = document.getString("license_Plate")
                                tvVehicleType.text = buildString {
                                    append(document.getString("vehicle_Brand"))
                                    append(" ")
                                    append(document.getString("vehicle_Line"))
                                }
                            }
                    }
                }
                if (status == Constants.ARRIVE) {
                    mGoogleMap?.clear()
                    binding.title.isVisible = false
                    binding.backButton2.isVisible = false
                    bottomSheetOnGoing.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetReport.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetRating.state = BottomSheetBehavior.STATE_COLLAPSED
                    binding.bottomSheetRating.ratingBar.rating = 0f
                    binding.bottomSheetRating.etFeedback.text.clear()
                    binding.bottomSheetReport.radioGroupReportReasons.clearCheck()
                    binding.bottomSheetReport.etReportFeedback.text.clear()

                    tripsCollection.document(tripID).get().addOnSuccessListener { document ->
                        binding.bottomSheetReceipt.tvTimeBookingReceipt.text = getHourAndMinute(
                            document.getString("startTime")!!
                        )
                        binding.bottomSheetReceipt.tvTimeCompleteReceipt.text = getHourAndMinute(
                            document.getString("endTime")!!
                        )
                        binding.bottomSheetReceipt.tvOriginAddressReceipt.text =
                            document.getString("originAddress")
                        binding.bottomSheetReceipt.tvDestinationAddressReceipt.text =
                            document.getString("destinationAddress")
                        val tripPaymentType = document.getString("paymentType")!!
                        when (tripPaymentType) {
                            Constants.CASH -> binding.bottomSheetReceipt.tvPaymentTypeReceipt.text =
                                getString(R.string.Cash)

                            Constants.MOMO -> binding.bottomSheetReceipt.tvPaymentTypeReceipt.text =
                                getString(R.string.Momo)
                        }
                        binding.bottomSheetReceipt.tvPriceReceipt.text =
                            document.getDouble("price")?.formatCurrency()
                        binding.bottomSheetReceipt.tvBookingTimeReceipt.text =
                            document.getString("bookingTime")
                        binding.bottomSheetReceipt.tvTravelDistanceReceipt.text = getString(
                            R.string.TravelDistance,
                            document.getDouble("distance")?.convertMetersToKilometers()?.toInt()
                        )
                        binding.bottomSheetReceipt.TvExactTimeReceipt.text = getString(
                            R.string.ReceiptTime,
                            calculateMinutesDifference(
                                document.getString("startTime")!!,
                                document.getString("endTime")!!
                            )
                        )
                        isReceiptOpened = true
                        bottomSheetReceipt.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
                if (status == Constants.COMPLETED) {
                    val bonusPoint = Math.round(snapshot.getDouble("price")?.div(1000.0) ?: 0.0)
                    Toast.makeText(
                        this,
                        getString(R.string.DriverConfirmCompleted, bonusPoint),
                        Toast.LENGTH_LONG
                    ).show()
                    isDriverConfirm = true
                }
            } else {
                Log.d("FirestoreListener", "Document does not exist.")
            }
        }
    }

    private fun listenerDriverLocation(driverId: String) {
        val driverRef = driversCollection.document(driverId)
        listenerDriver = driverRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("DriverLocationListener", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val driverLat = snapshot.getDouble("current_Lat") ?: 0.0
                val driverLng = snapshot.getDouble("current_Lng") ?: 0.0
                val driverLocation = LatLng(driverLat, driverLng)
                mGoogleMap?.clear()
                addDefaultMarker(originLatLng!!)
                if (vehicleType == Constants.BIKE) {
                    addMotoMarker(driverLocation)
                } else {
                    addCarMarker(driverLocation)
                }
                addPulsatingEffect(originLatLng, isStartSpinningAnimate = false)
            } else {
                Log.d("DriverLocationListener", "Document does not exist.")
            }
        }
    }

    private fun listenerDriverInTripLocation(driverId: String) {
        val driverRef = driversCollection.document(driverId)
        listenerDriver = driverRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("DriverLocationListener", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val driverLat = snapshot.getDouble("current_Lat") ?: 0.0
                val driverLng = snapshot.getDouble("current_Lng") ?: 0.0
                val driverLocation = LatLng(driverLat, driverLng)
                currentMarkerInTrip?.remove()
                currentMarkerInTrip =
                    if (vehicleType == Constants.BIKE) addMotoMarker(driverLocation) else addCarMarker(
                        driverLocation
                    )
                mGoogleMap?.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(driverLocation).zoom(15f).build()
                    )
                )
            } else {
                Log.d("DriverLocationListener", "Document does not exist.")
            }
        }
    }

    private fun notifyEachDriver(
        matchingDocs: MutableList<DocumentSnapshot>,
        tripID: String
    ) {
        val tripRef = tripsCollection.document(tripID)
        tripRef.update("status", Constants.WAITING)

        val updates: MutableMap<String, Any> = mutableMapOf(
            "trip_ID" to tripID,
        )
        driverID = matchingDocs[0].id
        val driverRef = driversCollection.document(driverID)
        driverRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val ready = document.getBoolean("ready") ?: false
                if (ready) {
                    driverRef.update(updates)
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun calculateRateAverage(document: DocumentSnapshot): Double {
        val rateStarNum = document.getDouble("rateStarNum") ?: 0.0
        val rateAverage = if (rateStarNum != 0.0) {
            document.getDouble("totalStar")?.div(rateStarNum) ?: 5.0
        } else {
            5.0
        }
        val formattedRate = String.format("%.1f", rateAverage).replace(",", ".")
        return formattedRate.toDoubleOrNull() ?: 5.0
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
    private fun logout(currentUser: FirebaseUser?) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.LogoutTitle))
            .setMessage(getString(R.string.AreYouSureLogout))
            .setNegativeButton(getString(R.string.Logout)) { _, _ ->
                if (currentUser != null) {
                    auth.signOut()
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setPositiveButton(getString(R.string.Cancel), null)
            .show()
    }

    private fun resetAfterCancelBooking(isDeleteTrip: Boolean = true) {
        resetValue()
        bottomSheetBookingInfo.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBookingSuccess.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.driverFoundNotification.isVisible = false
        animator?.cancel()
        mGoogleMap?.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(mGoogleMap?.cameraPosition!!.target)
                    .tilt(0f)
                    .zoom(14f)
                    .build()
            )
        )
        if (isDeleteTrip) {
            bottomSheetBookingFail.state = BottomSheetBehavior.STATE_EXPANDED
            tripsCollection.document(tripID).collection("Reason").document(reasonID).delete()
            tripsCollection.document(tripID).delete()
            this.tripID = ""
            reasonID = ""
        }
        listenerTrip?.remove()
        listenerDriver?.remove()
        driverID = ""
        exceptDriverID = ""
        driverFeedbackId = ""
        isRating = false
        isReceiptOpened = false
        isDriverConfirm = false
        isFromBottomSheetOnGoing = false
    }
}
