package almanakka.app

import almanakka.ui.CalendarController
import almanakka.ui.CalendarView
import almanakka.ui.events.DaySelectedEventArgs
import almanakka.ui.events.EventArgs
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pager.*
import android.widget.Toast.LENGTH_SHORT as toastShort

class PagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pager)

        calendar.daySelected += ::daySelected
        calendar.controller.scrolled += ::calendarScrolled

        previous.setOnClickListener {
            calendar.controller.smoothScrollToPosition(calendar.controller.findFirstVisibleItemPosition() - 1)
        }

        next.setOnClickListener {
            calendar.controller.smoothScrollToPosition(calendar.controller.findFirstVisibleItemPosition() + 1)
        }
    }

    override fun onDestroy() {
        calendar.daySelected -= ::daySelected
        calendar.controller.scrolled -= ::calendarScrolled

        super.onDestroy()
    }

    private fun daySelected(sender: CalendarView, args: EventArgs) {
        val daySelectedEventArgs = args as? DaySelectedEventArgs ?: return

        Toast.makeText(this, "Selected: ${daySelectedEventArgs.day}", toastShort).show()
    }

    private fun calendarScrolled(sender: CalendarController, args: EventArgs) {
        previous.isEnabled = 0 < sender.findFirstVisibleItemPosition()
        next.isEnabled = sender.findLastVisibleItemPosition() < sender.itemCount - 1
    }
}