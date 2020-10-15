package edu.bluejack20_1.dearmory.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import java.util.*


class TimePickerDialogFragment(
    private val setHour: Int,
    private val setMinute: Int
) : DialogFragment(), OnTimeSetListener {
    private lateinit var listener: TimePickerDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var theme = R.style.DialogDarkTheme
        when (ThemeManager.THEME_INDEX) {
            ThemeManager.LIGHT_THEME_INDEX -> theme = R.style.DialogLightTheme
            ThemeManager.GALAXY_THEME_INDEX -> theme = R.style.DialogGalaxyTheme
        }
        return TimePickerDialog(context, theme, this, setHour, setMinute, false)
    }

    override fun onTimeSet(timePicker: TimePicker, i: Int, i1: Int) {
        listener!!.onTimePickerDialogTimeSet(i, i1)
        dialog!!.dismiss()
    }

    fun setListener(listener: TimePickerDialogListener) {
        this.listener = listener
    }

    /**
     * listener
     */
    interface TimePickerDialogListener {
        fun onTimePickerDialogTimeSet(hour: Int, minute: Int)
    }
}