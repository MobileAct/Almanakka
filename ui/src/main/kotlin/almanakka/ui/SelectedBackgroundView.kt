package almanakka.ui

import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.configurations.ViewConfig
import almanakka.ui.extensions.convertToPixel
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.View.MeasureSpec.EXACTLY as exactlyMeasureSpec


@SuppressLint("ViewConstructor")
internal class SelectedBackgroundView(
        context: Context,
        private val selectionProvider: ISelectionProvider,
        private val viewConfig: ViewConfig,
        private val days: Array<IDayView>) : ViewGroup(context) {

    companion object {

        private const val sliderSizeDp = 12F

        private val stateNormal = intArrayOf(R.attr.state_normal)
        private val stateOnlyCornerLeft = intArrayOf(R.attr.state_only_corner_left)
        private val stateOnlyCornerRight = intArrayOf(R.attr.state_only_corner_right)
        private val stateNoCorner = intArrayOf(R.attr.state_no_corner)

        // FixMe: This define reason is only for Android Studio Bug.
        // I want define by import as, but it's recognized unused by Android Studio, so then auto delete when format import.
        private val visible = View.VISIBLE
        private val invisible = View.INVISIBLE
    }

    private var behaviorContainer: IBehaviorContainer? = null

    private var backgroundState = stateNormal
    private var backgroundWidth = 0
    var marginLeft = 0
        private set

    private val sliderSize: Int
    private val leftSlider: View
    private val rightSlider: View

    init {
        check(days.isNotEmpty()) { "days must not empty" }

        background = context.getDrawable(viewConfig.selectedBackgroundResId)

        sliderSize = context.convertToPixel(sliderSizeDp).toInt()

        leftSlider = View(context).apply {
            background = context.getDrawable(viewConfig.selectedSliderResId)

            this@SelectedBackgroundView.addView(this)
        }
        rightSlider = View(context).apply {
            background = context.getDrawable(viewConfig.selectedSliderResId)

            this@SelectedBackgroundView.addView(this)
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val state = super.onCreateDrawableState(extraSpace + 1)
        View.mergeDrawableStates(state, backgroundState)
        return state
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        calculateBackgroundSpec(days)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val contentWidth = Math.max(Math.min(width, backgroundWidth), 0)

        val sliderSpec = MeasureSpec.makeMeasureSpec(Math.min(sliderSize, contentWidth), exactlyMeasureSpec)
        leftSlider.measure(sliderSpec, sliderSpec)
        rightSlider.measure(sliderSpec, sliderSpec)

        setMeasuredDimension(contentWidth, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = measuredWidth
        val height = measuredHeight

        leftSlider.layout(
                0,
                (height / 2) - (leftSlider.measuredHeight / 2),
                leftSlider.measuredWidth,
                (height / 2) + (leftSlider.measuredHeight / 2)
        )
        rightSlider.layout(
                width - rightSlider.measuredWidth,
                (height / 2) - (rightSlider.measuredHeight / 2),
                width,
                (height / 2) + (rightSlider.measuredHeight / 2)
        )
    }

    private fun calculateBackgroundSpec(days: Array<IDayView>) {
        var offsetLeft = 0
        var width = 0

        for (day in days) {
            val d = day.getDay()
            if (d == null || behaviorContainer?.isSelected(d) != true) {
                offsetLeft += day.getMeasuredWidth()
            } else {
                break
            }
        }

        for (day in days) {
            val d = day.getDay()
            if (d != null && behaviorContainer?.isSelected(d) == true) {
                width += day.getMeasuredWidth()
            }
        }

        if (width == 0) {
            marginLeft = 0
            backgroundWidth = 0
            return
        }

        // if 1 day selected, will be DayView.width equal to DayView.height
        val dayTextOvalMargin = (days.first().getMeasuredWidth() - days.first().getMeasuredHeight()) / 2

        if (isLeftSelectingEdge(days)) {
            offsetLeft += viewConfig.monthPaddingSide
            offsetLeft += dayTextOvalMargin
            width -= dayTextOvalMargin
        } else {
            width += viewConfig.monthPaddingSide
        }

        if (isRightSelectingEdge(days)) {
            width -= dayTextOvalMargin
        } else {
            width += viewConfig.monthPaddingSide
        }

        marginLeft = offsetLeft
        backgroundWidth = width
    }

    fun updateState(behaviorContainer: IBehaviorContainer) {
        this.behaviorContainer = behaviorContainer
        calculateBackgroundState(behaviorContainer, days)

        setSliderVisibility(days)
        setTranslation()
        setPressed()

        requestLayout()
        invalidate()
    }

    private fun isLeftSelectingEdge(days: Array<IDayView>): Boolean {
        val firstDayView = days.first { it.getDay() != null }
        val firstDay = checkNotNull(firstDayView.getDay())
        val previousDay = firstDay.previousDay ?: return false
        return behaviorContainer?.isSelected(previousDay) != true
    }

    private fun isRightSelectingEdge(days: Array<IDayView>): Boolean {
        val lastDayView = days.last { it.getDay() != null }
        val lastDay = checkNotNull(lastDayView.getDay())
        val nextDay = lastDay.nextDay ?: return false
        return behaviorContainer?.isSelected(nextDay) != true
    }

    private fun calculateBackgroundState(behaviorContainer: IBehaviorContainer, days: Array<IDayView>) {
        val previousDay = days.first().getDay()?.previousDay
        val nextDay = days.last().getDay()?.nextDay

        if (previousDay != null && behaviorContainer.isSelected(previousDay) &&
                nextDay != null && behaviorContainer.isSelected(nextDay)) {
            backgroundState = stateNoCorner
        } else if (previousDay != null && behaviorContainer.isSelected(previousDay)) {
            backgroundState = stateOnlyCornerRight
        } else if (nextDay != null && behaviorContainer.isSelected(nextDay)) {
            backgroundState = stateOnlyCornerLeft
        } else {
            backgroundState = stateNormal
        }

        refreshDrawableState()
    }

    private var View.isVisible: Boolean
        get() = visibility == visible
        set(value) {
            visibility = if (value) visible else invisible
        }

    private fun setSliderVisibility(days: Array<IDayView>) {
        leftSlider.isVisible = isLeftSelectingEdge(days) && viewConfig.isVisibleSelectedSlider
        rightSlider.isVisible = isRightSelectingEdge(days) && viewConfig.isVisibleSelectedSlider
    }

    private fun setTranslation() {
        translationZ = selectionProvider.viewState.selectedTranslationZ
    }

    private fun setPressed() {
        isPressed = selectionProvider.viewState.isSelectedBackgroundPressed
    }
}