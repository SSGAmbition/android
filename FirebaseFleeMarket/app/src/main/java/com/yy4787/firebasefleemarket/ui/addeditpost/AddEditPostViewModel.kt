package com.yy4787.firebasefleemarket.ui.addeditpost

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yy4787.firebasefleemarket.data.post.Post
import com.yy4787.firebasefleemarket.data.post.PostRepository
import com.yy4787.firebasefleemarket.data.AppPreferences
import com.yy4787.firebasefleemarket.data.userdata.UserDataRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class AddEditPostViewModel(application: Application, val post: Post?): ViewModel() {

    private val _eventChannel = Channel<Event>()
    val event = _eventChannel.receiveAsFlow()

    private val postRepository = PostRepository()
    private val userDataRepository = UserDataRepository()
    private val appPreferences = AppPreferences.getInstance(application)

    private val currentUid = appPreferences.currentUid
    private val currentUserData = userDataRepository.getUser(currentUid)
    val currentUserEmail = currentUserData.map { userData -> userData?.email }

    var title = post?.title ?: ""
    var price = post?.price?.toString() ?: ""
    var content = post?.content ?: ""
    var soldOut = post?.soldOut ?: false

    fun onTitleChanged(text: String) {
        if (post == null) {
            title = text.trim()
        }
    }

    fun onContentChanged(text: String) {
        if (post == null) {
            content = text.trim()
        }
    }

    fun onPriceChanged(text: String) {
        price = text.trim()
    }

    fun onSoldOutChecked(isChecked: Boolean) {
        soldOut = isChecked
    }

    fun onSubmitClick() {

        if (currentUid == null) {
            return
        }

        if (title.isBlank() || price.isBlank() || content.isBlank()) {
            sendEvent(Event.ShowMessage("모두 입력해주세요"))
            return
        }

        var priceInt = 0
        try {
            priceInt = Integer.parseInt(price)
        } catch (e: NumberFormatException) {
            sendEvent(Event.ShowMessage("가격을 올바르게 입력해주세요"))
            e.printStackTrace()
        }

        val email = currentUserData.value?.email ?: return

        val newPost = post?.copy(price = priceInt, soldOut = soldOut)
            ?: Post(
                currentUid,
                email,
                title,
                content,
                priceInt,
                soldOut
            )

        postRepository.addPost(newPost,
            {
                if (post == null) {
                    sendEvent(Event.NavigateBackWithMessage("판매글이 작성되었습니다"))
                } else {
                    sendEvent(Event.NavigateBackWithMessage("판매글이 수정되었습니다"))
                }
            },
            {
                sendEvent(Event.ShowMessage("네트워크를 확인해주세요"))
                it.printStackTrace()
            })
    }


    private fun sendEvent(event: Event) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }

    sealed class Event {
        class ShowMessage(val message: String): Event()
        class NavigateBackWithMessage(val message: String): Event()
    }

}



