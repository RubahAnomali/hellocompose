package com.example.ch07.util

// now dan then dalam milidetik. Bulan = 30 hari, tahun = 365 hari.
fun formatRelativeTime(now: Long, then: Long): String {
    val seconds = ((now - then).coerceAtLeast(0L)) / 1_000L
    return when {
        seconds < 60L -> "baru saja"
        seconds < 3_600L -> "${seconds / 60L} menit lalu"
        seconds < 86_400L -> "${seconds / 3_600L} jam lalu"
        seconds < 2_592_000L -> "${seconds / 86_400L} hari lalu"
        seconds < 31_536_000L -> "${seconds / 2_592_000L} bulan lalu"
        else -> "${seconds / 31_536_000L} tahun lalu"
    }
}
