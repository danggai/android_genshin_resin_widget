package danggai.data.resource.repository

import android.content.Context
import danggai.domain.resource.repository.ResourceProviderRepository
import javax.inject.Inject

class ResourceProviderRepositoryImpl @Inject constructor(
    private val context: Context
): ResourceProviderRepository {

    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

//    fun getDrawable(resId: Int): Drawable? {
//        return context.getDrawable(resId)
//    }

}