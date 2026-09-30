package com.example.ch06.home

import com.example.ch06.data.Article

data class HomeUiState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val errorMessage: String? = null,
    val query: String = ""
) {
    // Properti turunan (getter), BUKAN field baru
    val isEmptyResult: Boolean
        get() = !isLoading && errorMessage == null && articles.isEmpty()
}