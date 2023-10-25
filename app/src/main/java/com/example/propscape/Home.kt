package com.example.propscape

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.propscape.user_creation.LoginPage

class Home : AppCompatActivity() {

    private lateinit var tv: TextView
    private lateinit var btn: Button

    private lateinit var sharedPreferences: SharedPreferences

    private val sharedPrefName = "my-pref"
    private val keyUsername = "username"
    private val keyPassword = "password"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tv = findViewById(R.id.tv)
        btn = findViewById(R.id.logOut)
        tv.text = """
            Name: ${intent.getStringExtra("name")},
            Email: ${intent.getStringExtra("email")},
            Username: ${intent.getStringExtra("username")},
            Profile: ${intent.getStringExtra("profile")}
        """.trimIndent()

        btn.setOnClickListener {

            sharedPreferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            startActivity(Intent(this, LoginPage::class.java))
            finish()
        }
    }
}