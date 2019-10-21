package almanakka.app

import almanakka.ui.CalendarView
import almanakka.ui.events.DaySelectedEventArgs
import almanakka.ui.events.EventArgs
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sticky_scroll.*
import android.widget.Toast.LENGTH_SHORT as toastShort

class StickyScrollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sticky_scroll)

        calendar.daySelected += ::daySelected
    }

    override fun onDestroy() {
        calendar.daySelected -= ::daySelected

        super.onDestroy()
    }

    private fun daySelected(sender: CalendarView, args: EventArgs) {
        val daySelectedEventArgs = args as? DaySelectedEventArgs ?: return

        Toast.makeText(this, "Selected: ${daySelectedEventArgs.day}", toastShort).show()
    }
}