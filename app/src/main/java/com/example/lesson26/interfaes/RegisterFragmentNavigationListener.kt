package com.example.lesson26.interfaes

interface RegisterFragmentNavigationListener {

    fun showLoginFragment(
        clearToTag: String?,
        isAddToBackStack: Boolean = true
    )
}