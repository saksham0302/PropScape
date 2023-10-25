package com.example.propscape

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class Home : AppCompatActivity() {

    private lateinit var tv: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tv = findViewById(R.id.tv)
        tv.text = """
            Name: ${intent.getStringExtra("name")},
            Email: ${intent.getStringExtra("email")},
            Username: ${intent.getStringExtra("username")},
            Profile: ${intent.getStringExtra("profile")}
        """.trimIndent()
    }
}