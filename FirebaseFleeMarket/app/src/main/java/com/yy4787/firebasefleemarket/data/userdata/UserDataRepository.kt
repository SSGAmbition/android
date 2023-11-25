package com.yy4787.firebasefleemarket.data.userdata

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class UserDataRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val userDataCollection = firestore.collection("user_data")

    fun addUserData(userData: UserData,
                    onSuccessListener: OnSuccessListener<Void>,
                    onFailureListener: OnFailureListener) {
        userDataCollection.document(userData.uid)
            .set(userData)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun getUser(userId: String?) : StateFlow<UserData?> {

        val dataFlow = MutableStateFlow<UserData?>(UserData())
        if (userId == null) {
            dataFlow.value = null
            return dataFlow
        }

        userDataCollection
            .document(userId)
            .addSnapshotListener{ documentSnapshot, error ->
                if (error != null || documentSnapshot == null) {
                    dataFlow.value = null
                    error?.printStackTrace()
                    return@addSnapshotListener
                }
                dataFlow.value = documentSnapshot.toObject(UserData::class.java)
            }

        return dataFlow
    }

}