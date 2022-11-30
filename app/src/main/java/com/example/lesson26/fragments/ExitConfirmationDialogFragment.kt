package com.example.lesson26.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.lesson26.R
import com.example.lesson26.interfaes.ExitConfirmationDialogListener

class ExitConfirmationDialogFragment : DialogFragment() {
    private var exitConfirmationDialogListener: ExitConfirmationDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        exitConfirmationDialogListener = context as? ExitConfirmationDialogListener
            ?: error("$context${resources.getString(R.string.exceptionInterface)}")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.txt_exit))
            .setMessage(resources.getString(R.string.txt_exit_confirmation))
            .setPositiveButton(resources.getString(R.string.btn_OK)) { _, _ ->
                exitConfirmationDialogListener?.logOut()
            }
            .setNegativeButton(resources.getString(R.string.btn_CANCEL)) { _, _ -> }
            .create()
    }

    override fun onDetach() {
        super.onDetach()
        exitConfirmationDialogListener = null
    }
}