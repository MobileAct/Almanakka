package almanakka.core

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.junit.jupiter.api.Test
import java.io.File

class CalendarCollectionTest {

    class TestMonth(

            @SerializedName("Year")
            val year: Short,

            @SerializedName("MonthOfYear")
            val month: Byte,

            @SerializedName("Weeks")
            val weeks: Array<TestWeek>
    )

    class TestWeek(@SerializedName("Days") val days: Array<TestRelationalDay>)

    class TestRelationalDay(

            @SerializedName("PreviousDay")
            val previousDay: TestDay?,

            @SerializedName("Day")
            val day: TestDay,

            @SerializedName("NextDay")
            val nextDay: TestDay?
    )

    class TestDay(

            @SerializedName("Year")
            val year: Short,

            @SerializedName("Month")
            val month: Byte,

            @SerializedName("Day")
            val day: Byte,

            @SerializedName("DayOfWeek")
            val dayOfWeek: Int,

            @SerializedName("IsDisabled")
            val isDisabled: Boolean
    )

    private object TestDataRegex {

        private const val date = "([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})"

        val fileNameRegex = Regex("${date}_${date}_([a-zA-Z]*)_(False|True).json")

        enum class FileNameRegexGroup(val index: Int) {
            All(0),
            StartDateYear(1),
            StartDateMonth(2),
            StartDateDay(3),
            EndDateYear(4),
            EndDateMonth(5),
            EndDateDay(6),
            DayOfWeekOrderStart(7),
            IsContainDaysOfDifferentMonth(8)
        }
    }

    private fun readJson(fileName: String): Array<TestMonth> {
        javaClass.classLoader.getResourceAsStream(fileName).use {
            it.reader().use { reader ->
                val json = reader.readText()
                return Gson().fromJson(json, Array<TestMonth>::class.java)
            }
        }
    }

    @Test
    fun testJsonData() {
        val url = javaClass.classLoader.getResource("2018-1-1_2018-1-31_Monday_False.json")
        val directory = File(url.toURI()).parentFile


        for (file in directory.listFiles()) {
            val match = requireNotNull(TestDataRegex.fileNameRegex.matchEntire(file.name)) { file.name }

            val minDay = Day(
                    year = match.groupValues[TestDataRegex.FileNameRegexGroup.StartDateYear.index].toShort(),
                    month = match.groupValues[TestDataRegex.FileNameRegexGroup.StartDateMonth.index].toByte(),
                    day = match.groupValues[TestDataRegex.FileNameRegexGroup.StartDateDay.index].toByte(),
                    dayOfWeek = DayOfWeek.firstday
            )
            val maxDay = Day(
                    year = match.groupValues[TestDataRegex.FileNameRegexGroup.EndDateYear.index].toShort(),
                    month = match.groupValues[TestDataRegex.FileNameRegexGroup.EndDateMonth.index].toByte(),
                    day = match.groupValues[TestDataRegex.FileNameRegexGroup.EndDateDay.index].toByte(),
                    dayOfWeek = DayOfWeek.firstday
            )
            val dayOfWeekOrderStart = when (match.groupValues[TestDataRegex.FileNameRegexGroup.DayOfWeekOrderStart.index]) {
                "Sunday" -> DayOfWeek.Sunday
                "Monday" -> DayOfWeek.Monday
                "Tuesday" -> DayOfWeek.Tuesday
                "Wednesday" -> DayOfWeek.Wednesday
                "Thursday" -> DayOfWeek.Thursday
                "Friday" -> DayOfWeek.Friday
                "Saturday" -> DayOfWeek.Saturday
                else -> throw IllegalStateException("${file.name} is invalid format")
            }
            val isShowDaysOfDifferentMonth = match.groupValues[TestDataRegex.FileNameRegexGroup.IsContainDaysOfDifferentMonth.index].toBoolean()

            val calendarCollection = CalendarCollection.create(minDay, maxDay, dayOfWeekOrderStart, isShowDaysOfDifferentMonth)
            val testData = readJson(file.name)

            assertTestData(calendarCollection, testData)
        }
    }

    private fun assertTestData(data: CalendarCollection, testData: Array<TestMonth>) {
        assert(testData.size == data.size)

        for (i in 0 until testData.size) {
            val testMonth = testData[i]
            val month = data[i]

            assert(testMonth.year == month.year)
            assert(testMonth.month == month.month)
            assert(testMonth.weeks.size == month.weekSize)

            for (j in 0 until testMonth.weeks.size) {
                val testWeek = testMonth.weeks[j]
                val week = month[j]
                val days = week.asSequence().filterNotNull().toList()

                assert(testWeek.days.size == days.size)

                for (k in 0 until testWeek.days.size) {
                    val testDay = testWeek.days[k]
                    val day = days[k]

                    testDay.previousDay?.also {
                        val previousDay = checkNotNull(day.previousDay)
                        assert(it.year == previousDay.year)
                        assert(it.month == previousDay.month)
                        assert(it.day == previousDay.day)
                        // DayOfWeek is Deviated between JVM and .NET
                        assert(it.dayOfWeek == (previousDay.dayOfWeek.value - 1))
                        assert(it.isDisabled == data.behaviorContainer.isDisable(previousDay))
                    }

                    assert(testDay.day.year == day.year)
                    assert(testDay.day.month == day.month)
                    assert(testDay.day.day == day.day)
                    // DayOfWeek is Deviated between JVM and .NET
                    assert(testDay.day.dayOfWeek == (day.dayOfWeek.value - 1))
                    assert(testDay.day.isDisabled == data.behaviorContainer.isDisable(day))

                    testDay.nextDay?.also {
                        val nextDay = checkNotNull(day.nextDay)
                        assert(it.year == nextDay.year)
                        assert(it.month == nextDay.month)
                        assert(it.day == nextDay.day)
                        // DayOfWeek is Deviated between JVM and .NET
                        assert(it.dayOfWeek == (nextDay.dayOfWeek.value - 1))
                        assert(it.isDisabled == data.behaviorContainer.isDisable(nextDay))
                    }
                }
            }
        }
    }
}