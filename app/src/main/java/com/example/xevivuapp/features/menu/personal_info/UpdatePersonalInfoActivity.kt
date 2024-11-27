package com.example.xevivuapp.features.menu.personal_info

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.xevivuapp.R
import com.example.xevivuapp.databinding.ActivityUpdatePersonalInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class UpdatePersonalInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePersonalInfoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var passengersCollection: CollectionReference
    private var passengerID: String = ""
    private var gender: Boolean? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        passengersCollection = firestore.collection("Passengers")
        passengerID = intent.getStringExtra("Passenger_ID").toString()

        auth = FirebaseAuth.getInstance()

        binding.backButton.setOnClickListener {
            super.onBackPressed()
        }

        binding.ivEditAvatar.setOnClickListener {
            Toast.makeText(this, getString(R.string.FeatureInDevelop), Toast.LENGTH_SHORT).show()
        }

        binding.radioGroupGender.setOnCheckedChangeListener { _, checkedId ->
            gender = when (checkedId) {
                R.id.radioMale -> true  // true cho Male
                R.id.radioFemale -> false // false cho Female
                else -> null
            }
        }

        binding.updateButton.setOnClickListener {
            updateData()
        }

        fetchAndSetUpData()
    }

    private fun fetchAndSetUpData() {
        passengersCollection.document(passengerID).get().addOnSuccessListener { document ->
            if (document != null) {
                binding.lastnameValue.setText(document.getString("lastname"))
                binding.firstnameValue.setText(document.getString("firstname"))
                binding.emailValue.setText(document.getString("email"))

                when (document.getBoolean("gender")) {
                    true -> binding.radioMale.isChecked = true
                    false -> binding.radioFemale.isChecked = true
                    else -> {}
                }
            }
        }
    }

    private fun updateData() {
        val lastname = binding.lastnameValue.text.toString()
        val firstname = binding.firstnameValue.text.toString()
        val email = binding.emailValue.text.toString()


        if (lastname.isEmpty() || firstname.isEmpty()) {
            Toast.makeText(this, getString(R.string.PleaseFillAllStarFields), Toast.LENGTH_SHORT)
                .show()
            return
        }

        passengersCollection.document(passengerID).update(
            mapOf(
                "lastname" to lastname,
                "firstname" to firstname,
                "email" to email,
                "gender" to gender
            )
        ).addOnSuccessListener {
            Toast.makeText(this, getString(R.string.UpdateSuccessfully), Toast.LENGTH_SHORT).show()
            super.onBackPressed()
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.SomethingWentWrong), Toast.LENGTH_LONG).show()
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
}
