package com.example.homework

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val adapter = NotificationsAdapter(this, Supplier.notifications)
        recyclerView.adapter = adapter

        val add: Button = findViewById(R.id.btnAdd)
        val profile: Button = findViewById(R.id.btnProfile)
        val logout: Button = findViewById(R.id.btnLogout)

        add.setOnClickListener{ Toast.makeText(applicationContext, "Functionality not added yet", Toast.LENGTH_SHORT).show() }

        profile.setOnClickListener { startActivity(Intent(applicationContext, ProfileActivity::class.java)) }
        logout.setOnClickListener { finish() }



    }


}
