package com.example.blogappkt

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.widget.Toast
import com.example.blogappkt.databinding.ActivityAddArticlesBinding
import com.example.blogappkt.models.BlogsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import java.text.SimpleDateFormat
import java.util.Date

class AddArticlesActivity : AppCompatActivity() {

    private val binding: ActivityAddArticlesBinding by lazy {
        ActivityAddArticlesBinding.inflate(layoutInflater)
    }

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Blogs")
    private val userReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {

            // Back Button
            backImageButton.setOnClickListener {
                finish()
            }

            // Add Button
            addBlogButton.setOnClickListener {

                val title: String = blogTitle.editText?.text.toString().trim()
                val description: String = blogDescription.editText?.text.toString().trim()

                if (title.isEmpty() || description.isEmpty()){
                    Toast.makeText(root.context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }

                // Get Current User
                val user: FirebaseUser? = mAuth.currentUser
                if (user != null){
                    val userId: String = user.uid
                    val userName: String = user.displayName?: "Anonymous"
                    val userImageUrl = user.photoUrl?: ""

                    // Fetch user_name and user_profile from database
                    userReference.child(userId).addListenerForSingleValueEvent(object: ValueEventListener{
                        @SuppressLint("NewApi")
                        override fun onDataChange(snapshot: DataSnapshot) {

                            val userData = snapshot.getValue(com.example.blogappkt.models.UserData::class.java)
                            if (userData != null){
                                val userNameFromDB = userData.name
                                val userImageUrlFromDB = userData.profileImage

                                val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                                // Primary Key for each new blog
                                val key = databaseReference.push().key

                                // Create a Blog item model
                                val blogItem = BlogsModel(
                                    title,
                                    userNameFromDB,
                                    userId,
                                    currentDate,
                                    description,
                                    0,
                                    userImageUrlFromDB,

                                )

                                if (key != null){

                                    blogItem.postId = key
                                    val blogReference = databaseReference.child(key)
                                    blogReference.setValue(blogItem).addOnCompleteListener {
                                        if (it.isSuccessful){
                                            Toast.makeText(root.context, "Added", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(root.context, MainActivity::class.java))
                                            finish()
                                        }
                                        else{
                                            Toast.makeText(root.context, "Failed to add!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                }

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }

            }

        }

    }
}