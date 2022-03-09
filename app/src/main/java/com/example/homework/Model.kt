package com.example.homework

import android.app.Notification

data class Reminder(var message: String, var location_x: Double, var location_y: Double,
                    var reminder_time: String, val creation_time: String, var creator_id: Int,
                    var reminder_seen: Boolean)
