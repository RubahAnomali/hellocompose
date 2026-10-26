package com.example.ch09.testutil

import com.example.ch09.data.local.ArticleDao
import com.example.ch09.data.local.ArticleEntity

class FakeArticleDao : ArticleDao() {

    private val rows =
        mutableMapOf<Pair<String, String>, ArticleEntity>()

    var cacheReads = 0
        private set

    override suspend fun getArticles(
        category: String
    ): List<ArticleEntity> {
        cacheReads++

        return rows.values
            .filter { it.category == category }
            .sortedBy { it.position }
    }

    override suspend fun getArticleByUrl(
        url: String
    ): ArticleEntity? {
        return rows.values
            .filter { it.url == url }
            .sortedWith(
                compareByDescending<ArticleEntity> { it.cachedAt }
                    .thenBy { it.category }
            )
            .firstOrNull()
    }

    override suspend fun searchCached(
        query: String
    ): List<ArticleEntity> {
        cacheReads++

        val pattern = query.map { char ->
            when (char) {
                '%' -> ".*"
                '_' -> "."
                else -> Regex.escape(char.toString())
            }
        }.joinToString("")

        val regex = Regex(
            pattern,
            setOf(
                RegexOption.IGNORE_CASE,
                RegexOption.DOT_MATCHES_ALL
            )
        )

        return rows.values
            .filter {
                regex.containsMatchIn(it.title) ||
                        regex.containsMatchIn(it.description)
            }
            .sortedWith(
                compareByDescending<ArticleEntity> { it.cachedAt }
                    .thenBy { it.position }
                    .thenBy { it.category }
            )
    }

    override suspend fun insertAll(
        articles: List<ArticleEntity>
    ) {
        articles.forEach {
            rows[it.category to it.url] = it
        }
    }

    override suspend fun deleteCategory(
        category: String
    ) {
        rows.keys
            .filter { it.first == category }
            .forEach { rows.remove(it) }
    }
}