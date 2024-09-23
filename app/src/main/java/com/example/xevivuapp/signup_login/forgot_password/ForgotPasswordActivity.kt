package com.example.xevivuapp.signup_login.forgot_password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.xevivuapp.R
import com.example.xevivuapp.signup_login.signup.SignupActivity
import com.example.xevivuapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var passengersCollection: CollectionReference

    private lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var fpPhoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        passengersCollection = firestore.collection("Passengers")
        auth = FirebaseAuth.getInstance()

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.fpButton.setOnClickListener {

            fpPhoneNumber = binding.fpPhoneNumber.text.toString()

            if (fpPhoneNumber.isNotEmpty()) {
                fpPassenger(fpPhoneNumber)
            } else {
                Toast.makeText(
                    this@ForgotPasswordActivity, getString(R.string.PleaseEnterYourPhoneNumber),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.fpNavSignup.setOnClickListener {
            startActivity(Intent(this@ForgotPasswordActivity, SignupActivity::class.java))
            finish()
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(this@ForgotPasswordActivity, NewPasswordActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    getString(R.string.AnErrorOccurred),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

                val intent =
                    Intent(this@ForgotPasswordActivity, VerifyPhoneNumFPActivity::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId)
                intent.putExtra("fpPhoneNumber", fpPhoneNumber)
                startActivity(intent)
            }
        }
    }

    private fun fpPassenger(phoneNumber: String) {
        passengersCollection.whereEqualTo("mobile_No", phoneNumber)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val number = "+84$phoneNumber"
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this@ForgotPasswordActivity) // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                } else {
                    Toast.makeText(
                        this@ForgotPasswordActivity, getString(R.string.AccountDoesNotExist),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@ForgotPasswordActivity, "Database Error: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
