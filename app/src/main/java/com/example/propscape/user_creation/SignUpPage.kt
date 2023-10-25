package com.example.propscape.user_creation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.propscape.Hashed
import com.example.propscape.R
import com.example.propscape.data_classes.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpPage : AppCompatActivity() {

    private lateinit var signUpName: EditText
    private lateinit var signUpEmail: EditText
    private lateinit var signUpUsername: EditText
    private lateinit var signUpPassword: EditText
    private lateinit var signUpBtn: Button
    private lateinit var loginRedirectedText: TextView
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    val hash = Hashed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_page)

        signUpName = findViewById(R.id.signup_name)
        signUpEmail = findViewById(R.id.signup_email)
        signUpUsername = findViewById(R.id.signup_username)
        signUpPassword = findViewById(R.id.signup_password)
        signUpBtn = findViewById(R.id.signup_button)
        loginRedirectedText = findViewById(R.id.loginRedirectedText)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        signUpBtn.setOnClickListener {

            val name = signUpName.text.toString()
            val email = signUpEmail.text.toString()
            val username = signUpUsername.text.toString()
            val password = signUpPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()
                && username.isNotEmpty() && password.isNotEmpty()) {

                databaseReference.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(object: ValueEventListener {

                        override fun onDataChange(emailSnapshot: DataSnapshot) {

                            if (!emailSnapshot.exists()) {

                                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                                    databaseReference.orderByChild("username").equalTo(username)
                                        .addListenerForSingleValueEvent(object: ValueEventListener {
                                            override fun onDataChange(usernameSnapshot: DataSnapshot) {

                                                if (!usernameSnapshot.exists()) {

                                                    val id = databaseReference.push().key
                                                    val userData = UserData(id, name, email, username, hash.hash(password), null)
                                                    databaseReference.child(id!!).setValue(userData)
                                                    Toast.makeText(this@SignUpPage,
                                                        "You have signup successfully!", Toast.LENGTH_SHORT).show()
                                                    val intent = Intent(this@SignUpPage, LoginPage::class.java)
                                                    startActivity(intent)
                                                    finish()

                                                } else {

                                                    Toast.makeText(this@SignUpPage,
                                                        "This username is taken.", Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {

                                                Toast.makeText(this@SignUpPage,
                                                    "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        })

                                } else {

                                    Toast.makeText(this@SignUpPage, "Enter valid Email address!",
                                        Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(this@SignUpPage,
                                    "Email already in use.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                            Toast.makeText(this@SignUpPage,
                                "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })

            } else {

                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
        }

        loginRedirectedText.setOnClickListener {

            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
    }
}