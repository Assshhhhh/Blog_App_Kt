package com.example.blogappkt

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.blogappkt.databinding.ActivityMainBinding
import com.example.blogappkt.databinding.ActivityProfileBinding
import com.example.blogappkt.registeration.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userId = mAuth.currentUser?.uid

        if (userId != null){
            loadUserProfileData(userId)
        }

        // Add New Articles/Blogs
        binding.buttonProfileAddNewArticle.setOnClickListener {
            startActivity(Intent(this, AddArticlesActivity::class.java))
        }

        // See Your Articles/Blogs
        binding.buttonProfileYourArticle.setOnClickListener {
            startActivity(Intent(this, YourArticlesActivity::class.java))
        }

        // Logout Profile
        binding.buttonProfileLogout.setOnClickListener {
            showLogoutDialog()
        }

    }

    private fun loadUserProfileData(userId: String) {
        val userReference = databaseReference.child(userId)

        // Load User Profile Picture
        userReference.child("profileImage").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val profileImageUrl = snapshot.getValue(String::class.java)

                if (profileImageUrl != null){
                    Glide.with(this@ProfileActivity)
                        .load(profileImageUrl)
                        .into(binding.circProfileImage)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Error loading profile picture 🙃", Toast.LENGTH_SHORT).show()
            }
        })

        // Load User Profile Name
        userReference.child("name").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)
                if (userName != null){
                    binding.tvProfileName.text = userName
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Error loading Username 🙃", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLogoutDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Logout Confirmation")
            .setMessage("Are you sure you want to logout?")

        // Positive button with red title color
        val positiveText = SpannableString("Logout")
        positiveText.setSpan(ForegroundColorSpan(Color.RED), 0, positiveText.length, 0)
        builder.setPositiveButton(positiveText) { dialog, which ->
            mAuth.signOut()
            dialog.dismiss()
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()

        }

        // Negative button
        val negativeText = SpannableString("Cancel")
        builder.setNegativeButton(negativeText) { dialog, which ->
            // User clicked Cancel, do nothing or handle accordingly
            dialog.dismiss()
        }

        builder.show()
    }

    //

}