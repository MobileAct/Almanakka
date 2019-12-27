package almanakka.ui

import almanakka.core.IDay
import almanakka.core.IMonth
import almanakka.core.behaviors.IBehaviorContainer
import almanakka.ui.configurations.DayConfig
import almanakka.ui.extensions.setDayTextStyleAndPrecomputedTextFuture
import android.annotation.SuppressLint
import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import android.view.Gravity.CENTER as centerGravity
import android.view.View.MeasureSpec.AT_MOST as atMostMeasureSpec
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT as wrapContent

@SuppressLint("ViewConstructor")
internal class DayView(
        context: Context,
        private val selectionProvider: ISelectionProvider,
        private val dayConfig: DayConfig) : FrameLayout(context), IDayView {

    private val text: AppCompatTextView
    private var currentDay: IDay? = null

    init {
        setOnClickListener(selectionProvider)
        text = AppCompatTextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(wrapContent, wrapContent).apply {
                gravity = centerGravity
            }
            setPadding(dayConfig.padding, dayConfig.padding, dayConfig.padding, dayConfig.padding)

            this@DayView.addView(this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        text.measure(
                MeasureSpec.makeMeasureSpec(width, atMostMeasureSpec),
                MeasureSpec.makeMeasureSpec(height, heightMode)
        )

        val measuredHeight = text.measuredHeight

        setMeasuredDimension(width, measuredHeight)
    }

    override fun setDay(behaviorContainer: IBehaviorContainer, month: IMonth, day: IDay?) {
        currentDay = day

        if (day == null) {
            text.text = ""
            isFocusable = false
            return
        } else {
            isFocusable = true
        }

        behaviorContainer.setDayTextStyleAndPrecomputedTextFuture(month, day, text)
    }

    override fun getDay(): IDay? {
        return currentDay
    }
}