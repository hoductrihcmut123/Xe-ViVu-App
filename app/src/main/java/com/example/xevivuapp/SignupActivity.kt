package com.example.xevivuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.xevivuapp.databinding.ActivitySignupBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

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

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Passengers")
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
                    this@SignupActivity, "Bạn vui lòng điền đầy đủ thông tin nhé!",
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
                Toast.makeText(this@SignupActivity, "Có lỗi xảy ra!", Toast.LENGTH_SHORT)
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
        databaseReference.orderByChild("mobile_No")
            .equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
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
                            this@SignupActivity, "Rất tiếc, Tài khoản đã tồn tại!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@SignupActivity, "Database Error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}