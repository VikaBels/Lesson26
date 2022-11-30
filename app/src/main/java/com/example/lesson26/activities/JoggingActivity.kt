package com.example.lesson26.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.lesson26.fragments.JoggingFragment
import com.example.lesson26.R
import com.example.lesson26.activities.MainActivity.Companion.TAG_FOR_SEND_TOKEN
import com.example.lesson26.databinding.ActivityJoggingBinding
import com.example.lesson26.interfaes.JoggingFragmentListener

class JoggingActivity : AppCompatActivity(),
    JoggingFragmentListener {
    companion object {
        const val TAG_FOR_JOGGING_FRAGMENT = "TAG_FOR_JOGGING_FRAGMENT"
    }

    private var bindingJoggingActivity: ActivityJoggingBinding? = null
    private var isStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bindingJogging = ActivityJoggingBinding.inflate(layoutInflater)
        setContentView(bindingJogging.root)

        this.bindingJoggingActivity = bindingJogging

        if (savedInstanceState == null) {
            showJoggingFragment()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingJoggingActivity = null
    }

    override fun onBackPressed() {
        if (isStarted) {
            showToastError(resources.getString(R.string.error_timer_running), this)
        } else {
            super.onBackPressed()
        }
    }

    override fun onClickStart(isStarted: Boolean) {
        this.isStarted = isStarted
    }

    private fun showJoggingFragment() {
        val fragment = JoggingFragment.newInstance(getToken())

        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.container, fragment, TAG_FOR_JOGGING_FRAGMENT)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun getToken(): String? {
        return intent.getStringExtra(TAG_FOR_SEND_TOKEN)
    }

    private fun showToastError(textError: String?, context: Context) {
        val toast = Toast.makeText(context, textError, Toast.LENGTH_SHORT)
        toast.show()
    }
}


