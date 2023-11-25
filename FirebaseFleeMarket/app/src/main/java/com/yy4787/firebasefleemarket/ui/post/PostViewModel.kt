package com.yy4787.firebasefleemarket.ui.post

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yy4787.firebasefleemarket.data.message.MessageRepository
import com.yy4787.firebasefleemarket.data.post.Post
import com.yy4787.firebasefleemarket.data.post.PostRepository
import com.yy4787.firebasefleemarket.data.AppPreferences
import com.yy4787.firebasefleemarket.data.message.Message
import com.yy4787.firebasefleemarket.data.userdata.UserDataRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PostViewModel(application: Application, postId: String?) : ViewModel() {

    private val _eventChannel = Channel<Event>()
    val event = _eventChannel.receiveAsFlow()

    private val postRepository = PostRepository()
    private val messageRepository = MessageRepository()
    private val userDataRepository = UserDataRepository()
    private val appPreferences = AppPreferences.getInstance(application)

    val post = postRepository.getPost(postId)
    val currentUid = appPreferences.currentUid
    private val currentUserData = userDataRepository.getUser(currentUid)


    fun onModifyPostClick() {
        val postValue = post.value ?: return
        if (postValue.writerUid == currentUid) {
            sendEvent(Event.NavigateToModifyPostScreen(postValue))
        }
    }

    fun onSendMessageClick() {
        val postValue = post.value ?: return
        if (postValue.writerUid != currentUid) {
            sendEvent(Event.PromptMessage(postValue.writerEmail))
        }
    }

    fun onSendMessageResult(text: String) {

        val postValue = post.value ?: return
        if (postValue.writerUid == currentUid) {
            return
        }

        val senderUid = currentUid ?: return
        val senderEmail = currentUserData.value?.email ?: return
        val receiverUid = postValue.writerUid
        val postId = post.value?.id ?: return

        if (text.isBlank()) {
            sendEvent(Event.ShowMessage("메세지를 입력하세요"))
            return
        }

        val message = Message(
            senderUid,
            senderEmail,
            receiverUid,
            postId,
            text.trim()
        )

        messageRepository.addMessage(message,
            { sendEvent(Event.ShowMessage("메세지를 보냈습니다")) },
            { e ->
                sendEvent(Event.ShowMessage("네트워크를 확인해주세요"))
                e.printStackTrace()
            })

    }

    private fun sendEvent(event: Event) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }

    sealed class Event {
        class ShowMessage(val message: String) : Event()
        class PromptMessage(val writerEmail: String) : Event()
        class NavigateToModifyPostScreen(val post: Post) : Event()
    }
}










