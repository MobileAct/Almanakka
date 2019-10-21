package almanakka.ui

import almanakka.ui.configurations.Config
import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StickyItemDecoration(
        private val config: Config,
        private val viewProperty: ViewProperty,
        backgroundColor: Int) : RecyclerView.ItemDecoration() {

    private val stickyBackgroundPaint = Paint().apply {
        color = backgroundColor
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        if (parent.childCount == 0) {
            return
        }

        val linearLayoutManager = parent.layoutManager as? LinearLayoutManager ?: return
        val topChildPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val topChild = linearLayoutManager.findViewByPosition(topChildPosition)

        val monthView = (topChild as? IMonthContainerView)?.monthView ?: return
        val monthLabelView = monthView.findMonthLabel() ?: return

        val nextChild = linearLayoutManager.findViewByPosition(topChildPosition + 1)

        val nextMonthView = (nextChild as? IMonthContainerView)?.monthView ?: return
        val nextMonthLabelView = nextMonthView.findMonthLabel() ?: return

        draw(c, monthLabelView, nextMonthLabelView, nextMonthView.top)
    }

    private fun draw(
            c: Canvas,
            monthLabelView: IMonthLabelView,
            nextMonthLabelView: IMonthLabelView,
            nextMonthViewScrollY: Int) {
        val stickyHeight = (config.viewConfig.monthPaddingTop + monthLabelView.getMeasuredHeight()).toFloat()

        c.save()
        c.translate(0F, 0F)
        c.drawRect(
                0F,
                0F,
                monthLabelView.getMeasuredWidth().toFloat(),
                stickyHeight,
                stickyBackgroundPaint
        )
        when (viewProperty.orientation) {
            ViewProperty.Orientation.Vertical -> {
                if (stickyHeight < nextMonthViewScrollY + config.viewConfig.monthPaddingTop) {
                    drawStatic(c, monthLabelView)
                } else {
                    drawMoving(c, monthLabelView, nextMonthLabelView, nextMonthViewScrollY)
                }
            }
            ViewProperty.Orientation.Horizontal -> {
                drawStatic(c, monthLabelView)
            }
        }
        c.restore()
    }

    private fun drawStatic(c: Canvas, monthLabelView: IMonthLabelView) {
        c.translate(0F, config.viewConfig.monthPaddingTop.toFloat())
        monthLabelView.draw(c)
    }

    private fun drawMoving(
            c: Canvas,
            monthLabelView: IMonthLabelView,
            nextMonthLabelView: IMonthLabelView,
            nextMonthViewScrollY: Int) {
        val nextMonthLabelScrollY = (nextMonthViewScrollY + config.viewConfig.monthPaddingTop).toFloat()

        c.translate(0F, nextMonthLabelScrollY - monthLabelView.getMeasuredHeight())
        monthLabelView.draw(c)
        c.translate(0F, monthLabelView.getMeasuredHeight().toFloat())
        nextMonthLabelView.draw(c)
    }

    private fun MonthView.findMonthLabel(): IMonthLabelView? {
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            if (child is IMonthLabelView) {
                return child
            }
        }
        return null
    }
}