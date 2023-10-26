package com.example.propscape.user_creation

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.room.Room
import com.example.propscape.Home
import com.example.propscape.data_classes.UserData
import com.example.propscape.databinding.ActivityLoginPageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginPage : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPageBinding

    private lateinit var cld : ConnectionLiveData

    private lateinit var databaseReference: DatabaseReference

    private lateinit var roomDatabase: RoomDatabaseDemo

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "my-pref"
    private val keyUsername = "username"
    private val keyPassword = "password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNetworkConnection()

        sharedPreferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE)
        roomDatabase = Room.databaseBuilder(
            applicationContext,
            RoomDatabaseDemo::class.java, "user"
        ).allowMainThreadQueries().build()

        val sharedPrefUsername = sharedPreferences.getString(keyUsername, null)

        if (sharedPrefUsername != null) {

            val intent = Intent(this@LoginPage, Home::class.java)
            startActivity(intent)
            finish()
        }

        binding.fetchPassword.setOnClickListener {

            val rdUsername = binding.loginUsername.text.toString()
            val pwd = roomDatabase.RoomDatabaseDao().getUser(rdUsername)

            if (pwd == null)
                Toast.makeText(this, "You have not saved the password on database!", Toast.LENGTH_SHORT).show()
            else {

                binding.loginPassword.setText(pwd.password)
                binding.savePassword.isChecked = false
                binding.savePassword.visibility = View.GONE
            }
        }


        binding.loginButton.setOnClickListener {

            if (validateUsername() && validatePassword()) {

                loginUser(binding.loginUsername.text.toString(), binding.loginPassword.text.toString())
            }
        }

        binding.signUpRedirectedText.setOnClickListener {

            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkNetworkConnection() {
        cld = ConnectionLiveData(application)

        cld.observe(this) { isConnected ->

            if (isConnected) {

                binding.internetLayout.visibility = View.GONE
                binding.wifiImage.setColorFilter(Color.GREEN)

            } else {
                binding.internetLayout.visibility = View.VISIBLE
                binding.wifiImage.setColorFilter(Color.RED)
            }
        }
    }

    private fun loginUser(userUsername: String, userPassword: String) {

        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        databaseReference.orderByChild("username").equalTo(userUsername)
            .addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {

                        val userData = userSnapshot.getValue(UserData::class.java)

                        if (userData != null && userData.password == userPassword) {

                            Toast.makeText(this@LoginPage, "Login Successful!", Toast.LENGTH_SHORT).show()

                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString(keyUsername, userUsername)
                            editor.putString(keyPassword, userPassword)
                            editor.apply()

                            if (binding.savePassword.isChecked) {

                                GlobalScope.launch {
                                    roomDatabase.RoomDatabaseDao().insert(
                                        RoomDatabaseUser(
                                            userUsername,
                                            userPassword
                                        )
                                    )
                                }
                            }

                            val intent = Intent(this@LoginPage, Home::class.java)
                            startActivity(intent)
                            finish()
                            return

                        } else {

                            binding.loginPassword.error = "Invalid Credentials"
                            binding.loginPassword.requestFocus()
                        }
                    }

                } else {

                    binding.loginUsername.error = "User does not exist"
                    binding.loginUsername.requestFocus()
                }
            }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(
                        this@LoginPage,
                        "Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun validateUsername(): Boolean {

        val validate = binding.loginUsername.text.toString()

        return if (validate.isEmpty()) {

            binding.loginUsername.error = "Username cannot be empty"
            false
        } else {
            binding.loginUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {

        val validate = binding.loginPassword.text.toString()

        return if (validate.isEmpty()) {

            binding.loginPassword.error = "Password cannot be empty"
            false

        } else {

            binding.loginPassword.error = null
            true
        }
    }
}