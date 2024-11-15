package com.example.xevivuapp.signup_login.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.xevivuapp.features.booking.HomeActivity
import com.example.xevivuapp.R
import com.example.xevivuapp.databinding.ActivitySignupBinding
import com.example.xevivuapp.signup_login.login.LoginActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var passengersCollection: CollectionReference

    private lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    private lateinit var signupLastname: String
    private lateinit var signupFirstname: String
    private lateinit var signupPhoneNumber: String
    private lateinit var signupPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        passengersCollection = firestore.collection("Passengers")
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this@SignupActivity, HomeActivity::class.java))
            finish()
        }

        binding.signupButton.setOnClickListener {

            signupLastname = binding.signupLastname.text.toString()
            signupFirstname = binding.signupFirstname.text.toString()
            signupPhoneNumber = binding.signupPhoneNumber.text.toString()
            signupPassword = binding.signupPasswordChild.text.toString()

            if (signupLastname.isNotEmpty() && signupFirstname.isNotEmpty() &&
                signupPhoneNumber.isNotEmpty() && signupPassword.isNotEmpty()
            ) {
                signupPassenger(signupPhoneNumber)
            } else {
                Toast.makeText(
                    this@SignupActivity, getString(R.string.PleaseFillAllInformation),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.signupNavLogin.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(this@SignupActivity, HomeActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(
                    this@SignupActivity,
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

                val intent = Intent(this@SignupActivity, VerifyPhoneNumActivity::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId)
                intent.putExtra("signupLastname", signupLastname)
                intent.putExtra("signupFirstname", signupFirstname)
                intent.putExtra("signupPhoneNumber", signupPhoneNumber)
                intent.putExtra("signupPassword", signupPassword)
                startActivity(intent)
            }
        }
    }

    private fun signupPassenger(phoneNumber: String) {
        passengersCollection.whereEqualTo("mobile_No", phoneNumber)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    val number = "+84$phoneNumber"
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this@SignupActivity) // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                } else {
                    Toast.makeText(
                        this@SignupActivity, getString(R.string.AccountExists),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@SignupActivity, "Database Error: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
