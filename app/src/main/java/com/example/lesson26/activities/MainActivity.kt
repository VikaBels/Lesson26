package com.example.lesson26.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lesson26.R
import com.example.lesson26.activities.AuthorizationActivity.Companion.KEY_FOR_SEND_TOKEN
import com.example.lesson26.adapters.MenuItemAdapter
import com.example.lesson26.databinding.ActivityMainBinding
import com.example.lesson26.factories.MainActivityViewModelFactory
import com.example.lesson26.fragments.*
import com.example.lesson26.interfaes.*
import com.example.lesson26.models.TrackInfo
import com.example.lesson26.models.Track
import com.example.lesson26.models.Notification
import com.example.lesson26.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity(),
    MenuNavigationListener,
    TracksScreenNavigationListener,
    ListNotificationScreenNavigationListener,
    ExitConfirmationDialogListener,
    AddNotificationFragmentListener,
    EditNotificationFragmentListener {
    companion object {
        const val TAG_FOR_LIST_TRACK = "TAG_FOR_LIST_TRACK"
        const val TAG_FOR_TRACK = "TAG_FOR_TRACK"

        const val TAG_FOR_LIST_NOTIFICATION = "TAG_FOR_LIST_NOTIFICATION"
        const val TAG_FOR_ADD_NOTIFICATION = "TAG_FOR_ADD_NOTIFICATION"
        const val TAG_FOR_EDIT_NOTIFICATION = "TAG_FOR_EDIT_NOTIFICATION"

        const val TAG_FOR_SEND_TOKEN = "TAG_FOR_SEND_TOKEN"
    }

    private var activityMainBinding: ActivityMainBinding? = null
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private var adapterMenu: MenuItemAdapter? = null
    private var token: String? = null

    private val mainActivityViewModel by viewModels<MainActivityViewModel> {
        MainActivityViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        this.activityMainBinding = activityMainBinding
        adapterMenu = MenuItemAdapter(this)

        setTransmissionData()

        if (savedInstanceState == null) {
            showListTrackFragment(null)
        }

        addToolBar()

        setUpAdapter()

        observeListMenu()
    }

    private fun observeListMenu() {
        mainActivityViewModel.currentListMenu.observe(this) { listTrack ->
            adapterMenu?.setList(listTrack)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityMainBinding = null
        adapterMenu = null
    }

    override fun showListTrackFragment() {
        showListTrackFragment(TAG_FOR_LIST_TRACK, false)
    }

    override fun logOut() {
        val intent = Intent(this, AuthorizationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun openFragment(idNameFragment: Int) {
        when (idNameFragment) {
            R.string.home_page -> {
                showListTrackFragment(TAG_FOR_LIST_TRACK, false)
            }
            R.string.list_notification_page -> {
                showListNotificationFragment()
            }
            R.string.exit_app -> {
                showExitConfirmationDialog()
            }
        }

        activityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
    }

    override fun showTrackFragment(track: Track) {
        val sendingInfo = token?.let { token ->
            TrackInfo(
                track,
                token
            )
        }

        val fragment = TrackFragment.newInstance(sendingInfo)
        showFragment(TAG_FOR_TRACK, fragment, TAG_FOR_LIST_TRACK)
    }

    override fun showJoggingActivity() {
        val intent = Intent(this, JoggingActivity::class.java)
        intent.putExtra(TAG_FOR_SEND_TOKEN, token)
        startActivity(intent)
    }

    override fun showAddNotificationFragment() {
        val fragment = AddNotificationFragment.newInstance(token)
        showFragment(TAG_FOR_ADD_NOTIFICATION, fragment, TAG_FOR_LIST_TRACK)
    }

    override fun showEditNotificationFragment(notification: Notification) {
        val fragment = EditNotificationFragment.newInstance(notification)
        showFragment(TAG_FOR_EDIT_NOTIFICATION, fragment, TAG_FOR_LIST_TRACK)
    }

    private fun showListNotificationFragment() {
        val fragment = ListNotificationFragment.newInstance(token)
        showFragment(TAG_FOR_LIST_NOTIFICATION, fragment, TAG_FOR_LIST_TRACK)
    }

    private fun showListTrackFragment(
        clearToTag: String?,
        isAddToBackStack: Boolean = true
    ) {
        val fragment = ListTrackFragment.newInstance(token)
        showFragment(TAG_FOR_LIST_TRACK, fragment, clearToTag, isAddToBackStack)
    }

    private fun showFragment(
        tag: String?,
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

    private fun addToolBar() {
        val drawerLayout = activityMainBinding?.drawerLayout
        val toolBar = activityMainBinding?.toolBar

        setSupportActionBar(toolBar)

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.open,
            R.string.close
        )
        drawerLayout?.addDrawerListener(drawerToggle)
    }

    private fun setUpAdapter() {
        activityMainBinding?.menuList?.apply {
            adapter = adapterMenu
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showExitConfirmationDialog() {
        ExitConfirmationDialogFragment().apply {
            isCancelable = false
            show(supportFragmentManager, null)
        }
    }

    private fun setTransmissionData() {
        token = intent.extras?.getString(KEY_FOR_SEND_TOKEN)
    }
}