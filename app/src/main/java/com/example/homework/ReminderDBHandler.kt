package com.example.homework

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import android.util.Log
import android.widget.Toast

class ReminderDBHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyReminder17.db"
        private const val DATABASE_VERSION = 1
        private const val KEY_ID = "_id"

        private const val TABLE_NAME = "reminderList"
        private const val COLUMN_MESSAGE = "message"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_VISIBILITY = "visibility"
        private const val COLUMN_LONGITUDE = "longitude"
        private const val COLUMN_LATITUDE = "latitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_REMINDERS_TABLE: String = ("CREATE TABLE " +
                TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_VISIBILITY + " INTEGER DEFAULT 0, " +
                COLUMN_LATITUDE + " DECIMAL DEFAULT 0.0, " +
                COLUMN_LONGITUDE + " DECIMAL DEFAULT 0.0" +
                ")")
        db?.execSQL(CREATE_REMINDERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun newReminder(context: Context, reminder: Reminder): Long {
        var row: Long = 0
        val cv = ContentValues()
        cv.put(COLUMN_MESSAGE, reminder.message)
        cv.put(COLUMN_TIME, reminder.reminder_time)
        cv.put(COLUMN_LATITUDE, reminder.location_y)
        cv.put(COLUMN_LONGITUDE, reminder.location_x)
        //val query = "Select * From $TABLE_NAME"
        val db: SQLiteDatabase = this.writableDatabase
        //val cursor: Cursor = db.rawQuery(query, null)
        try {
            row = db.insert(TABLE_NAME, null, cv)
            Log.d("rivi", "$row")
            Toast.makeText(context, "Reminder Added", Toast.LENGTH_SHORT).show()
            return row
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        db.close()
        return row
    }

    fun getReminders(context: Context, showAll: Boolean, curLatitude: Double, curLongitude: Double, myRadius: Int) : ArrayList<Reminder> {
        val query = "Select * From $TABLE_NAME"
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)
        val reminders = ArrayList<Reminder>()

        if (cursor.count == 0) {
            Toast.makeText(context, "No Records Found", Toast.LENGTH_SHORT).show()}
        else {
            while (cursor.moveToNext()) {
                val visible = cursor.getInt(cursor.getColumnIndex(COLUMN_VISIBILITY))
                if ((visible == 0) and !showAll) {
                    continue
                }
                val latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE))
                val longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE))
                val results = FloatArray(1)

                Location.distanceBetween(curLatitude, curLongitude, latitude, longitude, results)
                if ((latitude == 0.0) and (longitude == 0.0)) {
                    //do nothing
                }
                else if ((results[0] > myRadius) and !showAll) {
                    continue
                }

                val reminder = Reminder("", 0.0, 0.0, "", "", 0, false)
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