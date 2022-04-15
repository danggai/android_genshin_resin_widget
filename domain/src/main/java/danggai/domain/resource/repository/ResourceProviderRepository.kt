package danggai.domain.resource.repository


interface ResourceProviderRepository {
    fun getString(resId: Int): String
}