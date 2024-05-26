package com.example.blogappkt.registeration

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogappkt.MainActivity
import com.example.blogappkt.R
import com.example.blogappkt.databinding.ActivitySigninAndRegisterationBinding
import com.example.blogappkt.databinding.ActivityWelcomeBinding
import com.example.blogappkt.models.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SigninAndRegisterationActivity : AppCompatActivity() {

    private val binding: ActivitySigninAndRegisterationBinding by lazy {
        ActivitySigninAndRegisterationBinding.inflate(layoutInflater)
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseStorage: FirebaseStorage

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        val action: String? = intent.getStringExtra("action")

        if (action == "login"){

            binding.apply {

                // Visible Items
                loginButton.visibility = View.VISIBLE
                editLoginEmail.visibility = View.VISIBLE
                editLoginPassword.visibility = View.VISIBLE
                // Disabled Items
                registerButton.isEnabled = false
                registerButton.alpha = 0.5f
                tvNewHere.isEnabled = false
                tvNewHere.alpha = 0.5f
                // Invisible Items
                registerUserImage.visibility = View.GONE
                registerUserName.visibility = View.GONE
                registerUserEmail.visibility = View.GONE
                registerUserPassword.visibility = View.GONE

                // Signin User
                loginButton.setOnClickListener {

                    val login_email: String = editLoginEmail.text.toString()
                    val login_pass: String = editLoginPassword.text.toString()

                    if (login_email.isEmpty() || login_pass.isEmpty()){
                        Toast.makeText(root.context, "Please fill all the credentials", Toast.LENGTH_SHORT).show()
                    }
                    else{

                        mAuth.signInWithEmailAndPassword(login_email, login_pass).addOnCompleteListener {

                            if (it.isSuccessful){
                                Toast.makeText(root.context, "Sigin Successful 🤡", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(root.context, MainActivity::class.java))
                                finish()
                            }
                            else{
                                Toast.makeText(root.context, "Sigin Failed", Toast.LENGTH_SHORT).show()
                            }

                        }

                    }

                }

            }

        }
        else if (action == "register"){

            binding.apply {

                // Disabled Items
                loginButton.isEnabled = false
                loginButton.alpha = 0.5f

                // Register User
                registerButton.setOnClickListener {

                    val reg_name: String = registerUserName.text.toString()
                    val reg_email: String = registerUserEmail.text.toString()
                    val reg_pass: String = registerUserPassword.text.toString()

                    if (reg_name.isEmpty() || reg_email.isEmpty() || reg_pass.isEmpty() || imageUri == null){
                        Toast.makeText(root.context, "Please fill all the credentials", Toast.LENGTH_SHORT).show()
                    }
                    else{

                        mAuth.createUserWithEmailAndPassword(reg_email, reg_pass).addOnCompleteListener {
                            if (it.isSuccessful){

                                val user: FirebaseUser? = mAuth.currentUser
                                mAuth.signOut()
                                user?.let {
                                    val userRef: DatabaseReference = firebaseDatabase.getReference("Users")
                                    val userId: String = user.uid
                                    val userData: UserData = UserData(reg_name, reg_email)

                                    // Insert to Firebase Realtime Database
                                    userRef.child(userId).setValue(userData)

                                    // Upload Image to Firebase Storage
                                    val storageRef: StorageReference = firebaseStorage.reference.child("Profile_Images/$userId.jpg")
                                    storageRef.putFile(imageUri!!).addOnCompleteListener {
                                        storageRef.downloadUrl.addOnCompleteListener {imageUri ->

                                            val imageUrl =imageUri.result.toString()
                                            userRef.child(userId).child("profileImage").setValue(imageUrl)

                                        }
                                    }
                                    Toast.makeText(root.context, "User Registered Successfully", Toast.LENGTH_SHORT).show()

                                    registerUserName.setText("")
                                    registerUserEmail.setText("")
                                    registerUserPassword.setText("")
                                    registerUserImage.setImageURI(null)
                                    startActivity(Intent(root.context, WelcomeActivity::class.java))
                                    finish()

                                }


                            }
                            else{
                                Toast.makeText(root.context, "User Registration Failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }

                // Select Image
                registerUserImage.setOnClickListener {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST)

                }

            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){

            imageUri = data.data
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.registerUserImage)

        }

    }
}