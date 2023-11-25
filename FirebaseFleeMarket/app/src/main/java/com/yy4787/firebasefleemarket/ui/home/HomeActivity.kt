package com.yy4787.firebasefleemarket.ui.home

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.yy4787.firebasefleemarket.R
import com.yy4787.firebasefleemarket.ui.addeditpost.AddEditPostActivity
import com.yy4787.firebasefleemarket.databinding.ActivityHomeBinding
import com.yy4787.firebasefleemarket.ui.messages.MessagesActivity
import com.yy4787.firebasefleemarket.ui.post.PostActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), AuthStateListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(HomeViewModel::class.java)

        val adapter = PostAdapter().apply {
            setOnItemClickListener(object : PostAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val post = currentList[position]
                    viewModel.onPostClick(post)
                }
            })
        }

        with(binding) {
            switchExcludeSoldOut.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onExcludeSoldOutChecked(isChecked)
            }
            editTextMaxPrice.addTextChangedListener { text -> viewModel.onMaxPriceChanged(text.toString()) }
            recyclerPost.setHasFixedSize(true)
            recyclerPost.adapter = adapter
            buttonLogout.setOnClickListener { viewModel.onLogoutClick() }
            fabAddPost.setOnClickListener { viewModel.onAddPostClick() }
            fabMessages.setOnClickListener { viewModel.onMessagesClick() }
        }

        lifecycleScope.launch {
            viewModel.posts.collect { posts ->
                if (posts != null) {
                    adapter.submitList(posts)
                    binding.textViewNoPosts.visibility =
                        if (posts.isEmpty()) View.VISIBLE else View.INVISIBLE
                    binding.textViewPostCount.text =
                        resources.getString(R.string.post_count, posts.size)
                } else {
                    Toast.makeText(this@HomeActivity, "네트워크를 확인하세요", Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.event.collect { event ->
                when (event) {
                    is HomeViewModel.Event.ShowMessage -> {
                        Toast.makeText(this@HomeActivity, event.message, Toast.LENGTH_SHORT).show()
                    }

                    is HomeViewModel.Event.NavigateBack -> {
                        finish()
                    }

                    is HomeViewModel.Event.NavigateToPostScreen -> {
                        val intent = Intent(this@HomeActivity, PostActivity::class.java)
                        intent.putExtra("post_id", event.postId)
                        startActivity(intent)
                    }

                    is HomeViewModel.Event.NavigateToAddPostScreen -> {
                        val intent = Intent(this@HomeActivity, AddEditPostActivity::class.java)
                        startActivity(intent)
                    }

                    is HomeViewModel.Event.NavigateToMessagesScreen -> {
                        val intent = Intent(this@HomeActivity, MessagesActivity::class.java)
                        startActivity(intent)
                    }

                    is HomeViewModel.Event.ConfirmLogout -> {
                        showConfirmLogoutDialog()
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onBackClick()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        auth.addAuthStateListener(this)
    }

    override fun onPause() {
        auth.removeAuthStateListener(this)
        super.onPause()
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        viewModel.onAuthStateChanged()
    }

    private fun showConfirmLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("로그아웃")
            .setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton(
                "로그아웃"
            ) { _, _ -> viewModel.onLogoutConfirm() }
            .setNegativeButton("취소", null)
            .show()
    }

}












