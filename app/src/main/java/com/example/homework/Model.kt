package com.example.homework

import android.app.Notification

data class Reminder(var message: String, val location_x: String, val location_y: String, var reminder_time: String, val creation_time: String, var creator_id: Int, val reminder_seen: Boolean)

