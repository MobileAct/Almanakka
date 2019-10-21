package almanakka.ui

class ViewProperty {

    enum class Orientation {
        Vertical, Horizontal
    }

    var width: Int = 0
        internal set

    var height: Int = 0
        internal set

    var orientation = Orientation.Vertical
        internal set
}