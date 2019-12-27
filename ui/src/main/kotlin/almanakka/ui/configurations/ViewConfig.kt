package almanakka.ui.configurations

import almanakka.ui.IAttributeSet
import almanakka.ui.R
import almanakka.ui.extensions.convertToPixel
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.Px

class ViewConfig(context: Context, attrs: IAttributeSet? = null, defStyleAttr: Int = -1) {

    companion object {

        private const val defaultMonthPaddingTopDp = 8F
        private const val defaultMonthPaddingBottomDp = 8F
        private const val defaultMonthPaddingSideDp = 8F
    }

    @Px
    val monthPaddingTop: Int

    @Px
    val monthPaddingBottom: Int

    @Px
    val monthPaddingSide: Int

    @DrawableRes
    val selectedBackgroundLine: Int

    @DrawableRes
    val selectedBackgroundEdge: Int


    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, 0)

        monthPaddingTop = typedArray.getDimensionPixelSize(
                R.styleable.CalendarView_monthPaddingTop,
                context.convertToPixel(defaultMonthPaddingTopDp).toInt()
        )
        monthPaddingBottom = typedArray.getDimensionPixelSize(
                R.styleable.CalendarView_monthPaddingBottom,
                context.convertToPixel(defaultMonthPaddingBottomDp).toInt()
        )
        monthPaddingSide = typedArray.getDimensionPixelOffset(
                R.styleable.CalendarView_monthPaddingSide,
                context.convertToPixel(defaultMonthPaddingSideDp).toInt()
        )
        selectedBackgroundLine = typedArray.getResourceId(R.styleable.CalendarView_selectedBackgroundLine, R.drawable.background_line)
        selectedBackgroundEdge = typedArray.getResourceId(R.styleable.CalendarView_selectedBackgroundEdge, R.drawable.background_edge)

        typedArray.recycle()
    }
}