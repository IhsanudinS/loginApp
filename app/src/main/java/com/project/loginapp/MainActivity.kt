package com.project.loginapp

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.TimeUnit.values
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import com.project.loginapp.databinding.ActivityMainBinding
import io.reactivex.Observable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        processedLogin()

        onAction()
    }

    private fun onAction() {
        binding.apply {
            btnLogin.setOnClickListener {
                Intent(this@MainActivity, LoginActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }
    }

    private fun clickLogout(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("CheckResult")
    private fun processedLogin() {
        binding.apply {
            val emailStream = RxTextView.textChanges(edtEmail)
                .skipInitialValue()
                .map { email ->
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                }
            emailStream.subscribe {
                showEmailExistAlert(it)
            }

            val passwordStream = RxTextView.textChanges(edtPassword)
                .skipInitialValue()
                .map { password ->
                    password.length < 8
                }
            passwordStream.subscribe {
                showPasswordExistAlert(it)
            }

            val invalidFieldStream = Observable.combineLatest(
                emailStream,
                passwordStream
            ) {
                    emailInvalid, passwordInvalid ->
                !emailInvalid && !passwordInvalid
            }

            invalidFieldStream.subscribe {
                showButtonLogin(it)
            }
        }
    }

    private fun showButtonLogin(state: Boolean) {
        binding.btnLogin.isEnabled = state
    }

    private fun showPasswordExistAlert(state: Boolean) {
        binding.edtPassword.error = if (state) "Password length less then 8" else null
    }

    private fun showEmailExistAlert(state: Boolean) {
        binding.edtEmail.error = if (state) "Email not valid" else null
    }

}

