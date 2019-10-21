package almanakka.app

import almanakka.core.Day
import almanakka.core.DayOfWeek
import almanakka.ui.DatePickerDialog
import almanakka.ui.events.DaySelectedEventArgs
import almanakka.ui.events.EventArgs
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast.LENGTH_SHORT as toastShort

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        normalSelection.setOnClickListener {
            startActivity(Intent(this, NormalSelectionActivity::class.java))
        }

        tapRangeSelection.setOnClickListener {
            startActivity(Intent(this, TapRangeSelectionActivity::class.java))
        }

        slideRangeSelection.setOnClickListener {
            startActivity(Intent(this, SlideRangeSelectionActivity::class.java))
        }

        pager.setOnClickListener {
            startActivity(Intent(this, PagerActivity::class.java))
        }

        stickyScroll.setOnClickListener {
            startActivity(Intent(this, StickyScrollActivity::class.java))
        }

        stickyPager.setOnClickListener {
            startActivity(Intent(this, StickyPagerActivity::class.java))
        }

        dynamicSetting.setOnClickListener {
            startActivity(Intent(this, DynamicSettingActivity::class.java))
        }

        datePicker.setOnClickListener {
            val picker = DatePickerDialog(this)
            picker.calendarView.setRange(Day(2017, 1, 1, DayOfWeek.firstday), Day(2018, 12, 1, DayOfWeek.firstday))
            picker.daySelected += ::pickerSelected
            picker.show()
        }
    }

    private fun pickerSelected(datePickerDialog: DatePickerDialog, args: EventArgs) {
        val daySelectedEventArgs = args as? DaySelectedEventArgs ?: return

        Toast.makeText(this, "Selected: ${daySelectedEventArgs.day}", toastShort).show()
        datePickerDialog.daySelected -= ::pickerSelected
    }
}
