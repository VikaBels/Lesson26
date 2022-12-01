package com.example.lesson26.viewmodels

import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bolts.Task
import com.example.lesson26.R
import com.example.lesson26.models.UIError
import com.example.lesson26.repositories.RegisterRepository
import com.example.lesson26.utils.getIdError
import com.example.lesson26.utils.isOnline

class RegisterViewModel : BaseViewModel() {
    private var uiError = MutableLiveData<UIError>()
    private var token = MutableLiveData<String>()

    val error: LiveData<UIError>
        get() = uiError

    val currentToken: LiveData<String>
        get() = token

    private val errorEmail = MutableLiveData<Int>()
    private val errorName = MutableLiveData<Int>()
    private val errorLastName = MutableLiveData<Int>()
    private val errorPassword = MutableLiveData<Int>()
    private val errorRepeatPassword = MutableLiveData<Int>()

    val errorEmailField: LiveData<Int>
        get() = errorEmail

    val errorNameField: LiveData<Int>
        get() = errorName

    val errorLastNameField: LiveData<Int>
        get() = errorLastName

    val errorPasswordField: LiveData<Int>
        get() = errorPassword

    val errorRepeatPasswordField: LiveData<Int>
        get() = errorRepeatPassword


    fun startServerRegisterRepository(
        email: String?,
        name: String?,
        lastName: String?,
        password: String?,
        repeatPassword: String?
    ) {
        val currentEmail = getValidEmail(email)
        val currentName = getValidName(name)
        val currentLastName = getValidLastName(lastName)
        val currentPassword = getValidPassword(password)
        val currentRepeatPassword = getValidRepeatPassword(repeatPassword, password)

        if (currentEmail != null &&
            currentName != null &&
            currentLastName != null &&
            currentPassword != null &&
            currentRepeatPassword != null
        ) {

            if (isOnline()) {
                startServiceRequest(
                    currentEmail,
                    currentName,
                    currentLastName,
                    currentPassword
                )

            } else {
                uiError.value = UIError(R.string.error_no_internet)
            }

        } else {
            uiError.value = UIError(R.string.error_invalid_data_register)
        }
    }

    private fun startServiceRequest(
        currentEmail: String,
        currentName: String,
        currentLastName: String,
        currentPassword: String,
    ) {
        val registerRepository = RegisterRepository().getRegisterResponse(
            currentEmail,
            currentName,
            currentLastName,
            currentPassword
        )

        registerRepository?.continueWith({

            if (it.result != null) {
                checkServerResponse(it.result.token)
            }

            if (it.error != null) {
                uiError.value = UIError(getIdError(it.error))
            }

        }, Task.UI_THREAD_EXECUTOR, getToken())
    }

    private fun checkServerResponse(token: String?) {
        when (token) {
            null -> {
                uiError.value = UIError(R.string.error_invalid_data_register)
            }
            else -> {
                this.token.value = token
            }
        }
    }

    private fun getValidEmail(email: String?): String? {
        return when {
            email.isNullOrEmpty() -> {
                errorEmail.value = R.string.error_empty_email_field
                null
            }
            !isMatchesEmailPattern(email) -> {
                errorEmail.value = R.string.error_not_valid_email_field
                null
            }
            else -> {
                errorEmail.value = R.string.no_error
                email
            }
        }
    }

    private fun getValidName(name: String?): String? {
        return when {
            name.isNullOrEmpty() -> {
                errorName.value = R.string.error_empty_name_field
                null
            }
            else -> {
                errorName.value = R.string.no_error
                name
            }
        }
    }

    private fun getValidLastName(lastName: String?): String? {
        return when {
            lastName.isNullOrEmpty() -> {
                errorLastName.value = R.string.error_empty_password_field
                null
            }
            else -> {
                errorLastName.value = R.string.no_error
                lastName
            }
        }
    }

    private fun getValidPassword(password: String?): String? {
        return when {
            password.isNullOrEmpty() -> {
                errorPassword.value = R.string.error_empty_password_field
                null
            }
            else -> {
                errorPassword.value = R.string.no_error
                password
            }
        }
    }

    private fun getValidRepeatPassword(repeatPassword: String?, password: String?): String? {
        return when {
            repeatPassword.isNullOrEmpty() -> {
                errorRepeatPassword.value = R.string.error_empty_password_field
                null
            }
            !identityVerificationPasswords(repeatPassword, password) -> {
                errorRepeatPassword.value = R.string.error_empty_different_password
                null
            }
            else -> {
                errorRepeatPassword.value = R.string.no_error
                repeatPassword
            }
        }
    }

    private fun isMatchesEmailPattern(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun identityVerificationPasswords(repeatPassword: String?, password: String?): Boolean {
        return password == repeatPassword
    }
}
