package danggai.data.util

import android.content.Context
import android.graphics.drawable.Drawable
import danggai.domain.util.ResourceProvider
import javax.inject.Inject

class ResourceProviderImpl @Inject constructor(
    private val context: Context
): ResourceProvider{

    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

//    fun getDrawable(resId: Int): Drawable? {
//        return context.getDrawable(resId)
//    }

}