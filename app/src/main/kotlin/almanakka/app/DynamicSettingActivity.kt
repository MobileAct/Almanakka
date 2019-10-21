package almanakka.app

import almanakka.core.Day
import almanakka.core.extensions.toDay
import almanakka.ui.CalendarView
import almanakka.ui.events.DaySelectedEventArgs
import almanakka.ui.events.EventArgs
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_dynamic_setting.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast.LENGTH_SHORT as toastShort
import java.util.Calendar as JCalendar

class DynamicSettingActivity : AppCompatActivity() {

    companion object {

        private val dateFormat = SimpleDateFormat("yyyy MM/dd", Locale.US)

        private fun String.toJCalendar(): JCalendar {
            val date = dateFormat.parse(this)
            return JCalendar.getInstance().apply {
                this.time = date
            }
        }
    }

    private val rangeStartText: String
        get() = rangeStart.selectedItem as String
    private val rangeEndText: String
        get() = rangeEnd.selectedItem as String

    private lateinit var minDay: Day
    private lateinit var maxDay: Day

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dynamic_setting)

        if (savedInstanceState == null) {
            // called when initialized
            minDay = rangeStartText.toJCalendar().toDay()
            maxDay = rangeEndText.toJCalendar().toDay()
            calendar.setRange(minDay, maxDay)
        }

        rangeStart.isFocusable = false
        rangeStart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (rangeStart.isFocusable == false) {
                    // return when restoring
                    rangeStart.isFocusable = true
                    return
                }
                minDay = rangeStartText.toJCalendar().toDay()
                maxDay = rangeEndText.toJCalendar().toDay()
                calendar.setRange(minDay, maxDay)
            }
        }

        rangeEnd.isFocusable = false
        rangeEnd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (rangeEnd.isFocusable == false) {
                    // return when restoring
                    rangeEnd.isFocusable = true
                    return
                }
                minDay = rangeStartText.toJCalendar().toDay()
                maxDay = rangeEndText.toJCalendar().toDay()
                calendar.setRange(minDay, maxDay)
            }
        }

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