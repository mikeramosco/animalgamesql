package com.example.animalgamesql

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import java.util.*

/* Imports database from SQL if not already done once before */
fun Activity.importDatabaseIfNecessary(dbName: String, table: String) {
    val db = openOrCreateDatabase(dbName, MODE_PRIVATE, null)
    if(!tableExists(db, table)) {
        importDatabase(dbName)
    }
}

/* Checks if a table exists in an SQLiteDatabase */
fun tableExists(db: SQLiteDatabase, table: String): Boolean {
    val sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='$table'"
    val mCursor = db.rawQuery(sql, null)
    if (mCursor.count > 0) {
        mCursor.close()
        return true
    }
    mCursor.close()
    return false
}

/* Reads from a .sql file and executes its SQL statements. */
fun Activity.importDatabase(dbName: String) {
    val db = openOrCreateDatabase(dbName, MODE_PRIVATE, null)
    val resId = resources.getIdentifier(dbName, "raw", packageName)
    val scan = Scanner(resources.openRawResource(resId))

    // build and execute queries
    var query = ""
    while (scan.hasNextLine()) {
        val line = scan.nextLine()
        if (line.trim().startsWith("--")) continue   // strip comments
        query += "$line\n"
        if (query.trim().endsWith(";")) {
            db.execSQL(query)
            query = ""
        }
    }
}