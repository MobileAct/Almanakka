package almanakka.core.providers

import almanakka.core.Day

interface IDayConnector {

    fun connect(front: Day, behind: Day)
}