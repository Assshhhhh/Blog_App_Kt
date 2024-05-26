package com.example.blogappkt

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogappkt.adapters.BlogsAdapter
import com.example.blogappkt.databinding.ActivityMainBinding
import com.example.blogappkt.models.BlogsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    private val blogItems = ArrayList<BlogsModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Blogs")

        val userId = mAuth.currentUser?.uid
        // Set User Profile Picture
        if (userId != null){
            loadUserProfilePicture(userId)
        }

        // Set Recycler
        val blogAdapter = BlogsAdapter(this, blogItems)
        binding.blogsRecycler.adapter = blogAdapter
        binding.blogsRecycler.layoutManager = LinearLayoutManager(this)

        // Fetch Data
        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                blogItems.clear()
                for (dataSnapshot in snapshot.children){

                    val blogItem = dataSnapshot.getValue(BlogsModel::class.java)
                    if (blogItem != null){
                        blogItems.add(blogItem)
                    }

                }
                blogItems.reverse()
                blogAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error loading Blog data", Toast.LENGTH_SHORT).show()
            }
        })

        binding.apply {

            //Floating Action Button
            floatingAddArticleButton.imageTintList = ColorStateList.valueOf(Color.WHITE)
            floatingAddArticleButton.setOnClickListener {
                startActivity(Intent(root.context, AddArticlesActivity::class.java))
            }

            // Saved Blogs
            saveArticleButton.setOnClickListener {
                startActivity(Intent(root.context, SavedArticlesActivity::class.java))
            }

            // Profile
            profileImage.setOnClickListener {
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            }

        }

    }

    private fun loadUserProfilePicture(userId: String) {

        val userReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        userReference.child("profileImage").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val profileImageUrl = snapshot.getValue(String::class.java)

                if (profileImageUrl != null){
                    Glide.with(this@MainActivity)
                        .load(profileImageUrl)
                        .into(binding.profileImage)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error loading profile picture", Toast.LENGTH_SHORT).show()
            }
        })

    }
}