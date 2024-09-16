package com.example.xevivuapp.signup_login.signup

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.xevivuapp.R
import com.example.xevivuapp.data.PassengerData
import com.example.xevivuapp.databinding.ActivityVerifyPhonenumBinding
import com.example.xevivuapp.signup_login.permissions.PermissionActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

class VerifyPhoneNumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyPhonenumBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyPhonenumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Passengers")

        auth = FirebaseAuth.getInstance()
        lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

        val storedVerificationId = intent.getStringExtra("storedVerificationId")
        val signupLastname = intent.getStringExtra("signupLastname")
        val signupFirstname = intent.getStringExtra("signupFirstname")
        val signupPhoneNumber = intent.getStringExtra("signupPhoneNumber")
        val signupPassword = intent.getStringExtra("signupPassword")

        val otpGiven = binding.idOtp
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.guideContent.text = getString(R.string.VerificationMessage, signupPhoneNumber)

        val resendCode = binding.resendCode
        object : CountDownTimer(59000, 1000) {
            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                resendCode.setTextColor(
                    ContextCompat.getColor(
                        this@VerifyPhoneNumActivity,
                        R.color.button_background
                    )
                )
                resendCode.text = getString(R.string.ResendCodeMessage, millisUntilFinished / 1000)
                resendCode.underline()
            }

            // Callback function, fired when the time is up
            override fun onFinish() {
                resendCode.text = getString(R.string.ResendCode)
                var index = 1
                resendCode.setOnClickListener {
                    if (index > 0) {
                        index -= 1
                        Toast.makeText(
                            this@VerifyPhoneNumActivity, getString(R.string.OTPResent),
                            Toast.LENGTH_SHORT
                        ).show()

                        val number = "+84$signupPhoneNumber"
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(number) // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this@VerifyPhoneNumActivity) // Activity (for callback binding)
                            .setCallbacks(callbacks)
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    }
                    resendCode.setTextColor(
                        ContextCompat.getColor(
                            this@VerifyPhoneNumActivity,
                            R.color.sub_content
                        )
                    )
                }
            }
        }.start()

        binding.verifyButton.setOnClickListener {
            val otp = otpGiven.text.toString().trim()
            if (otp.isNotEmpty()) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp
                )
                if (signupLastname != null && signupFirstname != null && signupPhoneNumber != null
                    && signupPassword != null
                ) {
                    signInWithPhoneAuthCredential(
                        credential, signupLastname, signupFirstname,
                        signupPhoneNumber, signupPassword
                    )
                }
            } else {
                Toast.makeText(this, getString(R.string.PleaseEnterOTP), Toast.LENGTH_SHORT).show()
            }
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
            override fun onVerificationFailed(e: FirebaseException) {}
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
            }
        }

    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        signupLastname: String, signupFirstname: String,
        signupPhoneNumber: String, signupPassword: String
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    databaseReference.orderByChild("mobile_No")
                        .equalTo(signupPhoneNumber)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val userid = databaseReference.push().key
                                val passengerData = PassengerData(
                                    user_ID = userid,
                                    lastname = signupLastname, firstname = signupFirstname,
                                    mobile_No = signupPhoneNumber, password = signupPassword
                                )
                                databaseReference.child(userid!!).setValue(passengerData)
                                Toast.makeText(
                                    this@VerifyPhoneNumActivity,
                                    getString(R.string.VerificationRegistrationSuccessful),
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(this@VerifyPhoneNumActivity, PermissionActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(
                                    this@VerifyPhoneNumActivity,
                                    "Database Error: ${databaseError.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })

                    val intent = Intent(this@VerifyPhoneNumActivity, PermissionActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, getString(R.string.InvalidOTP), Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
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
}