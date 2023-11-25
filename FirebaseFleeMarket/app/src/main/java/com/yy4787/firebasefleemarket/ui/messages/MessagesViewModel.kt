package com.yy4787.firebasefleemarket.ui.messages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yy4787.firebasefleemarket.data.message.MessageRepository
import com.yy4787.firebasefleemarket.data.AppPreferences
import com.yy4787.firebasefleemarket.data.message.Message
import com.yy4787.firebasefleemarket.ui.home.HomeViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MessagesViewModel(app: Application) : AndroidViewModel(app) {

    private val preferences = AppPreferences.getInstance(app)
    private val messageRepository = MessageRepository()

    private val currentUid = preferences.currentUid
    val messages = messageRepository.getMessages(currentUid)

}
















