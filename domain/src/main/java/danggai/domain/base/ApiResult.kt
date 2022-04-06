package danggai.domain.base

data class ApiResult<T>(
    val meta: Meta,
    val data: T
)
