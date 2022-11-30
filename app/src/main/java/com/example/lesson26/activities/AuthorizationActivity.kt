package com.example.lesson26.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.lesson26.R
import com.example.lesson26.databinding.ActivityAuthorizationBinding
import com.example.lesson26.fragments.LoginFragment
import com.example.lesson26.fragments.RegisterFragment
import com.example.lesson26.interfaes.LoginFragmentNavigationListener
import com.example.lesson26.interfaes.RegisterFragmentNavigationListener

class AuthorizationActivity : AppCompatActivity(),
    LoginFragmentNavigationListener,
    RegisterFragmentNavigationListener {
    companion object {
        const val TAG_FOR_LOGIN = "TAG_FOR_LOGIN"
        const val TAG_FOR_REGISTER = "TAG_FOR_REGISTER"

        const val KEY_FOR_SEND_TOKEN = "KEY_FOR_SEND_TOKEN"
    }

    private var bindingAuthorization: ActivityAuthorizationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bindingAuthorization = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(bindingAuthorization.root)

        this.bindingAuthorization = bindingAuthorization

        if (savedInstanceState == null) {
            showLoginFragment(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingAuthorization = null
    }

    override fun showLoginFragment(
        clearToTag: String?,
        isAddToBackStack: Boolean
    ) {
        val fragment = LoginFragment.newInstance()
        showFragment(TAG_FOR_LOGIN, fragment, clearToTag, isAddToBackStack)
    }

    override fun showRegisterFragment() {
        val fragment = RegisterFragment.newInstance()
        showFragment(TAG_FOR_REGISTER, fragment, TAG_FOR_LOGIN)
    }

    override fun showMainActivity(token: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(KEY_FOR_SEND_TOKEN, token)
        startActivity(intent)
    }

    private fun showFragment(
        tag: String,
        fragment: Fragment,
        clearToTag: String?,
        isAddToBackStack: Boolean = true
    ) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        if (clearToTag != null) {
            fragmentManager.popBackStack(
                clearToTag,
                0
            )
        }

        when {
            isAddToBackStack -> {
                transaction.replace(R.id.container, fragment, tag)
                    .addToBackStack(tag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
            }
            else -> {
                transaction.replace(R.id.container, fragment, tag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
            }
        }
    }
}
