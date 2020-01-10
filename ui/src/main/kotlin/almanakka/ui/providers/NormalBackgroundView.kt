package almanakka.ui.providers

import almanakka.core.IWeek
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.BackgroundView
import almanakka.ui.configurations.ViewConfig
import almanakka.ui.internal.ViewMeasure
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable

@SuppressLint("ViewConstructor")
class NormalBackgroundView(context: Context, private val viewConfig: ViewConfig) : BackgroundView(context) {

    private var behaviorContainer: IBehaviorContainer? = null
    private var week: IWeek? = null
    private val backgroundEdge: Drawable = checkNotNull(context.getDrawable(viewConfig.selectedBackgroundEdge))
    private var offsetX = 0
    private var size = 0
    private var show = false

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        if (show.not()) {
            return
        }

        backgroundEdge.setBounds(0, 0, size, size)
        canvas.translate(offsetX.toFloat(), 0f)
        backgroundEdge.draw(canvas)
        canvas.translate(-offsetX.toFloat(), 0f)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        backgroundEdge.state = drawableState
    }

    override fun invalidate() {
        super.invalidate()
        calculateBackgroundSpec(measuredWidth, measuredHeight)
    }

    private fun calculateBackgroundSpec(width: Int, height: Int) {
        val behaviorContainer = behaviorContainer ?: return
        var offsetX = viewConfig.monthPaddingSide
        var size = 0
        var show = false
        val dayWidths = ViewMeasure.measureDayWidth(viewConfig, width)

        for ((i, day) in week?.asSequence()?.withIndex() ?: emptySequence()) {
            if (day != null && behaviorContainer.isSelected(day)) {
                show = true
                offsetX += (dayWidths[i] - height) / 2
                size = height
                break
            }
            offsetX += dayWidths[i] // maybe same array size
        }

        this.offsetX = offsetX
        this.size = size
        this.show = show
    }

    override fun updateState(behaviorContainer: IBehaviorContainer, week: IWeek?) {
        this.behaviorContainer = behaviorContainer
        this.week = week

        invalidate()
    }
}