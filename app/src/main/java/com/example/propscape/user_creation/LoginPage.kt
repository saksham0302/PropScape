package com.example.propscape.user_creation

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.Room
import com.example.propscape.Hashed
import com.example.propscape.Home
import com.example.propscape.R
import com.example.propscape.data_classes.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginPage : AppCompatActivity() {

    private lateinit var cld : ConnectionLiveData
    private lateinit var internetLayout : CardView
    private lateinit var wifiImage: ImageView

    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginBtn: Button
    private lateinit var savePassword: CheckBox
    private lateinit var fetchPassword: TextView
    private lateinit var signUpRedirectedText: TextView

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var roomDatabase: RoomDatabaseDemo

    private val sharedPrefName = "my-pref"
    private val keyUsername = "username"
    private val keyPassword = "password"

    val hash = Hashed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        checkNetworkConnection()

        internetLayout = findViewById(R.id.internetLayout)
        wifiImage = findViewById(R.id.wifiImage)

        loginUsername = findViewById(R.id.login_username)
        loginPassword = findViewById(R.id.login_password)
        savePassword = findViewById(R.id.savePassword)
        fetchPassword = findViewById(R.id.fetchPassword)
        loginBtn = findViewById(R.id.login_button)
        signUpRedirectedText = findViewById(R.id.signUpRedirectedText)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")
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

        fetchPassword.setOnClickListener {

            val rdUsername = loginUsername.text.toString()
            val pwd = roomDatabase.RoomDatabaseDao().getUser(rdUsername)

            if (pwd == null)
                Toast.makeText(this, "You have not saved the password on database!", Toast.LENGTH_SHORT).show()
            else {

                loginPassword.setText(pwd.password)
                savePassword.isChecked = false
                savePassword.visibility = View.GONE
            }
        }


        loginBtn.setOnClickListener {

            if (validateUsername() && validatePassword()) {

                loginUser(loginUsername.text.toString(), loginPassword.text.toString())
            }
        }

        signUpRedirectedText.setOnClickListener {

            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkNetworkConnection() {
        cld = ConnectionLiveData(application)

        cld.observe(this) { isConnected ->

            if (isConnected) {

                internetLayout.visibility = View.GONE
                wifiImage.setColorFilter(Color.GREEN)

            } else {
                internetLayout.visibility = View.VISIBLE
                wifiImage.setColorFilter(Color.RED)
            }
        }
    }

    private fun loginUser(userUsername: String, userPassword: String) {

        databaseReference.orderByChild("username").equalTo(userUsername)
            .addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {

                        val userData = userSnapshot.getValue(UserData::class.java)

                        if (userData != null && userData.password == hash.hash(userPassword)) {

                            Toast.makeText(this@LoginPage, "Login Successful!", Toast.LENGTH_SHORT).show()

                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString(keyUsername, userUsername)
                            editor.putString(keyPassword, userPassword)
                            editor.apply()

                            if (savePassword.isChecked) {

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

                            loginPassword.error = "Invalid Credentials"
                            loginPassword.requestFocus()
                        }
                    }

                } else {

                    loginUsername.error = "User does not exist"
                    loginUsername.requestFocus()
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

        val validate = loginUsername.text.toString()

        return if (validate.isEmpty()) {

            loginUsername.error = "Username cannot be empty"
            false
        } else {
            loginUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {

        val validate = loginPassword.text.toString()

        return if (validate.isEmpty()) {

            loginPassword.error = "Password cannot be empty"
            false

        } else {

            loginPassword.error = null
            true
        }
    }
}