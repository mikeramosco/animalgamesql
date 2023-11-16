package com.example.animalgamesql

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_play_game.*

class PlayGameActivity : AppCompatActivity() {

    // Screen messages
    private val messageIfComputerWins = "Aren't I clever?\n\n^_^\n\nThanks for playing!"
    private val promptToGuessAnswerPrefix = "Were you thinking of (a/an) "
    private val updateDatabaseCodeNumber = 42

    // SQL values
    private lateinit var dbName : String

    // data to get from Yes & No Node API calls @ getIDsForYesAndNoNodesFromData()
    private var yesGraphID = 0
    private var noGraphID = 0
    private var yesChildID = 0
    private var noChildID = 0

    // data to save for later in case of answer & graph modifications
    private lateinit var guessedAnswer : String
    private var currentGraphID = 0
    private var currentChildID = 0

    // node values
    private val firstQuestionID = 1
    private var nodeTypeIsAnswer = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)
        dbName = intent.getStringExtra("dbName")!!
        getNextNodeData(firstQuestionID)
    }



    private fun getNextNodeData(nodeID : Int) {
        val db = openOrCreateDatabase(dbName, MODE_PRIVATE, null)
        val query = "SELECT type, text FROM nodes WHERE nodeid = '$nodeID'"
        val cursor = db.rawQuery(query, null)

        var type = ""
        var text = ""
        while(cursor.moveToNext()) {
            type = cursor.getString(cursor.getColumnIndex("type"))
            text = cursor.getString(cursor.getColumnIndex("text"))
        }
        cursor.close()

        if(type == "question")
            getYesAndNoIDs(nodeID)
        updateScreenMessage(type, text)
    }



    private fun getYesAndNoIDs(nodeID : Int) {

        val db = openOrCreateDatabase(dbName, MODE_PRIVATE, null)
        val query = "SELECT graphid, childid, type FROM graph WHERE parentid = '$nodeID'"
        val cursor = db.rawQuery(query, null)

        // grabs values from SQL database
        var graphID : Int
        var childID : Int
        var type : String
        while(cursor.moveToNext()) {
            graphID = cursor.getInt(cursor.getColumnIndex("graphid"))
            childID = cursor.getInt(cursor.getColumnIndex("childid"))
            type = cursor.getString(cursor.getColumnIndex("type"))

            // saves IDs for yes and no responses
            if(type == "yes") {
                yesGraphID = graphID
                yesChildID = childID
            } else if(type == "no") {
                noGraphID = graphID
                noChildID = childID
            }
        }
        cursor.close()
    }



    private fun updateScreenMessage(type : String, text : String) {
        if(type == "question") {
            question_text_view.text = text
        } else if(type == "answer") {
            val promptToGuessAnswer = "$promptToGuessAnswerPrefix$text?"
            question_text_view.text = promptToGuessAnswer

            nodeTypeIsAnswer = true
            guessedAnswer = text
        }

        // make buttons visible when data is ready
        yes_button.isEnabled = true
        no_button.isEnabled = true
    }



    // if "yes" was selected for an answer node, computer wins,
    // otherwise, code finds node using the IDs linked to "yes"
    // which are found @ getYesAndNoIDs
    fun yesSelected(view: View) {
        if(nodeTypeIsAnswer) {
            answerWasCorrect()
        } else {
            buttonWasSelectedForQuestion(yesGraphID, yesChildID)
        }
    }



    // displays computer wins message
    private fun answerWasCorrect() {
        // game is over so Yes & No buttons should be removed
        yes_button.isVisible = false
        no_button.isVisible = false

        question_text_view.text = messageIfComputerWins
    }



    // if "no" was selected for an answer node, human wins,
    // and will open UpdateTreeActivity to update with new answers,
    // otherwise, code finds node using the IDs linked to "no"
    // which are found @ getYesAndNoIDs
    fun noSelected(view: View) {
        if(nodeTypeIsAnswer) {
            answerWasIncorrect()
        } else {
            buttonWasSelectedForQuestion(noGraphID, noChildID)
        }
    }



    // passes variables when opening UpdateTreeActivity:
    //      - guessed answer
    //      - graph type
    //      - graph ID to modify
    //      - parent ID of graph
    //      - child ID of graph
    @SuppressLint("SetTextI18n")
    private fun answerWasIncorrect() {
        if(dbName == "animalgame") {
            question_text_view.text = "Darn! Okay, you win!\n\nT-T\n\nI'll guess your answer right next time though!"
            yes_button.isVisible = false
            no_button.isVisible = false
        } else {
            val openPage = Intent(this, UpdateDatabaseActivity::class.java)
            openPage.putExtra("dbName", dbName)
            openPage.putExtra("guessedAnswer", guessedAnswer)
            openPage.putExtra("graphIDtoModify", currentGraphID)
            openPage.putExtra("childIDofGraph", currentChildID)
            startActivityForResult(openPage, updateDatabaseCodeNumber)
        }
    }



    // when UpdateTreeActivity is closed, this activity automatically closes
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == updateDatabaseCodeNumber) {
            finish()
        }
    }



    // sets the current graph type, graph ID, and child ID to IDs associated with the answer "yes" or "no"
    private fun buttonWasSelectedForQuestion(graphIDofSelectedButton: Int, childIDofSelectedButton: Int) {

        // make buttons invisible when button is pressed to prepare data before next click
        yes_button.isEnabled = false
        no_button.isEnabled = false

        currentGraphID = graphIDofSelectedButton
        currentChildID = childIDofSelectedButton
        getNextNodeData(childIDofSelectedButton)
    }



    fun returnToHome(view: View) {
        finish()
    }
}
