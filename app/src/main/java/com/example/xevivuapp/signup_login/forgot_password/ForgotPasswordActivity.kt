package com.example.xevivuapp.signup_login.forgot_password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.xevivuapp.signup_login.signup.SignupActivity
import com.example.xevivuapp.databinding.ActivityForgotPasswordBinding
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

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var fpPhoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Passengers")
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
                    this@ForgotPasswordActivity, "Bạn vui lòng điền Số điện thoại nhé!",
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
                Toast.makeText(this@ForgotPasswordActivity, "Có lỗi xảy ra!", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

                val intent = Intent(this@ForgotPasswordActivity, VerifyPhoneNumFPActivity::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId)
                intent.putExtra("fpPhoneNumber", fpPhoneNumber)
                startActivity(intent)
            }
        }
    }

    private fun fpPassenger(phoneNumber: String) {
        databaseReference.orderByChild("mobile_No")
            .equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
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
                            this@ForgotPasswordActivity, "Rất tiếc, Tài khoản không tồn tại!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@ForgotPasswordActivity, "Database Error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}