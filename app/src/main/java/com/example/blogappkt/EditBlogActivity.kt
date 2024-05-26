package com.example.blogappkt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.blogappkt.databinding.ActivityAddArticlesBinding
import com.example.blogappkt.databinding.ActivityEditBlogBinding
import com.example.blogappkt.models.BlogsModel
import com.google.firebase.database.FirebaseDatabase

class EditBlogActivity : AppCompatActivity() {

    private val binding: ActivityEditBlogBinding by lazy {
        ActivityEditBlogBinding.inflate(layoutInflater)
    }

    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val blogItemModel = intent.getParcelableExtra<BlogsModel>("blogItem")
        //val blogItemModel = BlogsModel()

        //postId = intent.getStringExtra("postId")!!
//        val title = intent.getStringExtra("heading")
//        val description = intent.getStringExtra("post")

        binding.apply {

            // Back Button
            backImageButton.setOnClickListener {
                finish()
            }

            editBlogTitle.editText?.setText(blogItemModel?.heading)
            editBlogDescription.editText?.setText(blogItemModel?.post)

//            editBlogTitle.editText?.setText(title)
//            editBlogDescription.editText?.setText(description)

            // Save Changes
            saveBlogButton.setOnClickListener {

                val updatedTitle = editBlogTitle.editText?.text.toString().trim()
                val updatedDescription = editBlogDescription.editText?.text.toString().trim()
                if (updatedTitle.isEmpty() || updatedDescription.isEmpty()){
                    Toast.makeText(root.context, "Please fill both the fields", Toast.LENGTH_SHORT).show()
                }
                else{

                    blogItemModel?.heading = updatedTitle
                    blogItemModel?.post = updatedDescription
                    if (blogItemModel != null){
                        saveAndUpdateChanges(blogItemModel)
                    }

//                    blogItemModel?.heading = updatedTitle
//                    blogItemModel?.post = updatedDescription
//                    if (blogItemModel != null){
//                        saveAndUpdateChanges(blogItemModel)
//                    }

                }


            }

        }

    }

    private fun saveAndUpdateChanges(blogItemModel: BlogsModel) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("Blogs")
        val postId = blogItemModel.postId.toString()
        databaseReference.child(postId).setValue(blogItemModel)
            .addOnSuccessListener {
                Toast.makeText(this, "Blog Updated", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show()
            }

    }
}