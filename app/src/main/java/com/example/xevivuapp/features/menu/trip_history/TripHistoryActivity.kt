package com.example.xevivuapp.features.menu.trip_history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.xevivuapp.MainActivity
import com.example.xevivuapp.R
import com.example.xevivuapp.common.adapter.TripHistoryAdapter
import com.example.xevivuapp.common.utils.Utils.toDate
import com.example.xevivuapp.data.TripDataDto
import com.example.xevivuapp.databinding.ActivityTripHistoryBinding
import com.example.xevivuapp.features.booking.HomeActivity
import com.example.xevivuapp.features.menu.personal_info.PersonalInfoActivity
import com.example.xevivuapp.features.menu.points.PointActivity
import com.example.xevivuapp.features.menu.setting.SettingActivity
import com.example.xevivuapp.features.menu.support.SupportActivity
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class TripHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var tripsCollection: CollectionReference
    private var passengerID: String = ""

    private lateinit var sideSheetMenu: SideSheetBehavior<View>

    // Trip history
    private var tripHistoryList = mutableListOf<TripDataDto>()

    // Adapter
    private val tripHistoryAdapter: TripHistoryAdapter by lazy { TripHistoryAdapter(this) }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels / resources.displayMetrics.density
        settingRecyclerViewHeight(screenHeight)

        firestore = FirebaseFirestore.getInstance()
        tripsCollection = firestore.collection("Trips")
        passengerID = intent.getStringExtra("Passenger_ID").toString()

        // Set up feedback adapter
        binding.recyclerViewTripHistory.adapter = tripHistoryAdapter

        // Set up sideSheetMenu
        sideSheetMenu = SideSheetBehavior.from(findViewById(R.id.sideSheetMenu))
        sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
        sideSheetMenu.isDraggable = true

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        binding.menuButton.setOnClickListener {
            binding.dimOverlay.isVisible = true
            binding.sideSheetMenu.sideSheetMenuItem3.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.button_background
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
                }
            }
        }
        sideSheetMenu.addCallback(sideSheetCallback)

        with(binding.sideSheetMenu) {
            sideSheetMenuItem1.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@TripHistoryActivity, HomeActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem2.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@TripHistoryActivity, PersonalInfoActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem4.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@TripHistoryActivity, PointActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem5.setOnClickListener {
                Toast.makeText(
                    this@TripHistoryActivity,
                    getString(R.string.FeatureInDevelop),
                    Toast.LENGTH_LONG
                ).show()
            }
            sideSheetMenuItem6.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@TripHistoryActivity, SettingActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem7.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@TripHistoryActivity, SupportActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem8.setOnClickListener {
                logout(currentUser)
            }
        }

        setUpData()
    }

    private fun setUpData() {
        tripsCollection.whereEqualTo("passenger_ID", passengerID).get()
            .addOnSuccessListener { documents ->
                tripHistoryList = documents.toObjects(TripDataDto::class.java)
                val sortedTripHistoryList = tripHistoryList.sortedByDescending { it.bookingTime?.toDate() }
                tripHistoryAdapter.updateData(sortedTripHistoryList)
            }
    }

    private fun settingRecyclerViewHeight(screenHeight: Float) {
        if (screenHeight < 700) {
            binding.recyclerViewTripHistory.layoutParams.height =
                (575 * resources.displayMetrics.density).toInt()
        }
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
}
