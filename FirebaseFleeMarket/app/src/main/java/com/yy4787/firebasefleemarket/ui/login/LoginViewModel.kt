package com.yy4787.firebasefleemarket.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yy4787.firebasefleemarket.data.AppPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(app: Application): AndroidViewModel(app) {

    private val _eventChannel = Channel<Event>()
    val event = _eventChannel.receiveAsFlow()

    private var email = ""
    private var password = ""

    private val auth = FirebaseAuth.getInstance()
    private val preferences = AppPreferences.getInstance(app)


    fun onEmailChanged(text: String) {
        email = text.trim()
    }

    fun onPasswordChanged(text: String) {
        password = text.trim()
    }

    fun onLoginClick() {

        if (email.isBlank() or password.isBlank()) {
            sendEvent(Event.ShowMessage("모두 입력해주세요"))
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
               sendEvent(Event.ShowMessage("로그인에 성공했습니다"))
            }
            .addOnFailureListener {
                sendEvent(Event.ShowMessage("로그인에 실패했습니다"))
                it.printStackTrace()
            }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            sendEvent(Event.NavigateToRegisterScreen)
        }
    }

    fun onAuthStateChanged() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            preferences.currentUid = uid
            sendEvent(Event.NavigateToHomeScreen)
        }
    }


    private fun sendEvent(event: Event) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }

    sealed class Event {
        class ShowMessage(val message: String): Event()
        data object NavigateToRegisterScreen: Event()
        data object NavigateToHomeScreen: Event()
    }
}