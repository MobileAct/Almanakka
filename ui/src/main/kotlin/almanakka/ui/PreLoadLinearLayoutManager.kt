package almanakka.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreLoadLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    private val displayHeight: Int = context.resources.displayMetrics.heightPixels

    override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
        return displayHeight
    }
}