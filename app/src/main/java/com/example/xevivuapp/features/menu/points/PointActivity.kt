package com.example.xevivuapp.features.menu.points

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.xevivuapp.MainActivity
import com.example.xevivuapp.R
import com.example.xevivuapp.databinding.ActivityPointBinding
import com.example.xevivuapp.features.booking.HomeActivity
import com.example.xevivuapp.features.menu.personal_info.PersonalInfoActivity
import com.example.xevivuapp.features.menu.setting.SettingActivity
import com.example.xevivuapp.features.menu.support.SupportActivity
import com.example.xevivuapp.features.menu.trip_history.TripHistoryActivity
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.taosif7.android.ringchartlib.RingChart
import com.taosif7.android.ringchartlib.models.RingChartData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class PointActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPointBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var passengersCollection: CollectionReference
    private var passengerID: String = ""

    private lateinit var sideSheetMenu: SideSheetBehavior<View>

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        passengersCollection = firestore.collection("Passengers")
        passengerID = intent.getStringExtra("Passenger_ID").toString()

        // Set up sideSheetMenu
        sideSheetMenu = SideSheetBehavior.from(findViewById(R.id.sideSheetMenu))
        sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
        sideSheetMenu.isDraggable = true

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        binding.menuButton.setOnClickListener {
            binding.dimOverlay.isVisible = true
            binding.sideSheetMenu.sideSheetMenuItem4.setTextColor(
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
                val intent = Intent(this@PointActivity, HomeActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem2.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@PointActivity, PersonalInfoActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem3.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@PointActivity, TripHistoryActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem5.setOnClickListener {
                Toast.makeText(
                    this@PointActivity,
                    getString(R.string.FeatureInDevelop),
                    Toast.LENGTH_LONG
                ).show()
            }
            sideSheetMenuItem6.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@PointActivity, SettingActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem7.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@PointActivity, SupportActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem8.setOnClickListener {
                logout(currentUser)
            }
        }

        setUpPoint()
    }

    private fun setUpPoint() {
        // Get data from Firestore
        passengersCollection.document(passengerID).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val point = document.getDouble("point") ?: 0
                binding.tvPoint.setDecimalFormat(DecimalFormat("###,###,###"))
                    .setAnimationDuration(2800).countAnimation(0, point.toInt())
            }
        }

        // Set up Ring Chart Point
        binding.pointRingChart.setLayoutMode(RingChart.renderMode.MODE_OVERLAP)
        val blue200 = RingChartData(0.2f, ContextCompat.getColor(this, R.color.blue_200), "")
        val gray = RingChartData(0.4f, ContextCompat.getColor(this, R.color.text_color), "")
        val red = RingChartData(0.6f, ContextCompat.getColor(this, R.color.refuse_background), "")
        val blue = RingChartData(0.8f, ContextCompat.getColor(this, R.color.button_background), "")
        val dataListPoint: ArrayList<RingChartData?> = object : ArrayList<RingChartData?>() {
            init {
                add(blue200)
                add(gray)
                add(red)
                add(blue)
            }
        }
        binding.pointRingChart.setData(dataListPoint)
        binding.pointRingChart.startAnimateLoading()

        // Stop animation after 3
        lifecycleScope.launch {
            delay(3000)
            binding.pointRingChart.stopAnimateLoading()
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
