package com.example.quizzywizzy

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuizActivity : AppCompatActivity() {

    private lateinit var selectedCategory: String
    private val questionsList = mutableListOf<Question>() // List to store questions
    private var currentQuestionIndex = 0 // Index to track the current question
    private var score = 0 // Variable to track the score

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Get the selected category
        selectedCategory = intent.getStringExtra("CATEGORY") ?: "math"
        selectedCategory = selectedCategory.lowercase()
        Log.d("selectedCategory", "onCreate: ${selectedCategory.lowercase()}")

        // Reference to Firebase Database
        val database = FirebaseDatabase.getInstance()
        val quizRef = database.getReference("quiz_categories/$selectedCategory")

        // Fetch questions from Firebase
        quizRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                questionsList.clear() // Clear the list to avoid duplicates
                for (questionSnapshot in snapshot.children) {
                    val question = questionSnapshot.getValue(Question::class.java)
                    question?.let { questionsList.add(it) }
                }
                // Load the first question
                loadQuestion(currentQuestionIndex)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@QuizActivity,
                    "Failed to load questions: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun loadQuestion(index: Int) {
        if (index < questionsList.size) {
            val currentQuestion = questionsList[index]

            // Display the question and options
            findViewById<TextView>(R.id.questionText).text = currentQuestion.question
            findViewById<Button>(R.id.option1Button).text = currentQuestion.options[0]
            findViewById<Button>(R.id.option2Button).text = currentQuestion.options[1]
            findViewById<Button>(R.id.option3Button).text = currentQuestion.options[2]
            findViewById<Button>(R.id.option4Button).text = currentQuestion.options[3]

            // Set click listeners for the options
            findViewById<Button>(R.id.option1Button).setOnClickListener { checkAnswer(currentQuestion, 0) }
            findViewById<Button>(R.id.option2Button).setOnClickListener { checkAnswer(currentQuestion, 1) }
            findViewById<Button>(R.id.option3Button).setOnClickListener { checkAnswer(currentQuestion, 2) }
            findViewById<Button>(R.id.option4Button).setOnClickListener { checkAnswer(currentQuestion, 3) }
        } else {
            // No more questions, show the result
            showResult()
        }
    }

    private fun checkAnswer(currentQuestion: Question, selectedOptionIndex: Int) {
        if (currentQuestion.options[selectedOptionIndex] == currentQuestion.answer) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wrong Answer!", Toast.LENGTH_SHORT).show()
        }

        // Load the next question
        currentQuestionIndex++
        loadQuestion(currentQuestionIndex)
    }

    private fun showResult() {
        Toast.makeText(
            this,
            "Quiz Over! Your Score: $score/${questionsList.size}",
            Toast.LENGTH_LONG
        ).show()
        finish()
    }
}



