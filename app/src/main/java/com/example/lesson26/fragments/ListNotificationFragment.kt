package com.example.lesson26.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lesson26.App
import com.example.lesson26.R
import com.example.lesson26.adapters.NotificationAdapter
import com.example.lesson26.databinding.FragmentListNotificationBinding
import com.example.lesson26.factories.ListNotificationViewModelFactory
import com.example.lesson26.interfaes.NotificationListener
import com.example.lesson26.interfaes.ListNotificationScreenNavigationListener
import com.example.lesson26.models.Notification
import com.example.lesson26.viewmodels.ListNotificationViewModel

class ListNotificationFragment : Fragment(),
    NotificationListener {
    companion object {
        private const val KEY_SEND_TOKEN = "KEY_SEND_TOKEN"

        fun newInstance(token: String?): Fragment {
            val listNotificationFragment = ListNotificationFragment()
            listNotificationFragment.arguments = bundleOf(KEY_SEND_TOKEN to token)
            return listNotificationFragment
        }
    }

    private var listNotificationScreenNavigationListener: ListNotificationScreenNavigationListener? = null

    private var bindingListNotificationFragment: FragmentListNotificationBinding? = null
    private var notificationAdapter: NotificationAdapter? = null

    private val listNotificationViewModel by viewModels<ListNotificationViewModel> {
        ListNotificationViewModelFactory(
            App.getDataRepository(),
            getToken()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listNotificationScreenNavigationListener = context as? ListNotificationScreenNavigationListener
            ?: error("$context${resources.getString(R.string.exceptionInterface)}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val listNotificationBinding = FragmentListNotificationBinding.inflate(layoutInflater)
        this.bindingListNotificationFragment = listNotificationBinding

        return listNotificationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationAdapter = NotificationAdapter(this)

        setupAdapter()

        observeError()

        observeListTrack()

        setUpListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingListNotificationFragment = null
    }

    override fun onDetach() {
        super.onDetach()
        listNotificationScreenNavigationListener = null
    }

    override fun onNotificationClick(notification: Notification) {
        listNotificationScreenNavigationListener?.showEditNotificationFragment(notification)
    }

    override fun onBtnDeleteClick(notification: Notification) {
        //getToken() - not cool !!!!
        listNotificationViewModel.deleteNotification(
            notification.time,
            notification.text,
            getToken()
        )
    }

    private fun observeError() {
        listNotificationViewModel.error.observe(viewLifecycleOwner) { error ->
            setTextError(error.textId)
        }
    }

    private fun observeListTrack() {
        listNotificationViewModel.listNotification.observe(viewLifecycleOwner) { listNotification ->
            if (listNotification.isEmpty()) {
                setTextError(R.string.error_no_notification_list)
            }
            notificationAdapter?.setListNotification(listNotification)
        }
    }

    private fun setTextError(idError: Int) {
        bindingListNotificationFragment?.apply {
            textError.text = resources.getString(idError)
            textError.isVisible = true
        }
    }

    private fun getToken(): String?{
        return arguments?.getString(KEY_SEND_TOKEN)
    }

    private fun setupAdapter() {
        bindingListNotificationFragment?.listNotification?.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setUpListener() {
        bindingListNotificationFragment?.fabAddNotification?.setOnClickListener {
            listNotificationScreenNavigationListener?.showAddNotificationFragment()
        }
    }
}