package almanakka.ui

import almanakka.core.IWeek
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.configurations.ViewConfig
import almanakka.ui.extensions.convertToPixel
import almanakka.ui.internal.ViewMeasure
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.View.MeasureSpec.EXACTLY as exactlyMeasureSpec


@SuppressLint("ViewConstructor")
internal class SelectedBackgroundView(
        context: Context,
        private val selectionProvider: ISelectionProvider,
        private val viewConfig: ViewConfig) : ViewGroup(context) {

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
    private var week: IWeek? = null

    private var backgroundState = stateNormal
    private var backgroundWidth = 0
    var marginLeft = 0
        private set

    private val sliderSize: Int
    private val leftSlider: View
    private val rightSlider: View

    init {
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
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        calculateBackgroundSpec(width, height)

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

    private fun calculateBackgroundSpec(width: Int, height: Int) {
        var offsetLeft = 0
        var backgroundWidth = 0

        val dayWidths = ViewMeasure.measureDayWidth(viewConfig, width)

        for ((i, day) in week?.asSequence()?.withIndex() ?: emptySequence()) {
            if (day == null || behaviorContainer?.isSelected(day) != true) {
                offsetLeft += dayWidths[i] // array may be equals count
            } else {
                break
            }
        }

        for ((i, day) in week?.asSequence()?.withIndex() ?: emptySequence()) {
            if (day != null && behaviorContainer?.isSelected(day) == true) {
                backgroundWidth += dayWidths[i] // array may be equals count
            }
        }

        if (backgroundWidth == 0) {
            marginLeft = 0
            this.backgroundWidth = 0
            return
        }

        // if 1 day selected, will be DayView.width equal to DayView.height
        val dayTextOvalMargin = (dayWidths.first() - height) / 2

        if (isLeftSelectingEdge()) {
            offsetLeft += viewConfig.monthPaddingSide
            offsetLeft += dayTextOvalMargin
            backgroundWidth -= dayTextOvalMargin
        } else {
            backgroundWidth += viewConfig.monthPaddingSide
        }

        if (isRightSelectingEdge()) {
            backgroundWidth -= dayTextOvalMargin
        } else {
            backgroundWidth += viewConfig.monthPaddingSide
        }

        marginLeft = offsetLeft
        this.backgroundWidth = backgroundWidth
    }

    fun updateState(behaviorContainer: IBehaviorContainer, week: IWeek?) {
        this.behaviorContainer = behaviorContainer
        this.week = week
        calculateBackgroundState(behaviorContainer)

        setSliderVisibility()
        setTranslation()
        setPressed()

        requestLayout()
        invalidate()
    }

    private fun isLeftSelectingEdge(): Boolean {
        val previousDay = week?.firstDay?.previousDay ?: return false
        return behaviorContainer?.isSelected(previousDay) != true
    }

    private fun isRightSelectingEdge(): Boolean {
        val nextDay = week?.lastDay?.nextDay ?: return false
        return behaviorContainer?.isSelected(nextDay) != true
    }

    private fun calculateBackgroundState(behaviorContainer: IBehaviorContainer) {
        val previousDay = week?.firstDay?.previousDay
        val nextDay = week?.lastDay?.nextDay

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

    private fun setSliderVisibility() {
        leftSlider.isVisible = isLeftSelectingEdge() && viewConfig.isVisibleSelectedSlider
        rightSlider.isVisible = isRightSelectingEdge() && viewConfig.isVisibleSelectedSlider
    }

    private fun setTranslation() {
        translationZ = selectionProvider.viewState.selectedTranslationZ
    }

    private fun setPressed() {
        isPressed = selectionProvider.viewState.isSelectedBackgroundPressed
    }
}