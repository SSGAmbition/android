package com.yy4787.firebasefleemarket.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.yy4787.firebasefleemarket.databinding.ActivityRegisterBinding
import com.yy4787.firebasefleemarket.ui.home.HomeActivity
import com.yy4787.firebasefleemarket.ui.login.LoginViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        with(binding) {
            editTextEmail.addTextChangedListener { text -> viewModel.onEmailChanged(text.toString()) }
            editTextPassword.addTextChangedListener { text -> viewModel.onPasswordChanged(text.toString()) }
            editTextName.addTextChangedListener { text -> viewModel.onNameChanged(text.toString()) }
            editTextBirthday.addTextChangedListener { text -> viewModel.onBirthdayChanged(text.toString()) }
            buttonRegister.setOnClickListener { viewModel.onRegisterClick() }
            buttonBack.setOnClickListener { viewModel.onBackClick() }
        }

        lifecycleScope.launch {
            viewModel.event.collect { event ->
                when (event) {
                    is RegisterViewModel.Event.ShowMessage -> {
                        Toast.makeText(this@RegisterActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is RegisterViewModel.Event.NavigateBack -> {
                        finish()
                    }
                }
            }
        }
    }
}















