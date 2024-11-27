package com.example.xevivuapp.features.menu.personal_info

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
import com.example.xevivuapp.MainActivity
import com.example.xevivuapp.R
import com.example.xevivuapp.databinding.ActivityPersonalInfoBinding
import com.example.xevivuapp.features.booking.HomeActivity
import com.example.xevivuapp.features.menu.points.PointActivity
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

class PersonalInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalInfoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var passengersCollection: CollectionReference
    private var passengerID: String = ""

    private lateinit var sideSheetMenu: SideSheetBehavior<View>

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInfoBinding.inflate(layoutInflater)
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
            binding.sideSheetMenu.sideSheetMenuItem2.setTextColor(
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
                val intent = Intent(this@PersonalInfoActivity, HomeActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem3.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@PersonalInfoActivity, TripHistoryActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem4.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@PersonalInfoActivity, PointActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem5.setOnClickListener {
                Toast.makeText(
                    this@PersonalInfoActivity,
                    getString(R.string.FeatureInDevelop),
                    Toast.LENGTH_LONG
                ).show()
            }
            sideSheetMenuItem6.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@PersonalInfoActivity, SettingActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem7.setOnClickListener {
                sideSheetMenu.state = SideSheetBehavior.STATE_HIDDEN
                val intent = Intent(this@PersonalInfoActivity, SupportActivity::class.java)
                intent.putExtra("Passenger_ID", passengerID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            sideSheetMenuItem8.setOnClickListener {
                logout(currentUser)
            }
        }

        binding.editButton.setOnClickListener {
            val intent = Intent(this, UpdatePersonalInfoActivity::class.java)
            intent.putExtra("Passenger_ID", passengerID)
            startActivity(intent)
        }
    }

    override fun onResume() {
        fetchPassengerInfoData()
        super.onResume()
    }

    private fun fetchPassengerInfoData() {
        passengersCollection.document(passengerID).get().addOnSuccessListener { document ->
            if (document != null) {
                binding.tvNamePersonalInfo.text = buildString {
                    append(document.getString("lastname"))
                    append(" ")
                    append(document.getString("firstname"))
                }
                binding.tvPhoneValue.text = document.getString("mobile_No")
                binding.tvCompleteTripNumberValue.text =
                    document.getLong("bookingTripNum").toString()
                binding.tvNumberBadReportsValue.text =
                    document.getLong("reportPassengerNum").toString()

                if (document.getString("email")?.isNotEmpty() == true) {
                    binding.tvEmailValue.text = document.getString("email")
                    binding.llEmail.isVisible = true
                    binding.separateViewEmail.isVisible = true
                } else {
                    binding.llEmail.isVisible = false
                    binding.separateViewEmail.isVisible = false
                }

                if (document.getBoolean("gender") != null) {
                    binding.tvGenderValue.text = when (document.getBoolean("gender")) {
                        true -> getString(R.string.Male)
                        false -> getString(R.string.Female)
                        else -> ""
                    }
                    binding.llGender.isVisible = true
                    binding.separateViewGender.isVisible = true
                } else {
                    binding.llGender.isVisible = false
                    binding.separateViewGender.isVisible = false
                }
            }
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
