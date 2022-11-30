package com.example.lesson26.viewmodels

import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bolts.Task
import com.example.lesson26.R
import com.example.lesson26.models.UIError
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.repositories.LoginRepository
import com.example.lesson26.utils.getIdError
import com.example.lesson26.utils.isOnline

class LoginViewModel(
    private val dataRepository: DataRepository
) : BaseViewModel() {
    private var uiError = MutableLiveData<UIError>()
    private val token = MutableLiveData<String>()

    private val errorEmail = MutableLiveData<Int>()
    private val errorPassword = MutableLiveData<Int>()

    val error: LiveData<UIError>
        get() = uiError

    val currentToken: LiveData<String>
        get() = token

    val errorEmailField: LiveData<Int>
        get() = errorEmail

    val errorPasswordField: LiveData<Int>
        get() = errorPassword

    //rename
    fun startServerLoginRepository(
        email: String?,
        password: String?
    ) {
        val currentEmail = getValidEmail(email)
        val currentPassword = getValidPassword(password)

        if (currentEmail != null && currentPassword != null) {

            if (isOnline()) {
                startServiceRequest(currentEmail, currentPassword)
            }else{
                uiError.value = UIError(R.string.error_no_internet)
            }

        } else {
            uiError.value = UIError(R.string.error_invalid_data_login)
        }
    }

    private fun startServiceRequest(
        currentEmail: String,
        currentPassword: String
    ) {
        val loginRepository = LoginRepository().getLogin(currentEmail, currentPassword)

        loginRepository?.continueWith({

            if (it.result != null) {

                checkServerResponseToken(it.result.token)

                isExistUser(
                    currentEmail,
                    it.result.firstName,
                    it.result.lastName,
                    currentPassword,
                    it.result.token
                )

            }
            if (it.error != null) {
                uiError.value = UIError(getIdError(it.error))
            }

        }, Task.UI_THREAD_EXECUTOR, getToken())
    }

    private fun isExistUser(
        currentEmail: String,
        currentName: String,
        currentLastName: String,
        currentPassword: String,
        currentToken: String?
    ) {
        if (currentToken != null) {
            dataRepository.getIsExistUserTask(
                getToken(),
                currentEmail,
                currentPassword,
            ).continueWith({

                if (it.result != null) {
                    val isExist = it.result

                    if (isExist != null && isExist != 1) {
                        startDataBaseRequest(
                            currentEmail,
                            currentName,
                            currentLastName,
                            currentPassword,
                            currentToken
                        )
                    }
                }

                if (it.error != null) {
                    uiError.value = UIError(getIdError(it.error))
                }

            }, Task.UI_THREAD_EXECUTOR)
        }
    }

    private fun startDataBaseRequest(
        currentEmail: String,
        currentName: String,
        currentLastName: String,
        currentPassword: String,
        currentToken: String?
    ) {

        if (currentToken != null) {
            dataRepository.addNewUserTask(
                getToken(),
                currentName,
                currentLastName,
                currentEmail,
                currentPassword,
                currentToken
            ).continueWith({

                if (it.error != null) {
                    uiError.value = UIError(getIdError(it.error))
                }

            }, Task.UI_THREAD_EXECUTOR)
        }
    }

    private fun checkServerResponseToken(token: String?) {
        when (token) {
            null -> {
                uiError.value = UIError(R.string.error_invalid_data_login)
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

    private fun isMatchesEmailPattern(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
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
}