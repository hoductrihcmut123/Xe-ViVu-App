package com.example.xevivuapp.signup_login.forgot_password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.xevivuapp.signup_login.signup.SignupActivity
import com.example.xevivuapp.databinding.ActivityNewPasswordBinding
import com.example.xevivuapp.signup_login.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Passengers")

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val fpPhoneNumber = intent.getStringExtra("fpPhoneNumber")

        binding.newPasswordButton.setOnClickListener {
            val newPassword = binding.newPasswordChild.text.toString()
            val verifyPassword = binding.verifyPasswordChild.text.toString()

            if (newPassword.isNotEmpty() && verifyPassword.isNotEmpty() && newPassword == verifyPassword) {
                if (currentUser != null) {
                    auth.signOut()
                }
                if (fpPhoneNumber != null) {
                    changePassword(fpPhoneNumber, newPassword)
                }
            } else if (newPassword.isNotEmpty() && verifyPassword.isNotEmpty() && newPassword != verifyPassword) {
                Toast.makeText(
                    this@NewPasswordActivity, "Mật khẩu nhập lại không khớp!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this@NewPasswordActivity, "Bạn vui lòng điền đầy đủ thông tin nhé!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.loginNavSignup.setOnClickListener {
            if (currentUser != null) {
                auth.signOut()
            }
            startActivity(Intent(this@NewPasswordActivity, SignupActivity::class.java))
            finish()
        }
    }

    private fun changePassword(phoneNumber: String, password: String) {
        databaseReference.orderByChild("mobile_No")
            .equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val passengerData = mapOf("password" to password)
                        databaseReference.child("${userSnapshot.key}").updateChildren(passengerData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@NewPasswordActivity,
                                    "Đổi mật khẩu thành công!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this@NewPasswordActivity,
                                    "Rất tiếc đã có lỗi xảy ra, bạn vui lòng thử lại nhé!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        startActivity(
                            Intent(
                                this@NewPasswordActivity,
                                LoginActivity::class.java
                            )
                        )
                        finish()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@NewPasswordActivity, "Database Error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}