package com.vks.locationtest.ui.dailog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.vks.locationtest.R

class DeleteConfirmDialog : DialogFragment() {
    var onOkClicked: OnClearClicked? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_delete_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.ok).setOnClickListener {
            dismiss()
            onOkClicked?.invoke()
        }
        view.findViewById<MaterialButton>(R.id.cancel).setOnClickListener {
            dismiss()
        }
    }
}