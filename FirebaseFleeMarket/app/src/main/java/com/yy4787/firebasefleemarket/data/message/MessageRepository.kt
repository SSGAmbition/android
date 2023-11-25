package com.yy4787.firebasefleemarket.data.message

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MessageRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val messageCollection = firestore.collection("messages")

    fun addMessage(
        message: Message,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        messageCollection.document(message.id)
            .set(message)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun getMessages(receiverUid: String?) : StateFlow<List<Message>?> {

        val dataFlow = MutableStateFlow<List<Message>?>(ArrayList())
        if (receiverUid == null) {
            dataFlow.value = null
            return dataFlow
        }

        messageCollection
            .whereEqualTo("receiverUid", receiverUid)
            .orderBy("created", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null || querySnapshot == null) {
                    dataFlow.value = null
                    return@addSnapshotListener
                }
                val messages = querySnapshot.toObjects(Message::class.java)
                dataFlow.value = messages
            }

        return dataFlow
    }

}