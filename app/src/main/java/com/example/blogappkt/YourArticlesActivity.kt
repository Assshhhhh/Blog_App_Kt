package com.example.blogappkt

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogappkt.adapters.YourBlogsAdapter
import com.example.blogappkt.databinding.ActivityYourArticlesBinding
import com.example.blogappkt.models.BlogsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class YourArticlesActivity : AppCompatActivity() {

    private val binding: ActivityYourArticlesBinding by lazy {
        ActivityYourArticlesBinding.inflate(layoutInflater)
    }

    private lateinit var yourBlogsList: ArrayList<BlogsModel>
    private lateinit var yourBlogsAdapter: YourBlogsAdapter

    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    private val EDIT_BLOG_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        yourBlogsList = ArrayList<BlogsModel>()

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid

        // Back
        binding.backImageButton.setOnClickListener {
            finish()
        }

        // Recycler
        binding.recyclerYourBlogs.layoutManager = LinearLayoutManager(this)
        if (currentUser != null) {
            yourBlogsAdapter =
                YourBlogsAdapter(this, emptyList(), object : YourBlogsAdapter.OnItemClickListener {
                    override fun onEditClick(blogItem: BlogsModel) {
                        val intent = Intent(applicationContext, EditBlogActivity::class.java)
                        intent.putExtra("blogItem", blogItem)
//                        intent.putExtra("heading", blogItem.heading)
//                        intent.putExtra("post", blogItem.post)
//                        intent.putExtra("postId", blogItem.postId)
                        startActivityForResult(intent, EDIT_BLOG_REQUEST_CODE)
                    }

                    override fun onReadMoreClick(blogItem: BlogsModel) {
                        val intent = Intent(applicationContext, ReadMoreActivity::class.java)
                        intent.putExtra("blogItem", blogItem)
                        startActivity(intent)
                    }

                    override fun onDeleteClick(blogItem: BlogsModel) {
                        deleteYourBlog(blogItem)
                    }
                })
        }
        binding.recyclerYourBlogs.adapter = yourBlogsAdapter

        // Fetch Blogs
        databaseReference = FirebaseDatabase.getInstance().getReference("Blogs")
        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children){
                    val yourBlogs = postSnapshot.getValue(BlogsModel::class.java)
                    if (yourBlogs != null && currentUser == yourBlogs.userId){
                        yourBlogsList.add(yourBlogs)
                    }
                }
                yourBlogsList.reverse()
                yourBlogsAdapter.setData(yourBlogsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@YourArticlesActivity, "Error loading your blogs 🙃", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun deleteYourBlog(blogItem: BlogsModel) {

        showDeleteDialog(blogItem)

    }

    private fun showDeleteDialog(blogItem: BlogsModel) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Deletion Confirmation")
            .setMessage("Are you sure you want to delete this blog?")

        // Positive button with red title color
        val positiveText = SpannableString("Delete")
        positiveText.setSpan(ForegroundColorSpan(Color.RED), 0, positiveText.length, 0)
        builder.setPositiveButton(positiveText) { dialog, which ->

            val postId = blogItem.postId.toString()
            val blogPostReference = databaseReference.child(postId)

            blogPostReference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this@YourArticlesActivity, "Blog Deleted", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this@YourArticlesActivity, "Couldn't delete the blog, Try again", Toast.LENGTH_SHORT).show()
                }

        }

        // Negative button
        val negativeText = SpannableString("Cancel")
        builder.setNegativeButton(negativeText) { dialog, which ->
            // User clicked Cancel, do nothing or handle accordingly
            dialog.dismiss()
        }

        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_BLOG_REQUEST_CODE && resultCode == Activity.RESULT_OK){

        }
    }
}