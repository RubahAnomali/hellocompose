package com.example.ch10.di

import com.example.ch10.data.NewsRepository
import com.example.ch10.data.model.Article
import com.example.ch10.data.model.NewsCategory
import com.example.ch10.data.model.NewsFeed
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeNewsRepository @Inject constructor() : NewsRepository {

    var headlines: Result<NewsFeed> = Result.success(
        NewsFeed(
            listOf(
                fakeArticle(1),
                fakeArticle(2)
            )
        )
    )

    override suspend fun getTopHeadlines(
        category: NewsCategory
    ): Result<NewsFeed> = headlines

    override suspend fun searchNews(
        query: String
    ): Result<NewsFeed> =
        Result.success(
            NewsFeed(
                listOf(
                    fakeArticle(
                        id = 9,
                        title = "Hasil untuk $query"
                    )
                )
            )
        )

    override suspend fun getArticle(url: String): Article? =
        headlines.getOrNull()
            ?.articles
            .orEmpty()
            .firstOrNull { it.url == url }
}

fun fakeArticle(
    id: Int,
    title: String = "Berita Uji $id"
): Article = Article(
    title = title,
    author = "Penulis Uji",
    description = "Deskripsi berita uji $id",
    content = "",
    url = "https://contoh.com/$id",
    imageUrl = null,
    publishedAt = "2025-05-01T08:00:00Z",
    sourceName = "Sumber Uji"
)