package com.example.xevivuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.animation.ExperimentalAnimationApi
import com.example.xevivuapp.databinding.ActivityHomeBinding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    @OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        binding.idLogout.setOnClickListener{
            if(currentUser != null) {
                auth.signOut()
            }
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }

    override fun onStop() {
        super.onStop()
        auth.signOut()
    }
}