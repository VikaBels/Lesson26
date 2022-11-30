package com.example.lesson26.interfaes

interface LoginFragmentNavigationListener {

    fun showRegisterFragment()

    fun showMainActivity(token: String?)
}