package com.example.propscape

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.propscape.databinding.ActivityDisplayPropertyBinding

class DisplayProperty : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayPropertyBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.cityStateCountry.text = intent.getStringExtra("cityStateCountry")
        Glide.with(this).load(intent.getStringExtra("image")).into(binding.image)
        binding.address.text = intent.getStringExtra("address")
        binding.descriptionOwner.text = """
                ${intent.getStringExtra("description")}
                
                Owner details are,
                Name: ${intent.getStringExtra("ownerName")}
                Phone: ${intent.getStringExtra("ownerPhone")}
            """.trimIndent()
        binding.type.text = intent.getStringExtra("type")
        binding.price.text = intent.getStringExtra("price")

        binding.checkOnMaps.setOnClickListener {

            val address = intent.getStringExtra("addressGeo")
            val intent = Intent(this, CheckMaps::class.java)
            intent.putExtra("addressGeo", address)
            startActivity(intent)
        }
    }
}