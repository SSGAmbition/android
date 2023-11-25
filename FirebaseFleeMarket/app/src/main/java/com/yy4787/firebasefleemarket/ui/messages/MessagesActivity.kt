package com.yy4787.firebasefleemarket.ui.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.yy4787.firebasefleemarket.databinding.ActivityMessagesBinding
import kotlinx.coroutines.launch

class MessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessagesBinding
    private lateinit var viewModel: MessagesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(MessagesViewModel::class.java)

        val messageAdapter = MessageAdapter()

        with(binding) {
            recyclerMessage.setHasFixedSize(true)
            recyclerMessage.adapter = messageAdapter
        }

        lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                if (messages != null) {
                    messageAdapter.submitList(messages)
                    binding.textViewNoMessages.visibility =
                        if (messages.isEmpty()) View.VISIBLE else View.INVISIBLE
                } else {
                    Toast.makeText(this@MessagesActivity, "네트워크를 확인해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
















