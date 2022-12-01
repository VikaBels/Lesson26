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
import com.example.lesson26.R
import com.example.lesson26.databinding.FragmentAddNotificationBinding
import com.example.lesson26.factories.AddNotificationViewModelFactory
import com.example.lesson26.interfaes.AddEditNotificationFragmentListener
import com.example.lesson26.utils.refactorDateTime
import com.example.lesson26.viewmodels.AddNotificationViewModel
import java.util.*

class AddNotificationFragment : Fragment() {
    companion object {
        private const val KEY_SEND_TOKEN = "KEY_SEND_TOKEN"

        fun newInstance(token: String?): Fragment {
            val addNotificationFragment = AddNotificationFragment()
            addNotificationFragment.arguments = bundleOf(KEY_SEND_TOKEN to token)
            return addNotificationFragment
        }
    }

    private var addEditNotificationFragmentListener: AddEditNotificationFragmentListener? = null
    private var bindingAddNotificationFragment: FragmentAddNotificationBinding? = null

    private val addNotificationViewModel by viewModels<AddNotificationViewModel> {
        AddNotificationViewModelFactory(
            getDataRepository(),
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
        val bindingAddNotificationFragment = FragmentAddNotificationBinding.inflate(layoutInflater)
        this.bindingAddNotificationFragment = bindingAddNotificationFragment

        return bindingAddNotificationFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeErrorNotificationText()

        observeIsCorrectData()

        observeError()

        setUpListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingAddNotificationFragment = null
    }

    override fun onDetach() {
        super.onDetach()
        addEditNotificationFragmentListener = null
    }

    private fun observeIsCorrectData() {
        addNotificationViewModel.isCorrectData.observe(viewLifecycleOwner) { isCorrectData ->
            if (isCorrectData) {
                addEditNotificationFragmentListener?.showListTrackFragment()
            }
        }
    }

    private fun observeError() {
        addNotificationViewModel.error.observe(viewLifecycleOwner) { errorId ->
            Toast.makeText(
                requireContext(),
                getString(errorId.textId),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun observeErrorNotificationText() {
        addNotificationViewModel.errorNotificationField.observe(viewLifecycleOwner) { errorId ->
            showNotificationError(errorId)
        }
    }

    private fun showNotificationError(errorId: Int) {
        bindingAddNotificationFragment?.inputLayoutNotification?.error =
            getString(errorId)
    }

    private fun getToken(): String? {
        return arguments?.getString(KEY_SEND_TOKEN)
    }

    private fun setUpListeners() {
        bindingAddNotificationFragment?.btnAddNotification?.setOnClickListener {
            addNotification()
        }
    }

    private fun addNotification() {
        val customCalendar = Calendar.getInstance()

        setCalendar(customCalendar)

        val customTime = customCalendar.timeInMillis
        val currentTime = System.currentTimeMillis()

        val notificationText =
            bindingAddNotificationFragment?.editTextNotification?.text?.toString()

        addNotificationViewModel.addNewNotification(
            notificationText,
            customTime,
            currentTime,
            refactorDateTime(customCalendar.time.time),
            getToken()
        )
    }

    private fun setCalendar(customCalendar: Calendar) {
        val bindingNotificationFragment = this.bindingAddNotificationFragment

        if (bindingNotificationFragment != null) {
            customCalendar.set(
                bindingNotificationFragment.datePicker.year,
                bindingNotificationFragment.datePicker.month,
                bindingNotificationFragment.datePicker.dayOfMonth,
                bindingNotificationFragment.timePicker.hour,
                bindingNotificationFragment.timePicker.minute, 0
            )
        }
    }
}
