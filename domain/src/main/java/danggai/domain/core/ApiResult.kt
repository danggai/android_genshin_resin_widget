package danggai.domain.core

sealed class ApiResult<T>{

    class Success<T>(
        val code: Int,
        val data: T
    ): ApiResult<T>()

    class Failure<T>(
        val code: Int,
        val message: String
    ): ApiResult<T>()

    class Error<T>(
        val throwable: Throwable
    ): ApiResult<T>()

    class Null<T> : ApiResult<T>()

}