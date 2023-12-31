package com.example.propscape.recycler_adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.propscape.CheckMaps
import com.example.propscape.DisplayProperty
import com.example.propscape.R
import com.example.propscape.data_classes.PropScapeData
import com.google.android.material.imageview.ShapeableImageView

class PropertyAdapter(
    private val context: Context,
    private var dataList: List<PropScapeData>
): RecyclerView.Adapter<PropertyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val recImage: ShapeableImageView = itemView.findViewById(R.id.recImage)
        val recCityStateCountry: TextView = itemView.findViewById(R.id.recCityStateCountry)
        val recAddress: TextView = itemView.findViewById(R.id.recAddress)
        val recType: TextView = itemView.findViewById(R.id.recType)
        val recPrice: TextView = itemView.findViewById(R.id.recPrice)
        val recCheckOnMaps: Button = itemView.findViewById(R.id.recCheckOnMaps)
        val recCard: CardView = itemView.findViewById(R.id.recCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.property_recycler, parent, false)
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

            val intent = Intent(context, CheckMaps::class.java)
            intent.putExtra("addressGeo",
                "${dataList[position].address}, ${dataList[position].city}, " +
                        "${dataList[position].state}, ${dataList[position].country}")
            context.startActivity(intent)
        }

        holder.recCard.setOnClickListener {

            val intent = Intent(context, DisplayProperty::class.java)
            intent.putExtra("ownerName", dataList[position].ownerName)
            intent.putExtra("ownerPhone", dataList[position].ownerPhoneNumber)
            intent.putExtra("address", dataList[position].address)
            intent.putExtra("addressGeo",
                "${dataList[position].address}, ${dataList[position].city}, " +
                        "${dataList[position].state}, ${dataList[position].country}")
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

    @SuppressLint("NotifyDataSetChanged")
    fun searchDataList(searchList: ArrayList<PropScapeData>) {

        dataList = searchList
        notifyDataSetChanged()
    }
}