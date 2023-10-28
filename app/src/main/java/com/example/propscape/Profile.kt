package com.example.propscape

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.propscape.databinding.ActivityProfileBinding
import com.example.propscape.profile_fragments.EditProfile
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Profile : AppCompatActivity(), InterchangeInterface {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "my-pref"
    private val keyUsername = "username"

    private var boolFrag = true

    private lateinit var databaseReference: DatabaseReference

    private lateinit var profileImg: ImageView
    private lateinit var profileEmail: TextView
    private lateinit var profileName: TextView
    private lateinit var profileMobile: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boolFrag = true
        sharedPreferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE)
        val sharedPrefUsername = sharedPreferences.getString(keyUsername, null)

        profileImg = findViewById(R.id.profileImg)
        profileEmail = findViewById(R.id.profileEmail)
        profileName = findViewById(R.id.profileName)
        profileMobile = findViewById(R.id.profileMobile)

        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        databaseReference.child(sharedPrefUsername!!).get().addOnSuccessListener {

            if (it.exists()) {

                if (it.child("profileImageUrl").value != null) Glide.with(this)
                    .load(it.child("profileImageUrl").value).into(profileImg)
                profileEmail.text = it.child("email").value.toString()
                profileName.text = it.child("name").value.toString()
                profileMobile.text = it.child("phoneNumber").value.toString()

            }
        }

        binding.fab.setOnClickListener {

            boolFrag = false
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, EditProfile())
                .addToBackStack(null).commit()
        }
    }

    override fun interchange(bool: Boolean) {

        if (bool) {

            supportFragmentManager.popBackStack()
            boolFrag = true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

        if (boolFrag) {
            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }
}