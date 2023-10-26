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
import com.example.propscape.data_classes.PropScapeData
import com.example.propscape.databinding.ActivityUploadPropScapeBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date

class UploadPropScape : AppCompatActivity() {

    private lateinit var binding: ActivityUploadPropScapeBinding
    private lateinit var uri: Uri
    private lateinit var imageURL: String
    private lateinit var propertyType: String

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "my-pref"
    private val keyUsername = "username"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPropScapeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                binding.uploadImg.setImageURI(uri)

            } else {

                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.uploadImg.setOnClickListener {

            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        binding.uploadBtn.setOnClickListener {

            saveData()
        }
    }

    private fun saveData() {

        val storageReference = uri.lastPathSegment?.let {
            FirebaseStorage.getInstance().reference
                .child("Property Images").child(it)
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
                imageURL = urlImage.toString()
                uploadData()
                dialog.dismiss()
            }
            ?.addOnFailureListener { e ->
                dialog.dismiss()
            }

    }

    private fun uploadData() {

        sharedPreferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE)

        val sdf = SimpleDateFormat("dd-MM-yyyyHH:mm:ssz")
        val currentDateAndTime = sdf.format(Date())

        val sharedPrefUsername = sharedPreferences.getString(keyUsername, null)

        val propId = sharedPrefUsername+currentDateAndTime

        val data = PropScapeData(
            sharedPrefUsername,
            binding.ownerName.text.toString(),
            binding.ownerPhoneNumber.text.toString(),
            binding.address.text.toString(),
            binding.city.text.toString(),
            binding.state.text.toString(),
            binding.country.text.toString(),
            propertyType,
            binding.description.text.toString(),
            imageURL,
            binding.price.text.toString())

        FirebaseDatabase.getInstance().reference.child("PropScape").child(propId).setValue(data)
            .addOnSuccessListener {

                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                finish()

            }.addOnFailureListener {

                    e ->

                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}