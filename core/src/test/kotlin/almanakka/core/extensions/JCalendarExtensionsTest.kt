package almanakka.core.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.*

class JCalendarExtensionsTest {

    @Test
    fun testToDay() {
        val dateFormat = SimpleDateFormat("yyyy MM/dd")
        val date = dateFormat.parse("2018 10/15")
        val calendar = Calendar.getInstance().apply {
            time = date
        }

        val day = calendar.toDay()

        assertEquals(day.year, 2018)
        assertEquals(day.month, 10)
        assertEquals(day.day, 15)
    }
}