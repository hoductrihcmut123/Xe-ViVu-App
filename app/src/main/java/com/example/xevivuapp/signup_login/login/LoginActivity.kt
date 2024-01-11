package com.example.xevivuapp.signup_login.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.xevivuapp.signup_login.permissions.PermissionActivity
import com.example.xevivuapp.signup_login.signup.SignupActivity
import com.example.xevivuapp.data.PassengerData
import com.example.xevivuapp.databinding.ActivityLoginBinding
import com.example.xevivuapp.signup_login.forgot_password.ForgotPasswordActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Passengers")

        binding.loginForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            val loginPhoneNumber = binding.loginPhoneNumber.text.toString()
            val loginPassword = binding.loginPasswordChild.text.toString()

            if (loginPhoneNumber.isNotEmpty() && loginPassword.isNotEmpty()) {
                loginPassenger(loginPhoneNumber, loginPassword)
            } else {
                Toast.makeText(
                    this@LoginActivity, "Bạn vui lòng điền đầy đủ thông tin nhé!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.loginNavSignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            finish()
        }
    }

    private fun loginPassenger(phoneNumber: String, password: String) {
        databaseReference.orderByChild("mobile_No")
            .equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userData = userSnapshot.getValue(PassengerData::class.java)
                            if (userData != null && userData.password == password) {
                                Toast.makeText(
                                    this@LoginActivity, "Chúc mừng bạn đã đăng nhập thành công!",
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this@LoginActivity, PermissionActivity::class.java))
                                finish()
                                return
                            }
                        }
                    }
                    Toast.makeText(
                        this@LoginActivity, "Sai thông tin, bạn kiểm tra lại nhé!",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@LoginActivity, "Database Error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}