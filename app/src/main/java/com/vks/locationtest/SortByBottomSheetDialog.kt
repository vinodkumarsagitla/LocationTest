package com.vks.locationtest

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

internal typealias OnApplyClicked = (Int) -> Unit
internal typealias OnClearClicked = () -> Unit

class SortByBottomSheetDialog : BottomSheetDialogFragment() {
    var onApplyClicked: OnApplyClicked? = null
    var onClearClicked: OnClearClicked? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_sort, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // used to show the bottom sheet dialog
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.close).setOnClickListener { dismiss() }
        view.findViewById<MaterialButton>(R.id.clear).setOnClickListener {
            dismiss()
            onClearClicked?.invoke()
        }
        view.findViewById<MaterialButton>(R.id.apply).setOnClickListener {
            dismiss()
            val i = if (view.findViewById<RadioButton>(R.id.ascending).isChecked) 0 else 1
            onApplyClicked?.invoke(i)
        }
    }
}