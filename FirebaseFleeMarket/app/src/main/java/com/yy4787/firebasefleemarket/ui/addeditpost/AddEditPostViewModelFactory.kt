package com.yy4787.firebasefleemarket.ui.addeditpost

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yy4787.firebasefleemarket.data.post.Post

@Suppress("UNCHECKED_CAST")
class AddEditPostViewModelFactory(private val application: Application, private val post: Post?)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditPostViewModel(application, post) as T
    }
}