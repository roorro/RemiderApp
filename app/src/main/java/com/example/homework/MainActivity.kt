package com.example.homework

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var dbHandler: ReminderDBHandler
    }

    //private val list = generateList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler = ReminderDBHandler(this)
        buildRV()

        val add: Button = findViewById(R.id.btnAdd)
        val profile: Button = findViewById(R.id.btnProfile)
        val logout: Button = findViewById(R.id.btnLogout)

        add.setOnClickListener{ startActivityForResult(Intent(applicationContext, AddReminderActivity::class.java), 1) }

        profile.setOnClickListener { startActivity(Intent(applicationContext, ProfileActivity::class.java)) }
        logout.setOnClickListener { finish() }
    }

    private fun buildRV() {
        val checkBox: CheckBox = findViewById(R.id.checkBox)

        val remindersList: ArrayList<Reminder> = dbHandler.getReminders(this, checkBox.isChecked)
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

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
        }
        buildRV()
    }

    private fun addReminder(message: String, time: String): Long {
        val tbc: String = "not implemented yet"

        val newItem = Reminder(message, tbc, tbc, time, tbc, 0, false)
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

                val myId = addReminder(message, time)

                //NotificationHelper(applicationContext).createNotification("Reminder Added", "Reminder added")

                WorkManager.getInstance(applicationContext).enqueue(workRequest(timeToReminder, myId, message, time, createNotification))

                Toast.makeText(applicationContext, "Reminder added succesfully", Toast.LENGTH_SHORT).show()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, "Reminder creation canceled!", Toast.LENGTH_SHORT).show()
            }
        }
        /*if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val message: String = data?.getStringExtra("keymessage").toString()
                val time: String = data?.getStringExtra("keytime").toString()
                val position: Int = data!!.getIntExtra("position", 0)
                editReminder(position, message, time)
                Toast.makeText(applicationContext, "Reminder edited succesfully", Toast.LENGTH_SHORT).show()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, "Reminder editing canceled!", Toast.LENGTH_SHORT).show()
            }
        }*/
    }
}
