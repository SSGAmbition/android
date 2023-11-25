package com.yy4787.firebasefleemarket.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.yy4787.firebasefleemarket.R
import com.yy4787.firebasefleemarket.databinding.ActivityLoginBinding
import com.yy4787.firebasefleemarket.ui.home.HomeActivity
import com.yy4787.firebasefleemarket.ui.register.RegisterActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), AuthStateListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(LoginViewModel::class.java)

        with(binding) {
            editTextEmail.addTextChangedListener { text -> viewModel.onEmailChanged(text.toString()) }
            editTextPassword.addTextChangedListener { text -> viewModel.onPasswordChanged(text.toString()) }
            buttonLogin.setOnClickListener { viewModel.onLoginClick() }
            buttonRegister.setOnClickListener { viewModel.onRegisterClick() }
        }

        lifecycleScope.launch {
            viewModel.event.collect { event ->
                when (event) {
                    is LoginViewModel.Event.ShowMessage -> {
                        Toast.makeText(this@LoginActivity, event.message, Toast.LENGTH_SHORT).show()
                    }

                    is LoginViewModel.Event.NavigateToRegisterScreen -> {
                        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                        startActivity(intent)
                    }

                    is LoginViewModel.Event.NavigateToHomeScreen -> {
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
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
}










