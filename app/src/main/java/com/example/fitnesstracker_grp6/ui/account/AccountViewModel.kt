package com.example.fitnesstracker_grp6.ui.account

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker_grp6.data.model.Account
import com.example.fitnesstracker_grp6.data.repository.AccountRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AccountFeedback {
    INVALID_USERNAME,
    INVALID_EMAIL,
    INVALID_PASSWORD_LENGTH,
    MISSING_CREDENTIALS,
    INVALID_CREDENTIALS,
    DUPLICATE_ACCOUNT,
    ACCOUNT_CREATED,
    ACCOUNT_UNAVAILABLE
}

data class AccountScreenState(
    val showRegistration: Boolean = false,
    val isSubmitting: Boolean = false,
    val signedInAccount: Account? = null,
    val feedback: AccountFeedback? = null
)

class AccountViewModel(private val accountRepository: AccountRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(AccountScreenState())
    val state = mutableState.asStateFlow()

    fun switchForm() {
        val currentState = mutableState.value
        if (currentState.isSubmitting || currentState.signedInAccount != null) return
        mutableState.value = AccountScreenState(showRegistration = !currentState.showRegistration)
    }

    fun signOut() {
        mutableState.value = AccountScreenState()
    }

    fun submitForm(usernameOrEmailInput: String, emailInput: String, password: String) {
        val currentState = mutableState.value
        if (currentState.isSubmitting || currentState.signedInAccount != null) return
        val usernameOrEmail = usernameOrEmailInput.trim()
        val email = emailInput.trim()
        val showRegistration = currentState.showRegistration
        val validationFeedback = when {
            showRegistration && !USERNAME_PATTERN.matches(usernameOrEmail) -> AccountFeedback.INVALID_USERNAME
            showRegistration && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> AccountFeedback.INVALID_EMAIL
            showRegistration && password.length !in 8..128 -> AccountFeedback.INVALID_PASSWORD_LENGTH
            usernameOrEmail.isEmpty() || password.isEmpty() -> AccountFeedback.MISSING_CREDENTIALS
            password.length > 128 -> AccountFeedback.INVALID_CREDENTIALS
            else -> null
        }
        if (validationFeedback != null) {
            mutableState.value = currentState.copy(feedback = validationFeedback)
            return
        }

        mutableState.value = currentState.copy(isSubmitting = true, feedback = null)
        viewModelScope.launch {
            mutableState.value = try {
                if (showRegistration) {
                    if (accountRepository.register(usernameOrEmail, email, password)) {
                        AccountScreenState(feedback = AccountFeedback.ACCOUNT_CREATED)
                    } else {
                        currentState.copy(feedback = AccountFeedback.DUPLICATE_ACCOUNT)
                    }
                } else {
                    val account = accountRepository.signIn(usernameOrEmail, password)
                    if (account == null) {
                        currentState.copy(feedback = AccountFeedback.INVALID_CREDENTIALS)
                    } else {
                        AccountScreenState(signedInAccount = account)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                currentState.copy(feedback = AccountFeedback.ACCOUNT_UNAVAILABLE)
            }
        }
    }

    private companion object {
        val USERNAME_PATTERN = Regex("[A-Za-z0-9_]{3,24}")
    }
}
