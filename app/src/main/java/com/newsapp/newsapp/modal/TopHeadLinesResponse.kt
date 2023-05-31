package com.newsapp.newsapp.modal

data class TopHeadLinesResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)