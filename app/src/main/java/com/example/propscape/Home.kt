package com.example.propscape

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.propscape.data_classes.PropScapeData
import com.example.propscape.databinding.ActivityHomeBinding
import com.example.propscape.recycler_adapters.MyPropertyAdapter
import com.example.propscape.recycler_adapters.PropertyAdapter
import com.example.propscape.user_creation.LoginPage
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "my-pref"
    private val keyUsername = "username"

    private lateinit var databaseReference: DatabaseReference

    val dataList = ArrayList<PropScapeData>()
    val adapter = PropertyAdapter(this, dataList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.navView.setNavigationItemSelectedListener(this)

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.drawer, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        binding.drawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
        binding.search.clearFocus()

        binding.recyclerView.layoutManager = GridLayoutManager(this, 1)

        if (intent.getBooleanExtra("bool", false)) {

            myProperty()
            binding.navView.setCheckedItem(R.id.nav_my_properties)

        } else {
            property("")
            binding.navView.setCheckedItem(R.id.nav_home)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {

            binding.drawer.closeDrawer(GravityCompat.START)

        } else {

            binding.navView.setNavigationItemSelectedListener(this)

            if (!binding.navView.menu.findItem(R.id.nav_home).isChecked) {

                binding.navView.setCheckedItem(R.id.nav_home)
                property("")
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.nav_profile -> {
                startActivity(Intent(this, Profile::class.java))
                finish()
            }

            R.id.nav_home -> property("")

            R.id.nav_my_properties -> myProperty()

            R.id.nav_log_out -> logOut()

            R.id.nav_property -> property("property")

            R.id.nav_land -> property("land")
        }

        binding.drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logOut() {

        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Log Out")
        alertDialog.setMessage("Are you sure, you want to log out?")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->

            sharedPreferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(this, LoginPage::class.java))
            finish()
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { dialog, _ ->

            dialog.dismiss()
        }

        alertDialog.show()
    }

    private fun property(searchText: String) {

        binding.search.visibility = View.VISIBLE
        binding.title.visibility = View.GONE
        binding.fab.visibility = View.GONE

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout_loading)
        val dialog = builder.create()

        binding.recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().reference.child("PropScape")
        dialog.show()

        databaseReference.addValueEventListener(object : ValueEventListener {

            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()
                for (itemSnapshot in snapshot.children) {

                    val dataClass = itemSnapshot.getValue(PropScapeData::class.java)
                    if (dataClass != null) {
                        dataList.add(dataClass)
                    }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {

                dialog.dismiss()
            }
        })

        search(searchText)
    }

    private fun myProperty() {

        binding.search.visibility = View.GONE
        binding.title.visibility = View.VISIBLE
        binding.fab.visibility = View.VISIBLE

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout_loading)
        val dialog = builder.create()

        val myPropertyAdapter = MyPropertyAdapter(this, dataList)
        binding.recyclerView.adapter = myPropertyAdapter

        sharedPreferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE)
        val sharedPrefUsername = sharedPreferences.getString(keyUsername, null)

        databaseReference = FirebaseDatabase.getInstance().reference.child("PropScape")
        dialog.show()

        databaseReference.orderByChild("ownerUsername").equalTo(sharedPrefUsername)
            .addValueEventListener(object : ValueEventListener {

                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    dataList.clear()
                    for (itemSnapshot in snapshot.children) {

                        val dataClass = itemSnapshot.getValue(PropScapeData::class.java)
                        if (dataClass != null) {
                            dataList.add(dataClass)
                        }
                    }
                    myPropertyAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {

                    dialog.dismiss()
                }
            })

        binding.fab.setOnClickListener {

            startActivity(Intent(this, UploadPropScape::class.java))
        }
    }

    private fun search(searchText: String) {

        binding.search.setQuery(searchText, false)
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchList(newText)
                }
                return false
            }
        })
    }
    fun searchList(text: String) {
        val searchList = ArrayList<PropScapeData>()

        for (dataClass in dataList) {

            if (dataClass.city?.lowercase()?.startsWith(text.lowercase()) == true ||
                dataClass.state?.lowercase()?.startsWith(text.lowercase()) == true ||
                dataClass.propertyType?.lowercase()?.startsWith(text.lowercase()) == true ||
                dataClass.address?.lowercase()?.contains(text.lowercase()) == true) {

                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }
}