package com.yy4787.firebasefleemarket.data.userdata

data class UserData(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val birthYear: Int = 0,
    val birthMonth: Int = 0,
    val birthDate: Int = 0,
    val created: Long = System.currentTimeMillis(),
)
