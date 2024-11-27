package com.example.xevivuapp.features.menu.support

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.xevivuapp.R
import com.example.xevivuapp.data.PassengerSupport
import com.example.xevivuapp.databinding.ActivityRequestSupportBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class RequestSupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestSupportBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var passengerSupportCollection: CollectionReference
    private var passengerID: String = ""

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        passengerSupportCollection = firestore.collection("PassengerSupports")
        passengerID = intent.getStringExtra("Passenger_ID").toString()

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.sendFeedbackButton.setOnClickListener {
            val feedbackContent = binding.etFeedback.text.toString()
            if (feedbackContent.isNotEmpty()) {
                val id = passengerSupportCollection.document().id
                val passengerSupport = PassengerSupport(
                    passengerSupport_ID = id,
                    passenger_ID = passengerID,
                    supportContent = feedbackContent,
                    createTime = Date().toString()
                )
                passengerSupportCollection.document(id).set(passengerSupport)
                    .addOnSuccessListener {
                        binding.etFeedback.setText("")
                        Toast.makeText(this, getString(R.string.RequestSend), Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            getString(R.string.SomethingWentWrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(this, getString(R.string.PleaseWriteContent), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
