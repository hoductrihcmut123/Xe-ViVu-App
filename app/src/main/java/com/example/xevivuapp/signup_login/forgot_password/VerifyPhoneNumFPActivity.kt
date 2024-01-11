package com.example.xevivuapp.signup_login.forgot_password

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.xevivuapp.R
import com.example.xevivuapp.databinding.ActivityVerifyPhonenumBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class VerifyPhoneNumFPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyPhonenumBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyPhonenumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

        val storedVerificationId = intent.getStringExtra("storedVerificationId")
        val fpPhoneNumber = intent.getStringExtra("fpPhoneNumber")

        val otpGiven = binding.idOtp
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.guideContent.text =
            "Một mã xác minh đã được gửi đến $fpPhoneNumber qua tin nhắn SMS"

        val resendCode = binding.resendCode
        object : CountDownTimer(59000, 1000) {
            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                resendCode.setTextColor(
                    ContextCompat.getColor(
                        this@VerifyPhoneNumFPActivity,
                        R.color.button_background
                    )
                )
                resendCode.text = "Vui lòng nhập mã (0:" + millisUntilFinished / 1000 + ")"
                resendCode.underline()
            }
            // Callback function, fired when the time is up
            override fun onFinish() {
                resendCode.text = "Gửi lại mã"
                var index = 1
                resendCode.setOnClickListener {
                    if (index > 0) {
                        index -= 1
                        Toast.makeText(
                            this@VerifyPhoneNumFPActivity, "Đã gửi lại OTP",
                            Toast.LENGTH_SHORT
                        ).show()

                        val number = "+84$fpPhoneNumber"
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(number) // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this@VerifyPhoneNumFPActivity) // Activity (for callback binding)
                            .setCallbacks(callbacks)
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    }
                    resendCode.setTextColor(
                        ContextCompat.getColor(
                            this@VerifyPhoneNumFPActivity,
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
                if (fpPhoneNumber != null) {
                    signInWithPhoneAuthCredential(credential, fpPhoneNumber)
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập OTP", Toast.LENGTH_SHORT).show()
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

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, fpPhoneNumber: String) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Xác minh thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@VerifyPhoneNumFPActivity, NewPasswordActivity::class.java)
                    intent.putExtra("fpPhoneNumber", fpPhoneNumber)
                    startActivity(intent)
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "OTP không hợp lệ!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

}