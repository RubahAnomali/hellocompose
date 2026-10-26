package com.example.ch09.data

import com.example.ch09.data.local.ArticleDao
import com.example.ch09.data.local.ArticleEntity
import com.example.ch09.data.mapper.toDomain
import com.example.ch09.data.mapper.toEntity
import com.example.ch09.data.model.Article
import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.model.NewsFeed
import com.example.ch09.data.model.OfflineInfo
import com.example.ch09.data.remote.NewsApiService
import com.example.ch09.data.remote.dto.NewsResponseDto
import kotlin.coroutines.cancellation.CancellationException

interface NewsRepository {
    suspend fun getTopHeadlines(
        category: NewsCategory
    ): Result<NewsFeed>

    suspend fun searchNews(
        query: String
    ): Result<NewsFeed>

    suspend fun getArticle(
        url: String
    ): Article?
}

class NewsRepositoryImpl(
    private val api: NewsApiService,
    private val dao: ArticleDao,
    private val clock: () -> Long = System::currentTimeMillis
) : NewsRepository {

    override suspend fun getTopHeadlines(
        category: NewsCategory
    ): Result<NewsFeed> {
        return fetch(
            cacheKey = category.apiValue,
            readCache = {
                dao.getArticles(category.apiValue)
            },
            remote = {
                api.getTopHeadlines(category = category.apiValue)
            }
        )
    }

    override suspend fun searchNews(
        query: String
    ): Result<NewsFeed> {
        val keyword = query.trim()

        return fetch(
            cacheKey = "__search__",
            readCache = {
                dao.searchCached(keyword)
            },
            remote = {
                api.searchNews(query = keyword)
            }
        )
    }

    override suspend fun getArticle(url: String): Article? {
        return dao.getArticleByUrl(url)?.toDomain()
    }

    private suspend fun fetch(
        cacheKey: String,
        readCache: suspend () -> List<ArticleEntity>,
        remote: suspend () -> NewsResponseDto
    ): Result<NewsFeed> {
        return try {
            val articles = remote().articles
                .mapNotNull { it.toDomain() }
                .distinctBy { it.url }

            val cachedAt = clock()

            val entities = articles.mapIndexed { index, article ->
                article.toEntity(
                    category = cacheKey,
                    position = index,
                    cachedAt = cachedAt
                )
            }

            dao.replaceCategory(
                category = cacheKey,
                articles = entities
            )

            Result.success(
                NewsFeed(articles = articles)
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            try {
                val cached = readCache().distinctBy { it.url }

                if (cached.isEmpty()) {
                    Result.failure(error)
                } else {
                    Result.success(
                        NewsFeed(
                            articles = cached.map { it.toDomain() },
                            offline = OfflineInfo(
                                cachedAt = cached.minOf { it.cachedAt },
                                reason = error.toUserMessage()
                            )
                        )
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (cacheError: Exception) {
                Result.failure(cacheError)
            }
        }
    }
}