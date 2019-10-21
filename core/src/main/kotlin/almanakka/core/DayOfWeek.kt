package almanakka.core

import java.util.Calendar.FRIDAY as fridayOfWeek
import java.util.Calendar.MONDAY as mondayOfWeek
import java.util.Calendar.SATURDAY as saturdayOfWeek
import java.util.Calendar.SUNDAY as sundayOfWeek
import java.util.Calendar.THURSDAY as thursdayOfWeek
import java.util.Calendar.TUESDAY as tuesdayOfWeek
import java.util.Calendar.WEDNESDAY as wednesdayOfWeek

sealed class DayOfWeek(val value: Int) {

    companion object {

        val firstday = Sunday
        val lastday = Saturday

        fun from(value: Int): DayOfWeek {
            return when (value) {
                Sunday.value -> Sunday
                Monday.value -> Monday
                Tuesday.value -> Tuesday
                Wednesday.value -> Wednesday
                Thursday.value -> Thursday
                Friday.value -> Friday
                Saturday.value -> Saturday
                else -> throw IllegalArgumentException("not day of week value, $value")
            }
        }
    }

    object Sunday : DayOfWeek(sundayOfWeek) {

        override fun previousDayOfWeek() = Saturday
        override fun nextDayOfWeek() = Monday
    }

    object Monday : DayOfWeek(mondayOfWeek) {

        override fun previousDayOfWeek() = Sunday
        override fun nextDayOfWeek() = Tuesday
    }

    object Tuesday : DayOfWeek(tuesdayOfWeek) {

        override fun previousDayOfWeek() = Monday
        override fun nextDayOfWeek() = Wednesday
    }

    object Wednesday : DayOfWeek(wednesdayOfWeek) {

        override fun previousDayOfWeek() = Tuesday
        override fun nextDayOfWeek() = Thursday
    }

    object Thursday : DayOfWeek(thursdayOfWeek) {

        override fun previousDayOfWeek() = Wednesday
        override fun nextDayOfWeek() = Friday
    }

    object Friday : DayOfWeek(fridayOfWeek) {

        override fun previousDayOfWeek() = Thursday
        override fun nextDayOfWeek() = Saturday
    }

    object Saturday : DayOfWeek(saturdayOfWeek) {

        override fun previousDayOfWeek() = Friday
        override fun nextDayOfWeek() = Sunday
    }

    abstract fun previousDayOfWeek(): DayOfWeek
    abstract fun nextDayOfWeek(): DayOfWeek
}