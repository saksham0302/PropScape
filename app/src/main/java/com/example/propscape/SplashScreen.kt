package com.example.propscape

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.propscape.user_creation.LoginPage

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Remove the notification bar while the room booking screen opens
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // To delay the loading time
        Handler().postDelayed(
            {
                val i = Intent(this, LoginPage::class.java)
                startActivity(i)
                finish()
            }, 3000
        )
    }
}