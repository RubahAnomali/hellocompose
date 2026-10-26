package com.example.ch09.data

import com.example.ch09.data.mapper.toDomain
import com.example.ch09.data.model.NewsCategory
import com.example.ch09.testutil.FakeArticleDao
import com.example.ch09.testutil.FakeNewsApiService
import com.example.ch09.testutil.articleDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class NewsRepositoryImplTest {

    private val api = FakeNewsApiService()
    private val dao = FakeArticleDao()
    private var now = 1_000L

    private val repository = NewsRepositoryImpl(
        api = api,
        dao = dao,
        clock = { now }
    )

    @Test
    fun `online menyimpan artikel valid unik dan urut ke cache`() = runTest {
        api.articles = listOf(
            articleDto(2),
            articleDto(1),
            articleDto(2),
            articleDto(3, title = "[Removed]")
        )

        val feed = repository
            .getTopHeadlines(NewsCategory.General)
            .getOrThrow()

        val stored = dao.getArticles("general")

        assertEquals(
            listOf(
                "https://contoh.com/2",
                "https://contoh.com/1"
            ),
            feed.articles.map { it.url }
        )

        assertEquals(
            listOf(0, 1),
            stored.map { it.position }
        )

        assertTrue(stored.all { it.cachedAt == now })
        assertNull(feed.offline)
    }

    @Test
    fun `gagal jaringan mengembalikan cache dengan alasan dan waktu`() = runTest {
        api.articles = listOf(articleDto(1))

        repository
            .getTopHeadlines(NewsCategory.General)
            .getOrThrow()

        now = 9_000L
        api.error = IOException()

        val feed = repository
            .getTopHeadlines(NewsCategory.General)
            .getOrThrow()

        assertEquals(1, feed.articles.size)
        assertEquals(1_000L, feed.offline!!.cachedAt)

        assertEquals(
            "Tidak ada koneksi internet.",
            feed.offline!!.reason
        )
    }

    @Test
    fun `gagal tanpa cache menghasilkan failure`() = runTest {
        val error = IOException()
        api.error = error

        val result = repository.getTopHeadlines(
            NewsCategory.General
        )

        assertSame(error, result.exceptionOrNull())
    }

    @Test
    fun `kategori baru tidak memakai cache kategori lain`() = runTest {
        api.articles = listOf(articleDto(1))

        repository
            .getTopHeadlines(NewsCategory.General)
            .getOrThrow()

        api.error = IOException()

        assertTrue(
            repository.getTopHeadlines(
                NewsCategory.Sports
            ).isFailure
        )
    }

    @Test
    fun `replace hanya mengganti kategori target`() = runTest {
        api.articles = listOf(articleDto(1))

        repository
            .getTopHeadlines(NewsCategory.General)
            .getOrThrow()

        repository
            .getTopHeadlines(NewsCategory.Technology)
            .getOrThrow()

        api.articles = listOf(articleDto(2))

        repository
            .getTopHeadlines(NewsCategory.General)
            .getOrThrow()

        assertEquals(
            "https://contoh.com/2",
            dao.getArticles("general").single().url
        )

        assertEquals(
            "https://contoh.com/1",
            dao.getArticles("technology").single().url
        )
    }

    @Test
    fun `respons kosong sukses membersihkan kategori`() = runTest {
        api.articles = listOf(articleDto(1))

        repository
            .getTopHeadlines(NewsCategory.General)
            .getOrThrow()

        api.articles = emptyList()

        val feed = repository
            .getTopHeadlines(NewsCategory.General)
            .getOrThrow()

        assertTrue(feed.articles.isEmpty())
        assertNull(feed.offline)
        assertTrue(dao.getArticles("general").isEmpty())
    }

    @Test
    fun `hasil pencarian disimpan dan detail dibaca dari dao`() = runTest {
        val dto = articleDto(
            id = 7,
            title = "Kotlin baru"
        )

        api.articles = listOf(dto)
        repository.searchNews(" Kotlin ").getOrThrow()

        assertEquals(
            1,
            dao.getArticles("__search__").size
        )

        val newRepository = NewsRepositoryImpl(api, dao)

        assertEquals(
            dto.toDomain(),
            newRepository.getArticle("https://contoh.com/7")
        )

        assertNull(
            newRepository.getArticle("https://tidak.ada")
        )
    }

    @Test
    fun `pencarian offline lintas kategori tanpa url ganda`() = runTest {
        api.articles = listOf(
            articleDto(1, title = "Kotlin Android")
        )

        repository
            .getTopHeadlines(NewsCategory.General)
            .getOrThrow()

        now = 2_000L

        repository
            .getTopHeadlines(NewsCategory.Technology)
            .getOrThrow()

        api.articles = listOf(
            articleDto(2, title = "Topik lain").copy(
                description = "Belajar Kotlin"
            )
        )

        repository.searchNews("Kotlin").getOrThrow()
        api.error = IOException()

        val feed = repository.searchNews("kotlin").getOrThrow()

        assertEquals(
            setOf(
                "https://contoh.com/1",
                "https://contoh.com/2"
            ),
            feed.articles.map { it.url }.toSet()
        )

        assertEquals(2, feed.articles.size)
        assertNotNull(feed.offline)

        assertTrue(
            repository.searchNews("tidak cocok").isFailure
        )
    }

    @Test
    fun `cancellation dilempar ulang tanpa membaca cache`() = runTest {
        api.articles = listOf(articleDto(1))

        repository
            .getTopHeadlines(NewsCategory.General)
            .getOrThrow()

        val cancelled = CancellationException("Pindah kategori")
        api.error = cancelled

        try {
            repository.getTopHeadlines(NewsCategory.General)
            fail("CancellationException harus dilempar ulang")
        } catch (actual: CancellationException) {
            assertSame(cancelled, actual)
        }

        assertEquals(0, dao.cacheReads)
    }
}