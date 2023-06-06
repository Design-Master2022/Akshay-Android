package com.newsapp.newsapp.modal

data class Article(
    val author: String ?= null,
    val content: String ?= null,
    val description: String ?= null,
    val publishedAt: String ?= null,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String ?= null
) : java.io.Serializable