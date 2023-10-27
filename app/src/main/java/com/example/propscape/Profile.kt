package com.example.propscape

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.propscape.databinding.ActivityProfileBinding
import com.example.propscape.profile_fragments.DisplayProfile
import com.example.propscape.profile_fragments.EditProfile
import com.example.propscape.profile_fragments.FragmentInterface

class Profile : AppCompatActivity(),FragmentInterface {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "my-pref"
    private val keyUsername = "username"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE)
        val sharedPrefUsername = sharedPreferences.getString(keyUsername, null)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, DisplayProfile(sharedPrefUsername!!))
            .addToBackStack(null)
            .commit()

        binding.fab.setOnClickListener {

            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, EditProfile())
                .addToBackStack(null).commit()
        }
    }

    override fun pop(bool: Boolean) {

        if (bool) {

            supportFragmentManager.popBackStack()
        }
    }
}