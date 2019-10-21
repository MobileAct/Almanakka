package almanakka.core.providers

import almanakka.core.Month

class EmptyMonthConnector : IMonthConnector {

    override fun connect(front: Month, behind: Month) {
    }
}