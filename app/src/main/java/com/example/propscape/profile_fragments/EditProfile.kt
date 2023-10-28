package com.example.propscape.profile_fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.propscape.Profile
import com.example.propscape.R
import com.example.propscape.data_classes.UserData
import com.example.propscape.user_creation.LoginPage
import com.example.propscape.user_creation.RoomDatabaseDemo
import com.example.propscape.user_creation.RoomDatabaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditProfile : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var uri: Uri
    private lateinit var oldImageUrl: String
    private lateinit var newImage: String

    private lateinit var view: View

    private lateinit var profileImg: ImageView
    private lateinit var profileName: EditText
    private lateinit var profileEmail: EditText
    private lateinit var profilePassword: EditText
    private lateinit var profilePhone: EditText
    private lateinit var saveButton: Button

    private lateinit var oldPassword: String
    private lateinit var roomDatabase: RoomDatabaseDemo

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "my-pref"
    private val keyUsername = "username"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        // Inflate the layout for this fragment

        val con = view.context

        profileImg = view.findViewById(R.id.profileImg)
        profileName = view.findViewById(R.id.profileName)
        profileEmail = view.findViewById(R.id.profileEmail)
        profilePassword = view.findViewById(R.id.profilePassword)
        profilePhone = view.findViewById(R.id.profilePhone)
        saveButton = view.findViewById(R.id.saveButton)

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
                result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                uri = data?.data!!
                profileImg.setImageURI(uri)

            } else {

                Toast.makeText(con, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        sharedPreferences = con.getSharedPreferences(sharedPrefName, AppCompatActivity.MODE_PRIVATE)
        val sharedPrefUsername = sharedPreferences.getString(keyUsername, null)

        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        Log.d("un", "$sharedPrefUsername")
        if (sharedPrefUsername != null) {
            databaseReference.child(sharedPrefUsername).get().addOnSuccessListener {

                if (it.exists()) {

                    oldImageUrl = it.child("profileImageUrl").value.toString()
                    if (it.child("profileImageUrl").value != null) Glide.with(con).load(oldImageUrl).into(profileImg)
                    profileEmail.setText(it.child("email").value.toString())
                    profileName.setText(it.child("name").value.toString())
                    profilePhone.setText(it.child("phoneNumber").value.toString())
                    oldPassword = it.child("password").value.toString()
                    profilePassword.setText(oldPassword)
                }
            }
        }

        profileImg.setOnClickListener {

            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        saveButton.setOnClickListener {

            val storageReference = uri.lastPathSegment?.let {
                FirebaseStorage.getInstance().reference.child("User Images").child(it)
            }

            val builder = AlertDialog.Builder(con)
            builder.setCancelable(false)
            builder.setView(R.layout.progress_layout_saving)
            val dialog: AlertDialog = builder.create()
            dialog.show()

            storageReference?.putFile(uri)
                ?.addOnSuccessListener { taskSnapshot ->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isComplete);
                    val urlImage = uriTask.result
                    newImage = urlImage.toString()
                    if (sharedPrefUsername != null) {
                        updateData(con, sharedPrefUsername)
                    }
                    dialog.dismiss()
                }
                ?.addOnFailureListener {
                    dialog.dismiss()
                }
        }

        return view
    }

    private fun updateData(con: Context, sharedPrefUsername: String) {

        val data = UserData(
            profileName.text.toString(),
            profileEmail.text.toString(),
            sharedPrefUsername,
            profilePassword.text.toString(),
            newImage,
            profilePhone.text.toString()
        )

        FirebaseDatabase.getInstance().reference.child("Users").child(sharedPrefUsername).setValue(data)
            .addOnSuccessListener {

                if (oldImageUrl != "") {

                    val storage = FirebaseStorage.getInstance()
                    val storageReference = storage.getReferenceFromUrl(oldImageUrl)

                    storageReference.delete().addOnSuccessListener {

                        if (oldPassword != profilePassword.text.toString()) {

                            GlobalScope.launch {

                                roomDatabase.RoomDatabaseDao().delete(
                                    RoomDatabaseUser(
                                        sharedPrefUsername,
                                        oldPassword
                                    )
                                )
                            }

                            sharedPreferences = con.getSharedPreferences(sharedPrefName,
                                AppCompatActivity.MODE_PRIVATE
                            )
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.clear()
                            editor.apply()
                            startActivity(Intent(con, LoginPage::class.java))
                            activity?.finish()
                        }

                        Toast.makeText(con, "Saved", Toast.LENGTH_SHORT).show()
                        val mai = activity as Profile
                        mai.interchange(true)
                    }
                }

            }.addOnFailureListener {

                    e ->
                Toast.makeText(con, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}