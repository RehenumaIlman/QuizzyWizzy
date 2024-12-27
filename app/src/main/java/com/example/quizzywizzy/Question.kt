package com.example.quizzywizzy

data class Question(
    val question: String = "",
    val options: List<String> = listOf(),
    val answer: String = ""
)
