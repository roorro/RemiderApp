package com.example.homework

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val name: EditText = findViewById(R.id.etUsername)
        val password: EditText = findViewById(R.id.etPassword)
        val login: Button = findViewById(R.id.btnLogin)

        val pref: SharedPreferences = getPreferences(Context.MODE_PRIVATE)

        storePrefs(pref)

        login.setOnClickListener {
            Log.d("HW", "Button Clicked!")

            checkLogin(name.text.toString(), password.text.toString(), pref)

            }
        }


    private fun storePrefs(prefs: SharedPreferences) {
        val username: String = "kissa"
        val password: String = "koira"


        val editor = prefs.edit()

        editor.putString("Username", username)
        editor.putString("Password", password)
        editor.apply()
    }


    private fun checkLogin(Username: String, Password:String, prefs:SharedPreferences) {

        val correctUsername = prefs.getString("Username", "")
        val correctPassword = prefs.getString("Password", "")
        if ((Username == correctUsername) && (Password == correctPassword)) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        else {
            Toast.makeText(applicationContext, "Wrong username or password!", Toast.LENGTH_SHORT).show()
        }

    }
}