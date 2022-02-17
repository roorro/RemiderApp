package com.example.homework

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class ReminderDBHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyReminder9.db"
        private const val DATABASE_VERSION = 1
        private const val KEY_ID = "_id"

        private const val TABLE_NAME = "reminderList"
        private const val COLUMN_MESSAGE = "message"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_VISIBILITY = "visibility"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_REMINDERS_TABLE: String = ("CREATE TABLE " +
                TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_VISIBILITY + " INTEGER DEFAULT 0" +
                ")")
        db?.execSQL(CREATE_REMINDERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun newReminder(context: Context, reminder: Reminder): Long {
        val row: Long = 0
        val cv = ContentValues()
        cv.put(COLUMN_MESSAGE, reminder.message)
        cv.put(COLUMN_TIME, reminder.reminder_time)
        //val query = "Select * From $TABLE_NAME"
        val db: SQLiteDatabase = this.writableDatabase
        //val cursor: Cursor = db.rawQuery(query, null)
        try {
            val row = db.insert(TABLE_NAME, null, cv)
            Log.d("rivi", "$row")
            Toast.makeText(context, "Reminder Added", Toast.LENGTH_SHORT).show()
            return row
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        db.close()
        return row
    }

    fun getReminders(context: Context, showAll: Boolean) : ArrayList<Reminder> {
        val query = "Select * From $TABLE_NAME"
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)
        val reminders = ArrayList<Reminder>()

        if (cursor.count == 0) {
            Toast.makeText(context, "No Records Found", Toast.LENGTH_SHORT).show()}
        else {
            while (cursor.moveToNext()) {
                val reminder = Reminder("", "", "", "", "", 0, false)
                val visible = cursor.getInt(cursor.getColumnIndex(COLUMN_VISIBILITY))
                if ((visible == 0) and !showAll) {
                    continue
                }
                reminder.creator_id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                reminder.message = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE))
                reminder.reminder_time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME))
                //Log.d("idd", "${reminder.creator_id}")

                Log.d("visibility", "$visible")
                /*if (visible != 0) {
                    reminder.reminder_seen = true
                }*/
                reminders.add(reminder)
            }
        }
        cursor.close()
        db.close()
        return reminders
    }

    fun deleteReminder(id: Int) : Boolean {
        val query = "Delete From $TABLE_NAME where $KEY_ID = $id"
        val db: SQLiteDatabase = this.writableDatabase
        var result: Boolean = false
        try {
            val cursor: Unit = db.execSQL(query)
            result = true
        } catch (e: java.lang.Exception) {
            Log.e(ContentValues.TAG, "Error deleting")
        }
        db.close()
        return result
    }


    fun updateReminder(id : String, reminderMsg: String, reminderTime: String) : Boolean {
        val db : SQLiteDatabase = this.writableDatabase
        val cv = ContentValues()
        var result : Boolean = false
        cv.put(COLUMN_MESSAGE, reminderMsg)
        cv.put(COLUMN_TIME, reminderTime)

        try{
            db.update(TABLE_NAME, cv, "$KEY_ID = ?", arrayOf(id))
            result = true
        } catch (e: Exception) {
            result = false
        }
        return result
    }

    fun changeVisibility(id : String): Boolean {
        val db : SQLiteDatabase = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_VISIBILITY, 1)

        try{
            db.update(TABLE_NAME, cv, "$KEY_ID = ?", arrayOf(id))
            return true
        } catch (e: Exception) {
            return false
        }
    }
}