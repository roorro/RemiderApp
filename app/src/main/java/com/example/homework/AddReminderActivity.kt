package com.example.homework

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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

        val checkbox: CheckBox = findViewById(R.id.cbNotification)

        val posIntent: Intent = intent

        val myIntent = Intent()

        val position: Int = posIntent.getIntExtra("position", 0)

        buttonSetTime.setOnClickListener{
            setAlarm(buttonSetTime, myIntent)
        }

        buttonAdd.setOnClickListener{

            val message: String = title.text.toString()
            val clock: String = buttonSetTime.text.toString()

            myIntent.putExtra("keymessage", message)
            myIntent.putExtra("keytime", clock)
            myIntent.putExtra("position", position)
            myIntent.putExtra("checked", checkbox.isChecked)


            setResult(Activity.RESULT_OK, myIntent)
            finish()
        }
    }

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
        }
    }

    private fun setAlarm(text: Button, intent: Intent) {
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

                            val c = Calendar.getInstance()
                            c.set(year, month, day, hour, min, 0)

                            val now = Calendar.getInstance()

                            var timeDifference = (c.timeInMillis/1000L) - (now.timeInMillis/1000L)

                            if (timeDifference < 0) {
                                timeDifference = 0
                            }

                            intent.putExtra("difference", timeDifference)

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