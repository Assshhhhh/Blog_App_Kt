package com.example.blogappkt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogappkt.databinding.ActivityReadMoreBinding
import com.example.blogappkt.models.BlogsModel

class ReadMoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backImageButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val blogs = intent.getParcelableExtra<BlogsModel>("blogItem")
        if (blogs != null){

            binding.tvReadTitle.text = blogs.heading
            binding.tvReadUsername.text = blogs.userName
            binding.tvReadDate.text = blogs.date
            binding.tvReadDescription.text = blogs.post

            val userImageUrl = blogs.profileImage
            Glide.with(this)
                .load(userImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileReadCircImage)

        }
        else{
            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
        }
    }
}