package com.example.quizzywizzy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Reference to RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.categoriesRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Define categories
        val categories = listOf(
            Category("Math", R.drawable.math),
            Category("Alphabet", R.drawable.alphabet),
            Category("Words", R.drawable.words),
            Category("Trivia", R.drawable.trivia)
        )

        // Set up adapter
        recyclerView.adapter = CategoriesAdapter(categories) { category ->
            // Handle category click and navigate to QuizActivity
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("CATEGORY", category.name) // Pass selected category to QuizActivity
            startActivity(intent)
        }
    }
}


