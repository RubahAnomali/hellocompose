package com.example.ch07.util

import java.util.Locale

// Pisahkan dengan koma; buang # di awal, spasi tepi, dan duplikat.
fun parseTags(text: String): List<String> = text.split(',')
    .map { it.trim().trimStart('#').trim().lowercase(Locale.ROOT) }
    .filter { it.isNotEmpty() }
    .distinct()

fun formatTags(tags: List<String>): String = tags.joinToString(", ")
