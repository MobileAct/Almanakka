package almanakka.core.animators

sealed class Progress {

    object Complete : Progress()

    data class LeftToRight(val percent: Int) : Progress()

    data class RightToLeft(val percent: Int) : Progress()

    object None : Progress()
}