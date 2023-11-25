package com.yy4787.firebasefleemarket.data.post

import java.io.Serializable

data class Post(
    val writerUid: String = "",
    val writerEmail: String = "",
    val title: String = "",
    val content: String = "",
    val price: Int = 0,
    val soldOut: Boolean = false,
    val created: Long = System.currentTimeMillis()
): Serializable {
    val id = "${writerUid}_$created"
}
