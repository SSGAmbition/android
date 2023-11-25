package com.yy4787.firebasefleemarket.data.post

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "PostRepositoryTest"

@RunWith(AndroidJUnit4::class)
class PostRepositoryTest {


    @Test
    fun addPost() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val postRepository = PostRepository()

        val post = Post(
            "ZSAFrrtFSgMMqYMqoYOPm2bYpln1",
            "android@hansung.ac.kr",
            "버티컬 마우스 팝니다",
            "사용감 없습니다",
            20000,
            false,
            System.currentTimeMillis()
        )

        postRepository.addPost(post,
            {
                Log.d(TAG, "addPost: success")
            },
            {
                it.printStackTrace()
            }
        )

        Thread.sleep(5000)
    }
}