package com.yy4787.firebasefleemarket.data.message

data class Message(
    val senderUid: String = "",
    val senderEmail: String = "",
    val receiverUid: String = "",
    val postId: String = "",
    val content: String = "",
    val created: Long = System.currentTimeMillis()
) {
    val id = receiverUid + "_" + senderUid + "_" + created
}
