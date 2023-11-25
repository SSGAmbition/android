package com.yy4787.firebasefleemarket.data.post

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PostRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val postCollection = firestore.collection("posts")

    fun addPost(
        post: Post,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        postCollection.document(post.id)
            .set(post)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun getPosts(excludeSoldOut: Boolean, maxPrice: Int)
            : StateFlow<List<Post>?> {

        val dataFlow = MutableStateFlow<List<Post>?>(ArrayList())

        postCollection
            .orderBy("created", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null || querySnapshot == null) {
                    dataFlow.value = null
                    return@addSnapshotListener
                }
                var posts = querySnapshot.toObjects(Post::class.java)
                if (excludeSoldOut) {
                    posts = posts.filter { !it.soldOut }
                }
                if (maxPrice > 0) {
                    posts = posts.filter { it.price <= maxPrice }
                }
                dataFlow.value = posts
            }

        return dataFlow
    }

    fun getPost(postId: String?) : StateFlow<Post?> {

        val dataFlow = MutableStateFlow<Post?>(Post())
        if (postId == null) {
            dataFlow.value = null
            return dataFlow
        }

        postCollection
            .document(postId)
            .addSnapshotListener{ documentSnapshot, error ->
                if (error != null || documentSnapshot == null) {
                    dataFlow.value = null
                    error?.printStackTrace()
                    return@addSnapshotListener
                }
                dataFlow.value = documentSnapshot.toObject(Post::class.java)
            }

        return dataFlow
    }

}