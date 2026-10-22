package com.example.ch10.ui.news

import com.example.ch10.data.NewsRepository
import com.example.ch10.data.model.NewsCategory
import com.example.ch10.data.model.NewsFeed
import com.example.ch10.testutil.MainDispatcherRule
import com.example.ch10.testutil.article
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelMockKTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private lateinit var repository: NewsRepository

    @Before
    fun setUp() {
        repository = mockk()
    }

    @Test
    fun `sukses - headline mengisi daftar artikel`() = runTest {
        val articles = listOf(
            article(1),
            article(2)
        )

        coEvery {
            repository.getTopHeadlines(any())
        } returns Result.success(
            NewsFeed(articles = articles)
        )

        val viewModel = NewsViewModel(repository)

        runCurrent()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
        assertEquals(2, state.articles.size)
        assertEquals(articles, state.articles)
        assertNull(state.errorMessage)
        assertNull(state.offline)

        coVerify(exactly = 1) {
            repository.getTopHeadlines(NewsCategory.General)
        }

        coVerify(exactly = 1) {
            repository.getTopHeadlines(any())
        }
    }

    @Test
    fun `gagal - pesan error tampil dan daftar kosong`() = runTest {
        coEvery {
            repository.getTopHeadlines(any())
        } returns Result.failure(IOException())

        val viewModel = NewsViewModel(repository)

        runCurrent()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
        assertTrue(state.articles.isEmpty())
        assertEquals(
            "Tidak ada koneksi internet.",
            state.errorMessage
        )
        assertNull(state.offline)

        coVerify(exactly = 1) {
            repository.getTopHeadlines(NewsCategory.General)
        }

        coVerify(exactly = 1) {
            repository.getTopHeadlines(any())
        }
    }
}