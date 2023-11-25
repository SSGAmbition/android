package com.yy4787.firebasefleemarket.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yy4787.firebasefleemarket.data.post.Post
import com.yy4787.firebasefleemarket.data.post.PostRepository
import com.yy4787.firebasefleemarket.data.AppPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val _eventChannel = Channel<Event>()
    val event = _eventChannel.receiveAsFlow()

    private val auth = FirebaseAuth.getInstance()
    private val preferences = AppPreferences.getInstance(app)
    private val postRepository = PostRepository()

    private val excludeSoldOut = MutableStateFlow(false)
    private val maximumPrice = MutableStateFlow(0)
    private val filter = combine(excludeSoldOut, maximumPrice) { a, b -> Pair(a, b) }
    val posts = filter.flatMapLatest { (excludeSoldOut, maximumPrice) ->
        postRepository.getPosts(excludeSoldOut, maximumPrice)
    }

    fun onAuthStateChanged() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            preferences.currentUid = null
            sendEvent(Event.NavigateBack)
        }
    }

    fun onBackClick() {
        sendEvent(Event.ConfirmLogout)
    }

    fun onPostClick(post: Post) {
        sendEvent(Event.NavigateToPostScreen(post.id))
    }

    fun onExcludeSoldOutChecked(isChecked: Boolean) {
        excludeSoldOut.value = isChecked
    }

    fun onMaxPriceChanged(text: String) {
        try {
            val price = if (text.isBlank()) 0 else Integer.parseInt(text.trim())
            maximumPrice.value = price
        } catch (e: NumberFormatException) {
            maximumPrice.value = 0
            e.printStackTrace()
        }
    }

    fun onLogoutClick() {
        sendEvent(Event.ConfirmLogout)
    }

    fun onLogoutConfirm() {
        auth.signOut()
    }

    fun onAddPostClick() {
        sendEvent(Event.NavigateToAddPostScreen)
    }

    fun onMessagesClick() {
        sendEvent(Event.NavigateToMessagesScreen)
    }

    private fun sendEvent(event: Event) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }

    sealed class Event {
        class ShowMessage(val message: String) : Event()
        data object NavigateBack : Event()
        class NavigateToPostScreen(val postId: String) : Event()
        data object NavigateToAddPostScreen : Event()
        data object NavigateToMessagesScreen : Event()
        data object ConfirmLogout : Event()
    }
}













