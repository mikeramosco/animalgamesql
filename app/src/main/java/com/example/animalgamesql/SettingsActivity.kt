package com.example.animalgamesql

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }


    fun createSQL(view: View) {
        // TODO: create/load SQL database
    }

    fun printSQL(view: View) {
        // TODO: print SQL database
    }

    fun deleteSQL(view: View) {
        // TODO: delete/reset SQL database
    }

    fun createFirebase(view: View) {
        // TODO: create/load Firebase database
    }

    fun deleteFirebase(view: View) {
        // TODO: delete/reset Firebase database
    }
}
