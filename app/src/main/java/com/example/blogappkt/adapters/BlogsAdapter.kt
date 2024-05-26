package com.example.blogappkt.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.bumptech.glide.Glide
import com.example.blogappkt.R
import com.example.blogappkt.ReadMoreActivity
import com.example.blogappkt.databinding.ItemBlogsBinding
import com.example.blogappkt.models.BlogsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BlogsAdapter(private val context:Context, private val arrayList: ArrayList<BlogsModel>): Adapter<BlogsAdapter.ViewHolder>()  {

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBlogsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blogItems = arrayList[position]
        holder.bind(blogItems)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(private val binding: ItemBlogsBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(blogsModel: BlogsModel){

            binding.apply {

                val postId = blogsModel.postId.toString()

                tvHeading.text = blogsModel.heading
                Glide.with(profileCircImage.context)
                    .load(blogsModel.profileImage)
                    .into(profileCircImage)
                tvUsername.text = blogsModel.userName
                tvDate.text = blogsModel.date
                tvPost.text = blogsModel.post
                tvLikesCount.text = blogsModel.likeCount.toString()

                // On Item Click listener
                readMoreButton.setOnClickListener {

                    val context = root.context
                    val intent = Intent(context, ReadMoreActivity::class.java)
                    intent.putExtra("blogItem", blogsModel)
                    context.startActivity(intent)

                }

                // Check if already liked
                val postLikeReference = databaseReference.child("Blogs").child(postId).child("Likes")
                val hasCurrentUserLiked = currentUser?.uid?.let { uid ->
                    postLikeReference.child(uid).addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()){
                                binding.likeImageButton.setImageResource(R.drawable.ic_like_red_filled)
                            }
                            else{
                                binding.likeImageButton.setImageResource(R.drawable.ic_like_black_outline)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }

                // Handle Like Clicks
                binding.likeImageButton.setOnClickListener {

                    if (currentUser != null){
                        handleLikeClicks(postId, blogsModel, binding)
                    }
                    else{
                        Toast.makeText(context, "You have to login first", Toast.LENGTH_SHORT).show()
                    }
                }

                // Set the icons on start
                val userReference = databaseReference.child("Users").child(currentUser!!.uid)
                val postSaveReference = userReference.child("SavedPosts").child(postId)
                postSaveReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            binding.savePostImageButton.setImageResource(R.drawable.ic_save_red_filled)
                        }
                        else{
                            binding.savePostImageButton.setImageResource(R.drawable.ic_save_red_outline)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                // Handle Save Clicks
                binding.savePostImageButton.setOnClickListener {

                    if (currentUser != null){
                        handleSaveClicks(postId, blogsModel, binding)
                    }
                    else{
                        Toast.makeText(context, "You have to login first", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }
    }

    private fun handleLikeClicks(postId: String, blogsModel: BlogsModel, binding: ItemBlogsBinding) {

        val userReference = databaseReference.child("Users").child(currentUser!!.uid)
        val postLikeReference = databaseReference.child("Blogs").child(postId).child("Likes")

        // Handle Like/Unlike
        postLikeReference.child(currentUser.uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    userReference.child("Likes").child(postId).removeValue()
                        .addOnSuccessListener {
                            postLikeReference.child(currentUser.uid).removeValue()
                            blogsModel.likedBy?.remove(currentUser.uid)

                            updateLikeImage(binding, false)

                            // Decrement Likes
                            val newLikeCount = blogsModel.likeCount!! -1
                            blogsModel.likeCount = newLikeCount
                            databaseReference.child("Blogs").child(postId).child("likeCount").setValue(newLikeCount)
                            notifyDataSetChanged()
                        }.addOnFailureListener {e ->
                            Log.e("LikedCLick", "onDataChange: Failed to unlike the blog $e", )
                        }
                }
                else{
                    // User hasn't liked yet, Like!
                    userReference.child("Likes").child(postId).setValue(true)
                        .addOnSuccessListener {
                            postLikeReference.child(currentUser.uid).setValue(true)
                            blogsModel.likedBy?.add(currentUser.uid)
                            updateLikeImage(binding,true)

                            // Increment Likes
                            val newLikeCount = blogsModel.likeCount!! +1
                            blogsModel.likeCount = newLikeCount
                            databaseReference.child("Blogs").child(postId).child("likeCount").setValue(newLikeCount)
                            notifyDataSetChanged()
                        }.addOnFailureListener {e ->
                            Log.e("LikedCLick", "onDataChange: Failed to like the blog $e", )
                        }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun updateLikeImage(binding: ItemBlogsBinding, flag: Boolean){

        if (flag){
            binding.likeImageButton.setImageResource(R.drawable.ic_like_black_outline)
        }
        else{
            binding.likeImageButton.setImageResource(R.drawable.ic_like_red_filled)
        }

    }

    private fun handleSaveClicks(postId: String, blogsModel: BlogsModel, binding: ItemBlogsBinding) {

        val userReference = databaseReference.child("Users").child(currentUser!!.uid)
        userReference.child("SavedPosts").child(postId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    // if Blog is saved already, Unsave!
                    userReference.child("SavedPosts").child(postId).removeValue()
                        .addOnSuccessListener {
                            val clickedBlogItem = arrayList.find { it.postId == postId }
                            clickedBlogItem?.isSaved = false
                            notifyDataSetChanged()
                            Toast.makeText(context, "Blog Unsaved!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(context, "Failed unsaving Blog!", Toast.LENGTH_SHORT).show()
                        }
                    binding.savePostImageButton.setImageResource(R.drawable.ic_save_red_outline)
                }
                else{
                    // if Blog is not saved, Save!
                    userReference.child("SavedPosts").child(postId).setValue(true)
                        .addOnSuccessListener {
                            val clickedBlogItem = arrayList.find { it.postId == postId }
                            clickedBlogItem?.isSaved = true
                            notifyDataSetChanged()
                            Toast.makeText(context, "Blog Saved!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(context, "Failed Saving Blog!", Toast.LENGTH_SHORT).show()
                        }
                    binding.savePostImageButton.setImageResource(R.drawable.ic_save_red_filled)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    fun updateData(savedBlogsArticles: List<BlogsModel>) {
        arrayList.clear()
        arrayList.addAll(savedBlogsArticles)
        notifyDataSetChanged()
    }

}