package com.example.propscape

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.propscape.data_classes.PropScapeData
import com.example.propscape.databinding.ActivityUpdatePropScapeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class UpdatePropScape : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePropScapeBinding
    private lateinit var propertyType: String
    private lateinit var key: String

    private lateinit var databaseReference: DatabaseReference
    private lateinit var uri: Uri
    private lateinit var oldImageUrl: String
    private lateinit var newImage: String

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "my-pref"
    private val keyUsername = "username"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePropScapeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras

        val item = arrayOf("Property", "Land")
        val adapterItem = ArrayAdapter(this, R.layout.list_item, item)
        binding.autoCompleteTextview.setAdapter(adapterItem)
        binding.autoCompleteTextview.setOnItemClickListener { parent, _, position, _ ->

            propertyType = parent.getItemAtPosition(position).toString()
        }

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
                result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                uri = data?.data!!
                binding.updateImg.setImageURI(uri)

            } else {

                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.updateImg.setOnClickListener {

            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        if (bundle != null) {

            oldImageUrl = bundle.getString("image").toString()
            Glide.with(this).load(oldImageUrl).into(binding.updateImg)
            binding.ownerName.setText(bundle.getString("ownerName"))
            binding.ownerPhoneNumber.setText(bundle.getString("ownerPhone"))
            binding.address.setText(bundle.getString("address"))
            binding.city.setText(bundle.getString("city"))
            binding.state.setText(bundle.getString("state"))
            binding.country.setText(bundle.getString("country"))
            propertyType = bundle.getString("type").toString()
            binding.autoCompleteTextview.setText(bundle.getString("type"), false)
            binding.description.setText(bundle.getString("description"))
            binding.price.setText(bundle.getString("price"))
            key = bundle.getString("key").toString()
        }

        databaseReference = FirebaseDatabase.getInstance().reference
            .child("PropScape").child(key)

        binding.updateBtn.setOnClickListener {

            saveData()
        }
    }

    private fun saveData() {

        val storageReference = uri.lastPathSegment?.let {
            FirebaseStorage.getInstance().reference.child("Property Images").child(it)
        }

        val builder = AlertDialog.Builder(this)
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
                updateData()
                dialog.dismiss()
            }
            ?.addOnFailureListener {
                dialog.dismiss()
            }

    }

    private fun updateData() {

        sharedPreferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE)
        val sharedPrefUsername = sharedPreferences.getString(keyUsername, null)

        val data = PropScapeData(
            key,
            sharedPrefUsername,
            binding.ownerName.text.toString(),
            binding.ownerPhoneNumber.text.toString(),
            binding.address.text.toString(),
            binding.city.text.toString(),
            binding.state.text.toString(),
            binding.country.text.toString(),
            propertyType,
            binding.description.text.toString(),
            newImage,
            binding.price.text.toString())

        FirebaseDatabase.getInstance().reference.child("PropScape").child(key).setValue(data)
            .addOnSuccessListener {

                val storage = FirebaseStorage.getInstance()
                val storageReference = storage.getReferenceFromUrl(oldImageUrl)

                storageReference.delete().addOnSuccessListener {

                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    finish()
                }

            }.addOnFailureListener {

                    e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}