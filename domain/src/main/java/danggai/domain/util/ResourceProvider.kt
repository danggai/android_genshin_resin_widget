package danggai.domain.util


interface ResourceProvider {
    fun getString(resId: Int): String
}