package com.example.animalgamesql

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View



class MainActivity : AppCompatActivity() {

    private val dbName = "animalgame"
//    private val dbName = "animalgame_small"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // imports database from SQL file if not already done once before
        importDatabaseIfNecessary(dbName, "nodes")
    }

    fun playGame(view: View) {
        val openPage = Intent(this, PlayGameActivity::class.java)
        openPage.putExtra("dbName", dbName)
        startActivity(openPage)
    }

    fun openSettings(view: View) {
        val openPage = Intent(this, SettingsActivity::class.java)
        startActivity(openPage)
    }
}
