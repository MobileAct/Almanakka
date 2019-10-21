package almanakka.core.providers

import almanakka.core.DayOfWeek
import almanakka.core.Month

interface IMonthCreator {

    /**
     * [month] range is 1 ~ 12
     */
    fun create(year: Short, month: Byte): Month

    fun createDayOfWeekOrders(): Array<DayOfWeek>
}