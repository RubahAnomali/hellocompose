package com.example.ch09.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch09.data.NewsRepository
import com.example.ch09.data.model.Article
import com.example.ch09.data.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val article: Article? = null,
    val errorMessage: String? = null
)

class ArticleDetailViewModel(
    private val repository: NewsRepository,
    private val articleUrl: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ArticleDetailUiState()
    )

    val uiState: StateFlow<ArticleDetailUiState> =
        _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val article = repository.getArticle(articleUrl)

                _uiState.value = ArticleDetailUiState(
                    isLoading = false,
                    article = article,
                    errorMessage = if (article == null) {
                        "Artikel tidak ditemukan"
                    } else {
                        null
                    }
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.value = ArticleDetailUiState(
                    isLoading = false,
                    errorMessage = error.toUserMessage()
                )
            }
        }
    }
}