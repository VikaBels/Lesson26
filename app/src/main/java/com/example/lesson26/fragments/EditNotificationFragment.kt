package com.example.lesson26.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.example.lesson26.App.Companion.getDataRepository
import com.example.lesson26.models.NotificationForEdit
import com.example.lesson26.R
import com.example.lesson26.databinding.FragmentEditNotificationBinding
import com.example.lesson26.factories.EditNotificationViewModelFactory
import com.example.lesson26.interfaes.AddEditNotificationFragmentListener
import com.example.lesson26.utils.refactorDateTime
import com.example.lesson26.viewmodels.EditNotificationViewModel
import java.util.*

class EditNotificationFragment : Fragment() {
    companion object {
        private const val NOTIFICATION_INFO = "NOTIFICATION_INFO"

        fun newInstance(notification: NotificationForEdit): Fragment {
            val editNotificationFragment = EditNotificationFragment()
            editNotificationFragment.arguments = bundleOf(NOTIFICATION_INFO to notification)
            return editNotificationFragment
        }
    }

    private var addEditNotificationFragmentListener: AddEditNotificationFragmentListener? = null
    private var bindingEditNotificationFragment: FragmentEditNotificationBinding? = null

    private var notification: NotificationForEdit? = null

    private val editNotificationViewModel by viewModels<EditNotificationViewModel> {
        EditNotificationViewModelFactory(
            getDataRepository()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addEditNotificationFragmentListener = context as? AddEditNotificationFragmentListener
            ?: error("$context${resources.getString(R.string.exceptionInterface)}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentEditNotificationBinding =
            FragmentEditNotificationBinding.inflate(layoutInflater)
        this.bindingEditNotificationFragment = fragmentEditNotificationBinding

        return fragmentEditNotificationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notification = getNotification()

        setNotificationInfo()

        observeErrorNotification()

        observeIsCorrectData()

        observeError()

        setUpListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingEditNotificationFragment = null
    }

    override fun onDetach() {
        super.onDetach()
        addEditNotificationFragmentListener = null
    }

    private fun observeIsCorrectData() {
        editNotificationViewModel.isCorrectData.observe(viewLifecycleOwner) { isCorrectData ->
            if (isCorrectData) {
                addEditNotificationFragmentListener?.showListTrackFragment()
            }
        }
    }

    private fun observeError() {
        editNotificationViewModel.error.observe(viewLifecycleOwner) { errorId ->
            Toast.makeText(
                requireContext(),
                getString(errorId.textId),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun observeErrorNotification() {
        editNotificationViewModel.errorNotificationField.observe(viewLifecycleOwner) { errorId ->
            showNotificationError(errorId)
        }
    }

    private fun getNotification(): NotificationForEdit? {
        return arguments?.getParcelable(NOTIFICATION_INFO)
    }

    private fun setNotificationInfo() {
        bindingEditNotificationFragment?.apply {
            editTextNotification.setText(notification?.notification?.text)
        }
    }

    private fun showNotificationError(errorId: Int) {
        bindingEditNotificationFragment?.inputLayoutNotification?.error =
            getString(errorId)
    }

    private fun setUpListeners() {
        bindingEditNotificationFragment?.btnChangeNotification?.setOnClickListener {
            editNotification()
        }
    }

    private fun editNotification() {
        val customCalendar = Calendar.getInstance()

        setCalendar(customCalendar)

        val customTime = customCalendar.timeInMillis
        val currentTime = System.currentTimeMillis()

        val notificationText =
            bindingEditNotificationFragment?.editTextNotification?.text?.toString()

        val currentNotification = notification

        if (currentNotification != null) {
            editNotificationViewModel.editNotification(
                currentNotification.notification.time,
                currentNotification.notification.text,
                refactorDateTime(customCalendar.time.time),
                notificationText,
                customTime,
                currentTime,
                currentNotification.token
            )
        }
    }

    private fun setCalendar(customCalendar: Calendar) {
        val fragmentEditNotificationBinding = this.bindingEditNotificationFragment

        if (fragmentEditNotificationBinding != null) {
            customCalendar.set(
                fragmentEditNotificationBinding.datePicker.year,
                fragmentEditNotificationBinding.datePicker.month,
                fragmentEditNotificationBinding.datePicker.dayOfMonth,
                fragmentEditNotificationBinding.timePiker.hour,
                fragmentEditNotificationBinding.timePiker.minute, 0
            )
        }
    }
}