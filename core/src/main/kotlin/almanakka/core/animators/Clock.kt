package almanakka.core.animators

object Clock : IClock {

    override fun currentTimeMilliSeconds(): Long {
        return System.currentTimeMillis()
    }
}