package com.example.ch06.di

import com.example.ch06.data.ArticleRepository
import com.example.ch06.data.FakeArticleRepository
import com.example.ch06.profile.FakeProfileRepository
import com.example.ch06.profile.ProfileRepository

interface AppContainer {
    val articleRepository: ArticleRepository
    // T4.6: Menambahkan profileRepository ke dalam interface
    val profileRepository: ProfileRepository
}

class DefaultAppContainer : AppContainer {
    override val articleRepository: ArticleRepository by lazy {
        FakeArticleRepository()
    }

    // T4.6: Memberikan implementasi FakeProfileRepository
    override val profileRepository: ProfileRepository by lazy {
        FakeProfileRepository()
    }
}