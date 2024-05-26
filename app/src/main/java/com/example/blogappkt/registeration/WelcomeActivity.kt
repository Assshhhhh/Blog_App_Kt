package com.example.blogappkt.registeration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.blogappkt.MainActivity
import com.example.blogappkt.R
import com.example.blogappkt.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    private val binding: ActivityWelcomeBinding by lazy {
        ActivityWelcomeBinding.inflate(layoutInflater)
    }

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val intent: Intent = Intent(this, SigninAndRegisterationActivity::class.java)
            intent.putExtra("action", "login")
            startActivity(intent)
            finish()
        }

        binding.registerButton.setOnClickListener {
            val intent: Intent = Intent(this, SigninAndRegisterationActivity::class.java)
            intent.putExtra("action", "register")
            startActivity(intent)
            finish()
        }

    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser

        if (currentUser != null){

            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }

    }
}