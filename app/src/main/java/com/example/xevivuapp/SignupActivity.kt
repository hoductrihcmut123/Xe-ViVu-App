package com.example.xevivuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.xevivuapp.databinding.ActivitySignupBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Passengers")

        binding.signupButton.setOnClickListener {

            val signupLastname = binding.signupLastname.text.toString()
            val signupFirstname = binding.signupFirstname.text.toString()
            val signupPhoneNumber = binding.signupPhoneNumber.text.toString()
            val signupPassword = binding.signupPasswordChild.text.toString()

            if(signupLastname.isNotEmpty() && signupFirstname.isNotEmpty() &&
                signupPhoneNumber.isNotEmpty() && signupPassword.isNotEmpty()){
                signupPassenger(signupLastname, signupFirstname, signupPhoneNumber, signupPassword)
            }
            else {
                Toast.makeText(this@SignupActivity,"Bạn vui lòng điền đầy đủ thông tin nhé!",
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupNavLogin.setOnClickListener{
            startActivity(Intent(this@SignupActivity,LoginActivity::class.java))
            finish()
        }
    }

    private fun signupPassenger(lastname: String, firstname: String, phoneNumber: String, password: String){
        databaseReference.orderByChild("mobile_No")
            .equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!dataSnapshot.exists()){
                    val userid = databaseReference.push().key
                    val passengerData = PassengerData(user_ID = userid, lastname = lastname,
                        firstname = firstname, mobile_No = phoneNumber, password = password)
                    databaseReference.child(userid!!).setValue(passengerData)
                    Toast.makeText(this@SignupActivity,"Đăng ký tài khoản thành công!",
                        Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignupActivity,LoginActivity::class.java))
                    finish()
                }
                else {
                    Toast.makeText(this@SignupActivity,"Rất tiếc, Tài khoản đã tồn tại!",
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SignupActivity,"Database Error: ${databaseError.message}",
                    Toast.LENGTH_SHORT).show()

            }
        })
    }

}