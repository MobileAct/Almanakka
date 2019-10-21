package almanakka.ui.extensions

import android.content.Context

fun Context.convertToPixel(dp: Float): Float {
    return dp * resources.displayMetrics.density
}