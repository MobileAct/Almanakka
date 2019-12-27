package almanakka.ui

import almanakka.core.IDay
import almanakka.core.IWeek
import almanakka.core.animators.Progress
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.configurations.ViewConfig
import almanakka.ui.internal.ViewMeasure
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View


@SuppressLint("ViewConstructor")
internal class SelectedBackgroundView(
        context: Context,
        private val selectionProvider: ISelectionProvider,
        private val viewConfig: ViewConfig) : View(context) {

    private var behaviorContainer: IBehaviorContainer? = null
    private var week: IWeek? = null
    private val backgroundLine: Drawable = checkNotNull(context.getDrawable(viewConfig.selectedBackgroundLine))
    private val backgroundLeftEdge: Drawable = checkNotNull(context.getDrawable(viewConfig.selectedBackgroundEdge))
    private val backgroundRightEdge: Drawable = checkNotNull(context.getDrawable(viewConfig.selectedBackgroundEdge))
    private val selectedBackgroundDrawable = SelectedBackgroundDrawable(backgroundLine, backgroundLeftEdge, backgroundRightEdge)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        selectedBackgroundDrawable.draw(canvas)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        selectedBackgroundDrawable.state = drawableState
    }

    override fun invalidate() {
        super.invalidate()
        calculateBackgroundSpec(measuredWidth, measuredHeight)
    }

    private fun calculateBackgroundSpec(width: Int, height: Int) {
        var offsetLeft = 0
        var backgroundWidth = 0

        val states = week?.asSequence()
                ?.map {
                    if (it == null)
                        Pair<IDay?, Progress?>(null, null)
                    else
                        Pair<IDay?, Progress?>(it, selectionProvider.viewState.animator?.state(it))
                }
                ?.toList() ?: List(7) { Pair(null, null) }

        val leftSelectedState = states.firstOrNull { it.second != null && it.second != Progress.None }
        val rightSelectedState = states.lastOrNull { it.second != null && it.second != Progress.None }

        val dayWidths = ViewMeasure.measureDayWidth(viewConfig, width)

        for ((i, state) in states.withIndex()) {
            val day = state.first
            if (day == null || selectionProvider.viewState.animator?.state(day) == Progress.None) {
                offsetLeft += dayWidths[i] // array may be equals count
            } else {
                break
            }
        }

        for ((i, state) in states.withIndex()) {
            val progress = state.second ?: Progress.None
            if (progress is Progress.RightToLeft) {
                val percent = progress.percent
                val add = (dayWidths[i].toFloat() / 100 * percent).toInt()
                backgroundWidth += add
                offsetLeft += dayWidths[i] - add
                continue
            }
            if (progress is Progress.LeftToRight) {
                val percent = progress.percent
                backgroundWidth += (dayWidths[i].toFloat() / 100 * percent).toInt()
                continue
            }
            if (progress == Progress.Complete) {
                backgroundWidth += dayWidths[i] // array may be equals count
            }
        }

        // if 1 day selected, will be DayView.width equal to DayView.height
        val dayTextOvalMargin = (dayWidths.first() - height) / 2

        offsetLeft += viewConfig.monthPaddingSide

        val leftEdge = states.firstOrNull { it.second is Progress.RightToLeft } != null
        val rightEdge = states.lastOrNull { it.second is Progress.LeftToRight } != null
        val singleDay = leftSelectedState?.first != null && leftSelectedState.first == rightSelectedState?.first
                && leftSelectedState.second is Progress.Complete

        selectedBackgroundDrawable.dayTextOvalMargin = dayTextOvalMargin
        selectedBackgroundDrawable.setBounds(offsetLeft, 0, offsetLeft + backgroundWidth, height)
        selectedBackgroundDrawable.type = when {
            (leftEdge && rightEdge) || singleDay -> SelectedBackgroundDrawable.Type.SideEdge
            leftEdge -> SelectedBackgroundDrawable.Type.LeftEdge
            rightEdge -> SelectedBackgroundDrawable.Type.RightEdge
            else -> SelectedBackgroundDrawable.Type.Line
        }
        selectedBackgroundDrawable.measure()
    }

    fun updateState(behaviorContainer: IBehaviorContainer, week: IWeek?) {
        this.behaviorContainer = behaviorContainer
        this.week = week

        setPressed()

        invalidate()
    }

    private fun setPressed() {
        isPressed = selectionProvider.viewState.isSelectedBackgroundPressed
    }
}