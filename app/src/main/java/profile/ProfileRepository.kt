package com.example.ch06.profile

data class UserProfile(
    val username: String,
    val notificationsEnabled: Boolean
)

// Sinkron, tanpa suspend, karena datanya lokal
interface ProfileRepository {
    fun getProfile(): UserProfile
}

class FakeProfileRepository : ProfileRepository {
    override fun getProfile(): UserProfile = UserProfile(
        username = "Mahasiswa Android",
        notificationsEnabled = true
    )
}