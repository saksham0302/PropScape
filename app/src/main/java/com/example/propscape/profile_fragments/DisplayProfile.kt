package com.example.propscape.profile_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.propscape.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DisplayProfile(sharedPrefUsername: String) : Fragment() {

    private val un = sharedPrefUsername

    private lateinit var view: View

    private lateinit var databaseReference: DatabaseReference

    private lateinit var profileImg: ImageView
    private lateinit var profileEmail: TextView
    private lateinit var profileName: TextView
    private lateinit var profileMobile: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_display_profile, container, false)
        // Inflate the layout for this fragment

        val con = view.context

        profileImg = view.findViewById(R.id.profileImg)
        profileEmail = view.findViewById(R.id.profileEmail)
        profileName = view.findViewById(R.id.profileName)
        profileMobile = view.findViewById(R.id.profileMobile)

        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        databaseReference.child(un).get().addOnSuccessListener {

            if (it.exists()) {

                if (it.child("profileImageUrl").value != null) Glide.with(con).load(it.child("profileImageUrl").value).into(profileImg)
                profileEmail.text = it.child("email").value.toString()
                profileName.text = it.child("name").value.toString()
                profileMobile.text = it.child("phoneNumber").value.toString()

            }
        }

        return view
    }
}