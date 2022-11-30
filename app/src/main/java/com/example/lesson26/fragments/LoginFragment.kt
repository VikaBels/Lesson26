package com.example.lesson26.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.lesson26.App
import com.example.lesson26.R
import com.example.lesson26.databinding.FragmentLoginBinding
import com.example.lesson26.factories.LoginViewModelFactory
import com.example.lesson26.interfaes.LoginFragmentNavigationListener
import com.example.lesson26.viewmodels.LoginViewModel

class LoginFragment : Fragment() {
    companion object {
        fun newInstance(): Fragment {
            return LoginFragment()
        }
    }

    private var loginFragmentNavigationListener: LoginFragmentNavigationListener? = null
    private var bindingLoginFragment: FragmentLoginBinding? = null

    private val loginViewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory(
            App.getDataRepository()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginFragmentNavigationListener = context as? LoginFragmentNavigationListener
            ?: error("$context${resources.getString(R.string.exceptionInterface)}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bindingLoginFragment = FragmentLoginBinding.inflate(layoutInflater)
        this.bindingLoginFragment = bindingLoginFragment

        return bindingLoginFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeError()

        observeToken()

        observeErrorEmail()

        observeErrorPassword()

        setUpListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingLoginFragment = null
    }

    override fun onDetach() {
        super.onDetach()
        loginFragmentNavigationListener = null
    }

    private fun setUpListeners() {
        bindingLoginFragment?.apply {
            buttonLogin.setOnClickListener {

                setVisibleProgressbar(true)
                setTextError(false)

                bindingLoginFragment?.apply {

                    loginViewModel.startServerLoginRepository(
                        editTextEmail.text?.toString(),
                        editTextPassword.text?.toString()
                    )

                }
            }

            txtViewRegister.setOnClickListener {
                loginFragmentNavigationListener?.showRegisterFragment()
            }
        }
    }

    private fun observeError() {
        loginViewModel.error.observe(viewLifecycleOwner) { error ->
            setVisibleProgressbar(false)
            setTextError(true, error.textId)
        }
    }

    private fun observeToken() {
        loginViewModel.currentToken.observe(viewLifecycleOwner) { token ->
            setVisibleProgressbar(false)
            loginFragmentNavigationListener?.showMainActivity(token)
        }
    }

    private fun observeErrorEmail() {
        loginViewModel.errorEmailField.observe(viewLifecycleOwner) { errorId ->
            showEmailError(errorId)
        }
    }

    private fun observeErrorPassword() {
        loginViewModel.errorPasswordField.observe(viewLifecycleOwner) { errorId ->
            showPasswordError(errorId)
        }
    }

    private fun setVisibleProgressbar(isVisible: Boolean) {
        bindingLoginFragment?.progressBar?.isVisible = isVisible
    }

    private fun setTextError(isVisible: Boolean, errorId: Int? = null) {
        bindingLoginFragment?.apply {
            textViewError.isVisible = isVisible
            textViewError.text = errorId?.let { resources.getString(it) }
        }
    }

    private fun showEmailError(errorId: Int) {
        bindingLoginFragment?.inputLayoutEmail?.error =
            getString(errorId)
    }

    private fun showPasswordError(errorId: Int) {
        bindingLoginFragment?.inputLayoutPassword?.error =
            getString(errorId)
    }
}