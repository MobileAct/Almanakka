package almanakka.core.providers

import almanakka.core.Month

interface IMonthConnector {

    fun connect(front: Month, behind: Month)
}