package co.iamartem.visitbucketlist

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Double.parseDouble
import java.lang.Float.parseFloat
import kotlin.collections.ArrayList

/**
 * Created by dukhnia on 5/09/18.
 */

class MyDBHandler(context: Context, company: String?,
                  factory: SQLiteDatabase.CursorFactory?, version: Int) :
        SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_LOCS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOCS")
        onCreate(db)
    }

    fun addLocation(location: Location) {
        val values = ContentValues()

        values.put(COLUMN_LAT, location.latitude)
        values.put(COLUMN_LON, location.longitude)
        values.put(COLUMN_NAME, location.name)
        values.put(COLUMN_TYPE, location.type)

        val db = this.writableDatabase

        db.insert(TABLE_LOCS, null, values)
        db.close()
        Log.v("Tag", " Record Inserted Sucessfully")
    }

    fun updateLocation(location: Location) {
        val query = ("UPDATE $TABLE_LOCS " +
                " SET $COLUMN_NAME = ${location.name} " +
                " SET $COLUMN_LAT = ${location.latitude} " +
                " SET ${COLUMN_LON} = ${location.longitude} " +
                " SET $COLUMN_TYPE = ${location.type} " +
                " WHERE " +
                "   $COLUMN_ID = ${location.id} ")

        val db = this.writableDatabase

        db.rawQuery(query, null)
        //db.insert(TABLE_LOCS, null, values)
        db.close()
        Log.v("Tag", " Record Inserted Sucessfully")
    }


    fun getAllPaidLocations(): List<Location> {
        val db = this.writableDatabase
        val list = ArrayList<Location>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_LOCS", null)

        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val locID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                    val locLat = parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_LAT)))
                    val locLon = parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_LON)))
                    val locName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                    val locType = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE))
                    val location = Location(locID, locLat, locLon, locName, locType)
                    list.add(location)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return list
    }

    fun findLocation(id : String)  : Location? {
        Log.i("Tag","-------------------          -- - - ===++++ $id")
        val query = (" Select * FROM $TABLE_LOCS WHERE $id = $COLUMN_ID ")

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)
        var location: Location? = null

        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            val locID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val locLat = parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_LAT)))
            val locLon = parseFloat(cursor.getString(cursor.getColumnIndex(COLUMN_LON)))
            val locName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
            val locType = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE))

            location = Location(locID, locLat, locLon, locName, locType)
        }

        this.writableDatabase.close()
        return location
    }

    fun deleteLocation(_id : Int): Boolean {
        var result = false
        val query = ("Select * FROM " + TABLE_LOCS + " WHERE "
                + COLUMN_ID + " =  \"" + _id + "\"")
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            db.delete(TABLE_LOCS, COLUMN_ID + " = ?",
                    arrayOf(id.toString()))
            cursor.close()
            result = true
        }
        db.close()
        return result
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "locationsDB.db"
        val TABLE_LOCS = "locations"

        val COLUMN_ID = "_id"
        val COLUMN_LAT = "db_lat"
        val COLUMN_LON = "db_lon"
        val COLUMN_NAME = "db_name"
        val COLUMN_TYPE = "db_type"


        // Create
        val CREATE_LOCS_TABLE = ("CREATE TABLE "
                + TABLE_LOCS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LAT + " FLOAT, "
                + COLUMN_LON + " FLOAT, "
                + COLUMN_NAME + " STRING, "
                + COLUMN_TYPE + " STRING, ")
    }

}
