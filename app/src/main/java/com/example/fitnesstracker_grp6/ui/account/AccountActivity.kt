package com.example.fitnesstracker_grp6.ui.account

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnesstracker_grp6.R
import com.example.fitnesstracker_grp6.data.setup.FitnessTrackerApplication
import com.example.fitnesstracker_grp6.databinding.ActivityAccountBinding
import com.example.fitnesstracker_grp6.ui.dashboard.DashboardActivity
import kotlinx.coroutines.launch

class AccountActivity : AppCompatActivity() {
    private val viewModel: AccountViewModel by viewModels {
        viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as FitnessTrackerApplication
                AccountViewModel(app.accountRepository)
            }
        }
    }
    private lateinit var binding: ActivityAccountBinding
    private var wasShowingRegistration: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val padding = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            insets
        }

        binding.submitButton.setOnClickListener {
            viewModel.submitForm(
                usernameOrEmailInput = binding.usernameOrEmailField.text.toString(),
                emailInput = binding.emailField.text.toString(),
                password = binding.passwordField.text.toString()
            )
        }
        binding.switchFormButton.setOnClickListener { viewModel.switchForm() }
        binding.signOutButton.setOnClickListener { viewModel.signOut() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::render)
            }
        }
    }

    private fun render(state: AccountScreenState) = with(binding) {
        val account = state.signedInAccount
        if (account != null) {
            val intent = Intent(this@AccountActivity, DashboardActivity::class.java).apply {
                putExtra(DashboardActivity.EXTRA_USER_ID, account.id)
                putExtra(DashboardActivity.EXTRA_USERNAME, account.username)
                putExtra(DashboardActivity.EXTRA_EMAIL, account.email)
            }
            startActivity(intent)
            finish()
            return@with
        }

        val showRegistration = state.showRegistration
        val formChanged = wasShowingRegistration?.let { it != showRegistration } ?: false
        val isSignedIn = state.signedInAccount != null
        if (isSignedIn || formChanged) {
            usernameOrEmailField.text?.clear()
            emailField.text?.clear()
            passwordField.text?.clear()
        }
        wasShowingRegistration = showRegistration
        accountForm.isVisible = !isSignedIn
        signedInPanel.isVisible = isSignedIn
        val submitLabel = if (showRegistration) R.string.auth_register else R.string.auth_login
        val switchFormLabel = if (showRegistration) R.string.auth_back_to_login else R.string.auth_create_account
        val usernameHint = if (showRegistration) R.string.auth_username else R.string.auth_identifier
        formTitle.setText(submitLabel)
        submitButton.setText(submitLabel)
        switchFormButton.setText(switchFormLabel)
        usernameOrEmailLayout.hint = getString(usernameHint)
        emailLayout.isVisible = showRegistration
        usernameOrEmailField.isEnabled = !state.isSubmitting
        emailField.isEnabled = !state.isSubmitting
        passwordField.isEnabled = !state.isSubmitting
        switchFormButton.isEnabled = !state.isSubmitting
        submitButton.isEnabled = !state.isSubmitting
        loadingIndicator.isVisible = state.isSubmitting
        val messageResId = when (state.feedback) {
            AccountFeedback.INVALID_USERNAME -> R.string.auth_invalid_username
            AccountFeedback.INVALID_EMAIL -> R.string.auth_invalid_email
            AccountFeedback.INVALID_PASSWORD_LENGTH -> R.string.auth_password_length
            AccountFeedback.MISSING_CREDENTIALS -> R.string.auth_missing_credentials
            AccountFeedback.INVALID_CREDENTIALS -> R.string.auth_invalid_credentials
            AccountFeedback.DUPLICATE_ACCOUNT -> R.string.auth_duplicate_account
            AccountFeedback.ACCOUNT_CREATED -> R.string.auth_registered
            AccountFeedback.ACCOUNT_UNAVAILABLE -> R.string.auth_unavailable
            null -> null
        }
        formMessage.isVisible = messageResId != null
        formMessage.text = messageResId?.let(::getString)
    }
}
