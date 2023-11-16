package com.example.animalgamesql

import android.annotation.SuppressLint
import android.content.Intent
import android.database.DatabaseUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_update_database.*

class UpdateDatabaseActivity : AppCompatActivity() {

    // Screen messages
    private val emptyTextFieldMessage = "You cannot leave the text field blank."
    private val databaseUpdatedMessage = "Thank you for your answers!! You've made me smarter!\n\nNext time we play, I'll win for sure! ;)"

    // Full prompt for new question will look like:
    // > Now give me a question that differentiates "<guessedAnswer>" from "<newAnswer>"
    private val newQuestionPromptPrefix = "Now give me a question that differentiates \""
    private val newQuestionPromptMiddle = "\" from \""
    private val newQuestionPromptSuffix = "\":"

    // Full prompt for Yes or No answer will look like:
    // > Lastly, how would you answer this question if your word was "<newAnswer>":
    // > <newQuestion>
    private val yesOrNoPromptPrefix = "Lastly, how would you answer this question if your word was \""
    private val yesOrNoPromptMiddle = "\":\n\n"

    // Database values
    private val nodesTable = "nodes"
    private val graphTable = "graph"

    // New data needed to update database
    private lateinit var guessedAnswer : String
    private lateinit var newAnswer : String
    private lateinit var newQuestion : String
    private var promptIsAskingForAnswer = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_database)
        guessedAnswer = intent.getStringExtra("guessedAnswer")!!
    }

    // when submit button is clicked, code checks to see which prompt was displayed:
    // either prompt asking for answer, or prompt asking for question for the new answer
    fun entrySubmitted(view: View) {
        val entry = text_field.text.toString()
        when {
            entry == "" -> Toast.makeText(this, emptyTextFieldMessage,
                Toast.LENGTH_LONG).show() // displays error message if entry submitted was empty
            promptIsAskingForAnswer -> newAnswerEntered(entry)
            else -> newQuestionEntered(entry)
        }
    }

    // if new answer was entered
    @SuppressLint("DefaultLocale")
    private fun newAnswerEntered(entry : String) {

        // store the new answer player has entered in the correct format
        newAnswer = entry
        newAnswer = newAnswer.toLowerCase()

        // switch bool so code @ entrySubmitted will know what prompt is being displayed
        promptIsAskingForAnswer = false

        // displays new prompt asking for a question for the new answer
        // Full prompt for new question will look like:
        // > Now give me a question that differentiates "<guessedAnswer>" from "<newAnswer>"
        val newQuestionPrompt = newQuestionPromptPrefix + guessedAnswer +
                newQuestionPromptMiddle + newAnswer + newQuestionPromptSuffix
        prompt_text_view.text = newQuestionPrompt
        text_field.setText("")
        text_field.hint = "Your question"
    }

    @SuppressLint("DefaultLocale")
    private fun newQuestionEntered(entry : String) {

        // store the new question player has entered in the correct format
        newQuestion = entry
        newQuestion = newQuestion.toLowerCase()
        newQuestion = newQuestion.capitalize()
        if(!newQuestion.contains("?")) newQuestion += "?"

        // display "Yes or No" prompt on screen
        // Full prompt for Yes or No answer will look like:
        // > Lastly, how would you answer this question if your word was "<newAnswer>":
        // > <newQuestion>
        val yesOrNoPrompt = yesOrNoPromptPrefix + newAnswer +
                yesOrNoPromptMiddle + newQuestion
        prompt_text_view.text = yesOrNoPrompt
        prompt_text_view.textSize = 18f

        // switch buttons' visibility so player can choose "Yes" or "No"
        text_field.isVisible = false
        submit_button.isVisible = false
        yes_button.isVisible = true
        no_button.isVisible = true
    }

    fun yesSelected(view: View) {
        updateDatabase("yes")
    }

    fun noSelected(view: View) {
        updateDatabase("no")
    }

    private fun updateDatabase(yesOrNo : String) {
        // update screen display
        yes_button.isVisible = false
        no_button.isVisible = false
        home_button.isEnabled = false

        /*

        updates database through cascading SQL queries:
            1. creates new answer node
            2. creates new question node
            3. modifies existing graph
            4. adds new graph for new answer
            5. adds new graph for guessed answer

         */

        val dbName = intent.getStringExtra("dbName")!!
        val db = openOrCreateDatabase(dbName, MODE_PRIVATE, null)
        val lastNodeID = DatabaseUtils.queryNumEntries(db, nodesTable).toInt()
        val lastGraphID = 100 + DatabaseUtils.queryNumEntries(db, graphTable).toInt()

        // 1. Adds new answer node to database
        val newAnswerNodeID = lastNodeID + 1
        var query = "INSERT INTO nodes VALUES ($newAnswerNodeID, 'answer', '$newAnswer', 0, '0000-00-00 00:00:00', 0);"
        db.execSQL(query)

        // 2. Adds new question node to database
        val newQuestionNodeID = newAnswerNodeID + 1
        query = "INSERT INTO nodes VALUES ($newQuestionNodeID, 'question', '$newQuestion', 0, '0000-00-00 00:00:00', 0);"
        db.execSQL(query)

        // 3. Modifies graph node to point to new question instead of originally guessed answer
        val graphIDtoModify = intent.getIntExtra("graphIDtoModify", 0)
        query = "UPDATE graph SET childid = $newQuestionNodeID WHERE graphid = $graphIDtoModify"
        db.execSQL(query)

        // 4. Adds new graph node for the new answer
        val newAnswerGraphID = lastGraphID + 1
        query = "INSERT INTO graph VALUES ($newAnswerGraphID, $newQuestionNodeID, $newAnswerNodeID, '$yesOrNo');"
        db.execSQL(query)

        // 5. Adds new graph node for the originally guessed answer
        val guessedAnswerNodeID = intent.getIntExtra("childIDofGraph", 0)
        val guessedAnswerGraphID = newAnswerGraphID + 1
        var guessedAnswerYesOrNo = "yes"
        if(yesOrNo == "yes") guessedAnswerYesOrNo = "no"
        query = "INSERT INTO graph VALUES ($guessedAnswerGraphID, $newQuestionNodeID, $guessedAnswerNodeID, '$guessedAnswerYesOrNo');"
        db.execSQL(query)

        // re-update screen display when database has been updated
        prompt_text_view.text = databaseUpdatedMessage
        home_button.isEnabled = true
    }

    fun returnToHome(view: View) {
        val data = Intent()
        setResult(RESULT_OK, data)
        finish()
    }
}
