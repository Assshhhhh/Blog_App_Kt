package com.example.blogappkt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogappkt.adapters.BlogsAdapter
import com.example.blogappkt.databinding.ActivitySavedArticlesBinding
import com.example.blogappkt.models.BlogsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class SavedArticlesActivity : AppCompatActivity() {

    private val binding: ActivitySavedArticlesBinding by lazy {
        ActivitySavedArticlesBinding.inflate(layoutInflater)
    }

    private val savedBlogsArticles = mutableListOf<BlogsModel>()
    private lateinit var blogsAdapter: BlogsAdapter
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Blogs Adapter
        blogsAdapter = BlogsAdapter(this,
            savedBlogsArticles.filter { it.isSaved }.toMutableList() as ArrayList<BlogsModel>
        )

        binding.recyclerSavedBlogs.adapter = blogsAdapter
        binding.recyclerSavedBlogs.layoutManager = LinearLayoutManager(this)

        val userId = mAuth.currentUser?.uid
        if (userId != null){

            val userReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("SavedPosts")
            userReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        for (postSnapshot in snapshot.children) {
                            val postId = postSnapshot.key
                            val isSaved = postSnapshot.value as Boolean
                            if (postId != null && isSaved) {

                                // Fetch the corresponding blogs on postId using Coroutine
                                CoroutineScope(Dispatchers.IO).launch {
                                    val blogItem = fetchBlogItem(postId)
                                    if (blogItem != null) {
                                        savedBlogsArticles.add(blogItem)
                                        launch(Dispatchers.Main) {
                                            blogsAdapter.updateData(savedBlogsArticles)
                                        }
                                    }
                                }
                            }

                        }
                    }
                    else{
                        Toast.makeText(this@SavedArticlesActivity, "No Bookmarks", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }

        binding.backImageButton.setOnClickListener {
            finish()
        }

        binding.recyclerSavedBlogs.setOnClickListener {

        }

    }

    private suspend fun fetchBlogItem(postId: String): BlogsModel? {

        val blogReference = FirebaseDatabase.getInstance().getReference("Blogs")
        return try {
            val dataSnapshot = blogReference.child(postId).get().await()
            val blogData = dataSnapshot.getValue(BlogsModel::class.java)
            blogData
        }catch (e: Exception){
            null
        }

    }
}