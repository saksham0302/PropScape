package com.example.propscape.recycler_adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.propscape.DisplayProperty
import com.example.propscape.R
import com.example.propscape.UpdatePropScape
import com.example.propscape.data_classes.PropScapeData
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class MyPropertyAdapter(
    private val context: Context,
    private var dataList: List<PropScapeData>
): RecyclerView.Adapter<MyPropertyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val recImage: ShapeableImageView = itemView.findViewById(R.id.recImage)
        val recCityStateCountry: TextView = itemView.findViewById(R.id.recCityStateCountry)
        val recAddress: TextView = itemView.findViewById(R.id.recAddress)
        val recType: TextView = itemView.findViewById(R.id.recType)
        val recPrice: TextView = itemView.findViewById(R.id.recPrice)
        val recCheckOnMaps: Button = itemView.findViewById(R.id.recCheckOnMaps)
        val recEdit: Button = itemView.findViewById(R.id.recEdit)
        val recDelete: Button = itemView.findViewById(R.id.recDelete)
        val recCard: CardView = itemView.findViewById(R.id.recCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.myproperty_recycler, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(context).load(dataList[position].propertyImageUrl).into(holder.recImage)
        holder.recCityStateCountry.text =
            "${dataList[position].city}, ${dataList[position].state}, ${dataList[position].country}"
        holder.recAddress.text = dataList[position].address
        holder.recType.text = dataList[position].propertyType
        holder.recPrice.text = dataList[position].propertyPrice

        holder.recCheckOnMaps.setOnClickListener {
            Toast.makeText(context, "Check on Maps Clicked", Toast.LENGTH_SHORT).show()
        }

        holder.recEdit.setOnClickListener {

            val intent = Intent(context, UpdatePropScape::class.java)
            intent.putExtra("ownerName", dataList[position].ownerName)
            intent.putExtra("ownerPhone", dataList[position].ownerPhoneNumber)
            intent.putExtra("address", dataList[position].address)
            intent.putExtra("city", dataList[position].city)
            intent.putExtra("state", dataList[position].state)
            intent.putExtra("country", dataList[position].country)
            intent.putExtra("type", dataList[position].propertyType)
            intent.putExtra("description", dataList[position].propertyDescription)
            intent.putExtra("image", dataList[position].propertyImageUrl)
            intent.putExtra("price", dataList[position].propertyPrice)
            intent.putExtra("key", dataList[position].key)
            context.startActivity(intent)
        }

        holder.recDelete.setOnClickListener {

            val database = FirebaseDatabase.getInstance().reference.child("PropScape")
            val storage = FirebaseStorage.getInstance()

            val storageReference = dataList[position].propertyImageUrl?.let {
                storage.getReferenceFromUrl(
                    it
                )
            }

            storageReference?.delete()?.addOnSuccessListener {
                dataList[position].key?.let {
                        it1 ->
                    database.child(it1).removeValue().addOnSuccessListener {

                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        holder.recCard.setOnClickListener {

            val intent = Intent(context, DisplayProperty::class.java)
            intent.putExtra("ownerName", dataList[position].ownerName)
            intent.putExtra("ownerPhone", dataList[position].ownerPhoneNumber)
            intent.putExtra("address", dataList[position].address)
            intent.putExtra("cityStateCountry", "${dataList[position].city}, ${dataList[position].state}, ${dataList[position].country}")
            intent.putExtra("type", dataList[position].propertyType)
            intent.putExtra("description", dataList[position].propertyDescription)
            intent.putExtra("image", dataList[position].propertyImageUrl)
            intent.putExtra("price", dataList[position].propertyPrice)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {

        return dataList.size
    }
}
