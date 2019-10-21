package almanakka.app

import almanakka.ui.CalendarView
import almanakka.ui.events.EventArgs
import almanakka.ui.events.RangeDaySelectedEventArgs
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_slie_range_selection.*
import android.widget.Toast.LENGTH_SHORT as toastShort

class SlideRangeSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_slie_range_selection)

        calendar.daySelected += ::daySelected
    }

    override fun onDestroy() {
        calendar.daySelected -= ::daySelected

        super.onDestroy()
    }

    private fun daySelected(sender: CalendarView, args: EventArgs) {
        val rangeDaySelectedEventArgs = args as? RangeDaySelectedEventArgs ?: return

        Toast.makeText(this, "Selected: ${rangeDaySelectedEventArgs.days.size} days", toastShort).show()
    }
}