package com.example.propscape.user_creation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.propscape.data_classes.UserData
import com.example.propscape.databinding.ActivitySignUpPageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpPage : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpPageBinding

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        binding.signupButton.setOnClickListener {

            val name = binding.signupName.text.toString()
            val email = binding.signupEmail.text.toString()
            val username = binding.signupUsername.text.toString()
            val password = binding.signupPassword.text.toString()

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

                                                    val userData = UserData(name, email, username, password, null)
                                                    databaseReference.child(username!!).setValue(userData)
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
                                        Toast.LENGTH_SHORT).show()
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

        binding.loginRedirectedText.setOnClickListener {

            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
    }
}