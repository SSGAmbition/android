package com.yy4787.firebasefleemarket.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.yy4787.firebasefleemarket.data.userdata.UserData
import com.yy4787.firebasefleemarket.data.userdata.UserDataRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class RegisterViewModel: ViewModel() {

    private val _eventChannel = Channel<Event>()
    val event = _eventChannel.receiveAsFlow()

    private var email = ""
    private var password = ""
    private var name = ""
    private var birthday = ""

    private val auth = FirebaseAuth.getInstance()
    private val userDataRepository = UserDataRepository()


    fun onEmailChanged(text: String) {
        email = text.trim()
    }

    fun onPasswordChanged(text: String) {
        password = text.trim()
    }

    fun onNameChanged(text: String) {
        name = text.trim()
    }

    fun onBirthdayChanged(text: String) {
        birthday = text.trim()
    }

    fun onRegisterClick() {

        if (email.isBlank() or password.isBlank() or name.isBlank() or birthday.isBlank()) {
            sendEvent(Event.ShowMessage("모두 입력해주세요"))
            return
        }

        if (birthday.length != 8) {
            sendEvent(Event.ShowMessage("생년월일을 20020108 형식으로 입력해주세요"))
            return
        }

        var birth: LocalDate? = null
        var year = 0
        var month = 0
        var day = 0
        try {
            year = Integer.parseInt(birthday.substring(0, 4))
            month = Integer.parseInt(birthday.substring(4, 6))
            day = Integer.parseInt(birthday.substring(6, 8))
            birth = LocalDate.of(year, month, day)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (birth == null) {
            sendEvent(Event.ShowMessage("생년월일을 올바르게 입력해주세요"))
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    val newUserData = UserData(uid, email, name, year, month, day)
                    userDataRepository.addUserData(newUserData,
                        { sendEvent(Event.NavigateBack) },
                        { it.printStackTrace() }
                    )
                }
            }
            .addOnFailureListener {
                sendEvent(Event.ShowMessage("회원가입에 실패했습니다"))
                it.printStackTrace()
            }
    }

    fun onBackClick() {
        viewModelScope.launch {
            sendEvent(Event.NavigateBack)
        }
    }


    private fun sendEvent(event: Event) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }

    sealed class Event {
        class ShowMessage(val message: String): Event()
        data object NavigateBack: Event()
    }
}