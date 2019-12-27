package almanakka.ui.providers

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable

class TapRangeBackgroundDrawable(
        private val line: Drawable,
        private val leftEdge: Drawable,
        private val rightEdge: Drawable
) : Drawable() {

    enum class Type {
        LeftEdge,
        RightEdge,
        SideEdge,
        Line
    }

    var type = Type.Line
    var dayTextOvalMargin = 0

    override fun draw(canvas: Canvas) {
        canvas.clipRect(bounds.left, bounds.top, bounds.right, bounds.bottom)
        when (type) {
            Type.Line -> {
                line.draw(canvas)
            }
            Type.LeftEdge -> {
                line.draw(canvas)
                leftEdge.draw(canvas)
            }
            Type.RightEdge -> {
                line.draw(canvas)
                rightEdge.draw(canvas)
            }
            Type.SideEdge -> {
                line.draw(canvas)
                leftEdge.draw(canvas)
                rightEdge.draw(canvas)
            }
        }
    }

    fun measure() {
        val height = bounds.height()
        when (type) {
            Type.Line -> {
                line.setBounds(bounds.left, 0, bounds.right, height)
            }
            Type.LeftEdge -> {
                line.setBounds(bounds.left + dayTextOvalMargin + height / 2, 0, bounds.right, height)
                leftEdge.setBounds(bounds.left + dayTextOvalMargin, 0, bounds.left + dayTextOvalMargin + height, height)
            }
            Type.RightEdge -> {
                line.setBounds(bounds.left, 0, bounds.right - dayTextOvalMargin - height / 2, height)
                rightEdge.setBounds(bounds.right - dayTextOvalMargin - height, 0, bounds.right - dayTextOvalMargin, height)
            }
            Type.SideEdge -> {
                line.setBounds(bounds.left + dayTextOvalMargin + height / 2, 0, bounds.right - dayTextOvalMargin - height / 2, height)
                leftEdge.setBounds(bounds.left + dayTextOvalMargin, 0, bounds.left + dayTextOvalMargin + height, height)
                rightEdge.setBounds(bounds.right - dayTextOvalMargin - height, 0, bounds.right - dayTextOvalMargin, height)
            }
        }
    }

    override fun setState(stateSet: IntArray): Boolean {
        line.state = stateSet
        leftEdge.state = stateSet
        rightEdge.state = stateSet
        return super.setState(stateSet)
    }

    override fun setAlpha(alpha: Int) {
        line.alpha = alpha
        leftEdge.alpha = alpha
        rightEdge.alpha = alpha
    }

    override fun getOpacity(): Int {
        @Suppress("DEPRECATION")
        return line.opacity
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        line.colorFilter = colorFilter
        leftEdge.colorFilter = colorFilter
        rightEdge.colorFilter = colorFilter
    }
}