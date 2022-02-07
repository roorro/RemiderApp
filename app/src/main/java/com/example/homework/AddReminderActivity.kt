package com.example.homework

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class AddReminderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        val title: EditText = findViewById(R.id.etMessage)
        val buttonAdd: Button = findViewById(R.id.btnReminder)
        val buttonSetTime: Button = findViewById(R.id.btnSetTime)
        val timeText: TextView = findViewById(R.id.tvSetTime)

        val posIntent: Intent = intent

        val position: Int = posIntent.getIntExtra("position", 0)

        buttonSetTime.setOnClickListener{
            setAlarm(timeText)
        }

        buttonAdd.setOnClickListener{

            val message: String = title.text.toString()
            val clock: String = timeText.text.toString()

            val intent: Intent = Intent()
            intent.putExtra("keymessage", message)
            intent.putExtra("keytime", clock)
            intent.putExtra("position", position)

            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun setAlarm(text: TextView) {
        Calendar.getInstance().apply {

            DatePickerDialog(
                this@AddReminderActivity,
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)

                    TimePickerDialog(
                        this@AddReminderActivity,
                        0,
                        { _, hour, min ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, min)
                            this.set(Calendar.SECOND, 0)

                            val formatter: SimpleDateFormat = SimpleDateFormat("HH:mm_dd/MM")

                            val myTime: String = formatter.format(this.time)

                            text.setText(myTime)

                            Toast.makeText(applicationContext, myTime, Toast.LENGTH_SHORT).show()
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        true
                    ).show()

                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}