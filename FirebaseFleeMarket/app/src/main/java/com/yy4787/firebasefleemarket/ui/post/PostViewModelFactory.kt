package com.yy4787.firebasefleemarket.ui.post

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class PostViewModelFactory(private val application: Application, private val postId: String?)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostViewModel(application, postId) as T
    }
}