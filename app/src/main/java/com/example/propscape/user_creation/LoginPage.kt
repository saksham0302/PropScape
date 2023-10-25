package com.example.propscape.user_creation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.propscape.Hashed
import com.example.propscape.Home
import com.example.propscape.R
import com.example.propscape.data_classes.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginPage : AppCompatActivity() {

    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginBtn: Button
    private lateinit var signUpRedirectedText: TextView
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    val hash = Hashed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        loginUsername = findViewById(R.id.login_username)
        loginPassword = findViewById(R.id.login_password)
        loginBtn = findViewById(R.id.login_button)
        signUpRedirectedText = findViewById(R.id.signUpRedirectedText)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

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

    private fun loginUser(userUsername: String, userPassword: String) {

        databaseReference.orderByChild("username").equalTo(userUsername)
            .addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {

                        val userData = userSnapshot.getValue(UserData::class.java)

                        if (userData != null && userData.password == hash.hash(userPassword)) {

                            Toast.makeText(this@LoginPage, "Login Successful!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginPage, Home::class.java)
                            intent.putExtra("name", userData.name)
                            intent.putExtra("email", userData.email)
                            intent.putExtra("username", userData.username)
                            intent.putExtra("profile", userData.profileImageUrl)
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

            loginPassword.error = "Username cannot be empty"
            false

        } else {

            loginPassword.error = null
            true
        }
    }
}