package com.example.homework

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val editText: EditText = findViewById(R.id.etProfRadius)

        val myIntent = Intent()

        val button: Button = findViewById(R.id.btnRefresh)

        button.setOnClickListener {
            try {
                val radius = Integer.parseInt(editText.text.toString())
                myIntent.putExtra("radius", radius)
            } catch (e: NumberFormatException) {
                System.out.println("Could not parse :" + e)
            }
            setResult(Activity.RESULT_OK, myIntent)
            finish()
        }
    }
}