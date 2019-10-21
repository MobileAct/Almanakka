package almanakka.ui.extensions

import android.os.Build
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView

fun AppCompatTextView.setTextAppearanceCompat(@StyleRes textAppearanceResId: Int) {
    if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
        setTextAppearance(textAppearanceResId)
    } else {
        // TextView.setTextAppearance(Context, Int) is deprecated in under API 23
        // but not deprecated AppCompatTextView.setTextAppearance(Context, Int)
        @Suppress("DEPRECATION")
        setTextAppearance(context, textAppearanceResId)
    }
}