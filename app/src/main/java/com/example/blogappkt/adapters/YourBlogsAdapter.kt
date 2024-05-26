package com.example.blogappkt.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogappkt.databinding.ItemYourBlogsBinding
import com.example.blogappkt.models.BlogsModel
import java.util.ArrayList

class YourBlogsAdapter(
    private val context: Context,
    private var blogList: List<BlogsModel>,
    private var itemClickListener: OnItemClickListener
): RecyclerView.Adapter<YourBlogsAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun onEditClick(blogItem: BlogsModel)
        fun onReadMoreClick(blogItem: BlogsModel)
        fun onDeleteClick(blogItem: BlogsModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourBlogsAdapter.ViewHolder {
        return ViewHolder(ItemYourBlogsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: YourBlogsAdapter.ViewHolder, position: Int) {
        val blogItem = blogList[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun setData(yourBlogsList: ArrayList<BlogsModel>) {
        this.blogList = yourBlogsList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemYourBlogsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItem: BlogsModel) {

            binding.apply {

                tvHeading.text = blogItem.heading
                Glide.with(profileCircImage.context)
                    .load(blogItem.profileImage)
                    .into(profileCircImage)
                tvUsername.text = blogItem.userName
                tvDate.text = blogItem.date
                tvPost.text = blogItem.post

                // Read More
                readMoreButton.setOnClickListener {
                    itemClickListener.onReadMoreClick(blogItem)
                }

                // Read More
                editButton.setOnClickListener {
                    itemClickListener.onEditClick(blogItem)
                }

                // Read More
                deleteButton.setOnClickListener {
                    itemClickListener.onDeleteClick(blogItem)
                }

            }
        }

    }

}