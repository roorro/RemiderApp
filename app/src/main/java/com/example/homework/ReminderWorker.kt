package com.example.homework

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

open class ReminderWorker(val context: Context, val params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        val createNotification: Boolean = inputData.getBoolean("createNotification", true)

        if (createNotification) {
            NotificationHelper(context).createNotification(
                inputData.getString("message").toString(),
                inputData.getString("time").toString()
            )
        }

        val test = inputData.getLong("myId", 0)

        MainActivity.dbHandler.changeVisibility(test.toString())


        return Result.success()
    }
}