package almanakka.ui

import almanakka.ui.events.DaySelectedEventArgs
import almanakka.ui.events.EventArgs
import almanakka.ui.events.EventHandler
import almanakka.ui.events.RangeDaySelectedEventArgs
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface.BUTTON_NEGATIVE as buttonNegative
import android.content.DialogInterface.BUTTON_POSITIVE as buttonPositive

class DatePickerDialog : AlertDialog, DialogInterface.OnClickListener {

    val daySelected = EventHandler<DatePickerDialog, EventArgs>()
    val calendarView: CalendarView

    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?)
            : super(context, cancelable, cancelListener)

    init {
        val inflater = LayoutInflater.from(context)
        @SuppressLint("InflateParams")
        val view = inflater.inflate(R.layout.dialog_date_picker, null)
        setView(view)

        setButton(buttonPositive, context.getString(android.R.string.ok), this)
        setButton(buttonNegative, context.getString(android.R.string.cancel), this)

        calendarView = view.findViewById(R.id.calendar)
        calendarView.daySelected += ::daySelected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val button = getButton(buttonPositive) ?: return
        button.isEnabled = false
    }

    private fun daySelected(sender: CalendarView, args: EventArgs) {
        when (args) {
            is DaySelectedEventArgs -> {
                val button = getButton(buttonPositive)
                button.isEnabled = true
            }
            is RangeDaySelectedEventArgs -> {
                val button = getButton(buttonPositive)
                button.isEnabled = args.days.isNotEmpty()
            }
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            buttonPositive -> {
                val currentEventArgs = calendarView.daySelected.currentEventArgs ?: return
                daySelected.raise(this, currentEventArgs)
            }
            buttonNegative -> {
                cancel()
            }
        }
    }
}