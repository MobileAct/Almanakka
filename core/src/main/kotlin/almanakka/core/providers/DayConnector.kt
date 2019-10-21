package almanakka.core.providers

import almanakka.core.Day

class DayConnector : IDayConnector {

    override fun connect(front: Day, behind: Day) {
        front.nextDay = behind
        behind.previousDay = front
    }
}