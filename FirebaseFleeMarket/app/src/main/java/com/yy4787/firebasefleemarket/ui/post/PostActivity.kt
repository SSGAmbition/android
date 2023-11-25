package com.yy4787.firebasefleemarket.ui.post

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.yy4787.firebasefleemarket.ui.addeditpost.AddEditPostActivity
import com.yy4787.firebasefleemarket.data.post.Post
import com.yy4787.firebasefleemarket.databinding.ActivityPostBinding
import com.yy4787.firebasefleemarket.databinding.SendMessageViewBinding
import kotlinx.coroutines.launch
import java.util.Locale

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var viewModel: PostViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getStringExtra("post_id")
        viewModel = ViewModelProvider(
            this, PostViewModelFactory(application, postId)
        )[PostViewModel::class.java]

        if (viewModel.currentUid == null) {
            finish()
            return
        }

        with(binding) {
            buttonModifyPost.setOnClickListener { viewModel.onModifyPostClick() }
            buttonSendMessage.setOnClickListener { viewModel.onSendMessageClick() }
        }

        lifecycleScope.launch {
            viewModel.post.collect { post ->
                if (post != null) {
                    updateUI(post, viewModel.currentUid!!)
                } else {
                    showNoPostMessage()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.event.collect { event ->
                when (event) {
                    is PostViewModel.Event.ShowMessage -> {
                        Toast.makeText(this@PostActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is PostViewModel.Event.PromptMessage -> {
                        showSendMessageDialog(event.writerEmail)
                    }

                    is PostViewModel.Event.NavigateToModifyPostScreen -> {
                        val intent = Intent(this@PostActivity, AddEditPostActivity::class.java)
                        intent.putExtra("post", event.post)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun updateUI(post: Post, currentUid: String) {
        with(binding) {
            textViewPostTitle.text = post.title
            textViewPostWriter.text = post.writerEmail
            textViewPostSoldOut.text = if (post.soldOut) "판매완료" else "판매중"
            textViewPostPrice.text = String.format(Locale.getDefault(), "%d원", post.price)
            textViewPostContent.text = post.content

            textViewPostSoldOut.alpha = if (post.soldOut) 0.7f else 1f
            textViewPostPrice.alpha = if (post.soldOut) 0.5f else 1f

            buttonModifyPost.visibility = View.INVISIBLE
            buttonSendMessage.visibility = View.INVISIBLE
            if (post.writerUid == currentUid) {
                buttonModifyPost.visibility = View.VISIBLE
            } else {
                buttonSendMessage.visibility = View.VISIBLE
                buttonSendMessage.isEnabled = !post.soldOut
            }
        }
    }

    private fun showNoPostMessage() {
        AlertDialog.Builder(this)
            .setTitle("에러")
            .setMessage("네트워크를 확인하세요")
            .setPositiveButton("확인") { _, _ -> finish() }
            .setOnDismissListener { finish() }
            .show()
    }

    private fun showSendMessageDialog(writerEmail: String) {

        val sendMessageView = SendMessageViewBinding.inflate(layoutInflater, null, false)
        val editTextMessage = sendMessageView.editTextMessage

        AlertDialog.Builder(this)
            .setView(sendMessageView.root)
            .setTitle("$writerEmail 에게 메세지 보내기")
            .setPositiveButton("보내기") { _, _ ->
                viewModel.onSendMessageResult(editTextMessage.text.toString())
            }
            .setNegativeButton("취소", null)
            .show()
    }

}