package com.example.homework

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var dbHandler: ReminderDBHandler
    }

    private val PERMISSION_FINE_LOCATION: Int = 99
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var myLatitude: Double = 0.0
    private var myLongitude: Double = 0.0
    private var myRadius: Int = 400

    //private val list = generateList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler = ReminderDBHandler(this)
        buildRV()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
                .setInterval(30000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val location : Location = locationResult.lastLocation
                updateUIValues(location)
            }
        }


        val add: Button = findViewById(R.id.btnAdd)
        val profile: Button = findViewById(R.id.btnProfile)
        val logout: Button = findViewById(R.id.btnLogout)
        val track: Button = findViewById(R.id.btnTrackLocation)
        val virtualLocation: Button = findViewById(R.id.btnVirtualLocation)
        val tvRadius : TextView = findViewById(R.id.tvRadius)
        tvRadius.text = "Radius: $myRadius"

        add.setOnClickListener{ startActivityForResult(Intent(applicationContext, AddReminderActivity::class.java), 1) }

        val profileIntent: Intent = Intent()
        profileIntent.putExtra("radius", myRadius)

        profile.setOnClickListener { startActivityForResult(Intent(applicationContext, ProfileActivity::class.java), 3) }
        logout.setOnClickListener { finish() }
        track.setOnClickListener {
            startLocationUpdates()
        }
        virtualLocation.setOnClickListener {
            stopLocationUpdates()
            startActivityForResult(Intent(applicationContext, MapsActivity::class.java), 0)
        }

        updateGPS()
    } //END OF ONCREATE

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        updateGPS()
    }

    private fun updateGPS() {

        when {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
                    updateUIValues(location)
                }
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_FINE_LOCATION)
                }
            }
        }
    }

    private fun updateUIValues(location: Location) {
        val tvLat: TextView = findViewById(R.id.tvLatLng)
        myLatitude = location.latitude
        myLongitude = location.longitude

        tvLat.setText(String.format(
                Locale.getDefault(),
                "Lat %1.4f, Lng: %2.4f",
                myLatitude,
                myLongitude
        ))
        buildRV()
    }

    private fun buildRV() {
        val checkBox: CheckBox = findViewById(R.id.checkBox)

        val remindersList: ArrayList<Reminder> = dbHandler.getReminders(this, checkBox.isChecked, myLatitude, myLongitude, myRadius)
        val adapter = NotificationsAdapter(this, remindersList)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        buildRV()
        super.onResume()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_FINE_LOCATION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS()
            }
            else {
                Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show()
                finish()
            }

        }
    }

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
        }
        buildRV()
    }

    private fun addReminder(message: String, time: String, latitude: Double, longitude: Double): Long {
        val tbc: String = "not implemented yet"

        val newItem = Reminder(message, longitude, latitude, time, tbc, 0, false)
        return dbHandler.newReminder(this, newItem)
    }

    private fun workRequest(timeToReminder: Long, reminderId: Long, message: String, time: String, createNotification: Boolean) : OneTimeWorkRequest {
        val myRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeToReminder, TimeUnit.SECONDS)
            .setInputData(
                workDataOf("message" to message, "time" to time,
                            "myId" to reminderId, "createNotification" to createNotification)
            )
            .build()
        return myRequest
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val message: String = data?.getStringExtra("keymessage").toString()
                val time: String = data?.getStringExtra("keytime").toString()
                val timeToReminder = data!!.getLongExtra("difference", 0)
                val createNotification = data.getBooleanExtra("checked",true)
                val latitude = data.getDoubleExtra("latitude", 0.0)
                val longitude = data.getDoubleExtra("longitude", 0.0)

                val myId = addReminder(message, time, latitude, longitude)

                WorkManager.getInstance(applicationContext).enqueue(workRequest(timeToReminder, myId, message, time, createNotification))

                Handler().postDelayed(this::buildRV, 300)

                Toast.makeText(applicationContext, "Reminder added succesfully", Toast.LENGTH_SHORT).show()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, "Reminder creation canceled!", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                val latitude = data?.getDoubleExtra("latitude", 0.0)
                val longitude = data?.getDoubleExtra("longitude", 0.0)

                if (latitude != null) {
                    myLatitude = latitude
                }
                if (longitude != null) {
                    myLongitude = longitude
                }

                val tvLat: TextView = findViewById(R.id.tvLatLng)
                tvLat.setText(String.format(
                        Locale.getDefault(),
                        "Lat %1.4f, Lng: %2.4f",
                        myLatitude,
                        myLongitude
                ))
                buildRV()

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, "Setting location canceled!", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                val radius = data?.getIntExtra("radius", 400)

                if (radius != null) {
                    myRadius = radius
                }
                val tvRadius : TextView = findViewById(R.id.tvRadius)
                tvRadius.text = "Radius: $myRadius"
            }
        }
    }
}
