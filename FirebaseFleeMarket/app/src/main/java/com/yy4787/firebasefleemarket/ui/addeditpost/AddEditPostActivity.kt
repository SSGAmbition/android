package com.yy4787.firebasefleemarket.ui.addeditpost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.yy4787.firebasefleemarket.data.post.Post
import com.yy4787.firebasefleemarket.databinding.ActivityAddEditPostBinding
import kotlinx.coroutines.launch

class AddEditPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditPostBinding
    private lateinit var viewModel: AddEditPostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = intent.getSerializableExtra("post") as Post?
        viewModel = ViewModelProvider(
            this, AddEditPostViewModelFactory(application, post)
        )[AddEditPostViewModel::class.java]

        with(binding) {
            editTextPostTitle.addTextChangedListener { text -> viewModel.onTitleChanged(text.toString()) }
            editTextPostPrice.addTextChangedListener { text -> viewModel.onPriceChanged(text.toString()) }
            editTextPostContent.addTextChangedListener { text -> viewModel.onContentChanged(text.toString()) }
            switchSoldOut.setOnCheckedChangeListener { _, isChecked -> viewModel.onSoldOutChecked(isChecked) }
            buttonSubmitPost.setOnClickListener { viewModel.onSubmitClick() }
        }

        updateUI(viewModel.post)

        lifecycleScope.launch {
            viewModel.currentUserEmail.collect { email ->
                if (email != null) {
                    binding.textViewPostWriter.text = email
                }
            }
        }

        lifecycleScope.launch {
            viewModel.event.collect { event ->
                when (event) {
                    is AddEditPostViewModel.Event.ShowMessage -> {
                        Toast.makeText(this@AddEditPostActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is AddEditPostViewModel.Event.NavigateBackWithMessage -> {
                        Toast.makeText(this@AddEditPostActivity, event.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun updateUI(post: Post?) {
        with(binding) {
            editTextPostTitle.setText(viewModel.title)
            editTextPostContent.setText(viewModel.content)
            editTextPostPrice.setText(viewModel.price)
            switchSoldOut.isChecked = viewModel.soldOut

            editTextPostTitle.isEnabled = (post == null)
            editTextPostContent.isEnabled = (post == null)
            switchSoldOut.isEnabled = (post != null)

            buttonSubmitPost.text = if (post == null) "작성완료" else "수정완료"
        }
    }
}







