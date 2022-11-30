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
import com.example.lesson26.activities.AuthorizationActivity
import com.example.lesson26.databinding.FragmentRegisterBinding
import com.example.lesson26.factories.RegisterViewModelFactory
import com.example.lesson26.interfaes.RegisterFragmentNavigationListener
import com.example.lesson26.viewmodels.RegisterViewModel

class RegisterFragment : Fragment() {
    companion object {
        fun newInstance(): Fragment {
            return RegisterFragment()
        }
    }

    private var registerFragmentListener: RegisterFragmentNavigationListener? = null
    private var bindingRegisterFragment: FragmentRegisterBinding? = null

    private val registerViewModel by viewModels<RegisterViewModel> {
        RegisterViewModelFactory()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerFragmentListener = context as? RegisterFragmentNavigationListener
            ?: error("$context${resources.getString(R.string.exceptionInterface)}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bindingRegisterFragment = FragmentRegisterBinding.inflate(layoutInflater)
        this.bindingRegisterFragment = bindingRegisterFragment

        return bindingRegisterFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeError()

        observeToken()

        observeFields()

        setUpListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingRegisterFragment = null
    }

    override fun onDetach() {
        super.onDetach()
        registerFragmentListener = null
    }

    private fun observeError() {
        registerViewModel.error.observe(viewLifecycleOwner) { error ->
            setVisibleProgressbar(false)
            setTextError(true, error.textId)
        }
    }

    private fun observeToken() {
        registerViewModel.currentToken.observe(viewLifecycleOwner) {
            setVisibleProgressbar(false)
            registerFragmentListener?.showLoginFragment(AuthorizationActivity.TAG_FOR_LOGIN, false)
        }
    }

    //OBSERVE!!!!!!!!!! - delete some
    //this strange !!!
    private fun observeFields() {

        registerViewModel.errorEmailField.observe(viewLifecycleOwner) { errorId ->
            bindingRegisterFragment?.inputLayoutEmail?.error = getString(errorId)
        }

        registerViewModel.errorNameField.observe(viewLifecycleOwner) { errorId ->
            bindingRegisterFragment?.inputLayoutName?.error = getString(errorId)
        }

        registerViewModel.errorLastNameField.observe(viewLifecycleOwner) { errorId ->
            bindingRegisterFragment?.inputLayoutLastName?.error = getString(errorId)
        }

        registerViewModel.errorPasswordField.observe(viewLifecycleOwner) { errorId ->
            bindingRegisterFragment?.inputLayoutPassword?.error = getString(errorId)
        }

        registerViewModel.errorRepeatPasswordField.observe(viewLifecycleOwner) { errorId ->
            bindingRegisterFragment?.inputLayoutRepeatPassword?.error = getString(errorId)
        }
    }

    private fun setUpListener() {
        bindingRegisterFragment?.apply {
            txtViewLogin.setOnClickListener {
                registerFragmentListener?.showLoginFragment(AuthorizationActivity.TAG_FOR_LOGIN, false)
            }

            buttonRegister.setOnClickListener {

                setVisibleProgressbar(true)
                setTextError(false)

                bindingRegisterFragment?.apply {
                    registerViewModel.startServerRegisterRepository(
                        editTextEmail.text?.toString(),
                        editTextName.text?.toString(),
                        editTextLastName.text?.toString(),
                        editTextPassword.text?.toString(),
                        editTextRepeatPassword.text?.toString(),
                    )
                }
            }
        }
    }

    private fun setVisibleProgressbar(isVisible: Boolean) {
        bindingRegisterFragment?.progressBar?.isVisible = isVisible
    }

    private fun setTextError(isVisible: Boolean, errorId: Int? = null) {
        bindingRegisterFragment?.apply {
            textViewError.isVisible = isVisible
            textViewError.text = errorId?.let { resources.getString(it) }
        }
    }
}