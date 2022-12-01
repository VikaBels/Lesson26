package com.example.lesson26.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.lesson26.R
import android.view.animation.AnimationUtils
import com.example.lesson26.databinding.ActivityGreetingBinding

class GreetingActivity : AppCompatActivity() {
    companion object {
        const val TIME_SHOWING = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bindingActivityGreeting = ActivityGreetingBinding.inflate(layoutInflater)
        setContentView(bindingActivityGreeting.root)

        startAnimationRotation(bindingActivityGreeting.logo)

        setScreenDisplayTime()
    }

    private fun startAnimationRotation(view: View) {
        val rotate = AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_clockwise
        )
        view.startAnimation(rotate)
    }

    private fun setScreenDisplayTime() {
        Handler(
            Looper.getMainLooper()
        ).postDelayed({
            val intent = Intent(this, AuthorizationActivity::class.java)
            startActivity(intent)
            finish()
        }, TIME_SHOWING)
    }
}