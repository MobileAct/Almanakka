package almanakka.ui.events

class EventHandler<TSender, TArgs> where TArgs : EventArgs {

    var currentEventArgs: TArgs? = null
        private set

    private val listeners = mutableListOf<(TSender, TArgs) -> Unit>()

    operator fun plusAssign(listener: (TSender, TArgs) -> Unit) {
        listeners += listener
    }

    operator fun minusAssign(listener: (TSender, TArgs) -> Unit) {
        listeners -= listener
    }

    internal fun raise(sender: TSender, args: TArgs) {
        currentEventArgs = args

        for (listener in listeners) {
            listener(sender, args)
        }
    }
}