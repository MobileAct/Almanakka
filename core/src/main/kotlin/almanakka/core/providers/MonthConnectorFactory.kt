package almanakka.core.providers

object MonthConnectorFactory {

    fun create(isShowDaysOfDifferentMonth: Boolean): IMonthConnector {
        return when (isShowDaysOfDifferentMonth) {
            true -> MonthConnector()
            false -> EmptyMonthConnector()
        }
    }
}