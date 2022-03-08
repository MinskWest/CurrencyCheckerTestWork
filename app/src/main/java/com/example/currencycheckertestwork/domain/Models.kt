package com.example.currencycheckertestwork.domain


data class CurrentCurrency(
    val value: List<Currency>
)

data class Currency(
    val name: String,
    val value: Double
)

data class FavouriteCurrency(
    val name: String,
    val value: Double
)

data class Result<out T>(
    val status: Status,
    val data: T?,
    val error: OurError?,
    val message: String?
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Result<T> {
            return Result(Status.SUCCESS, data, null, null)
        }

        fun <T> error(message: String, error: OurError?): Result<T> {
            return Result(Status.ERROR, null, error, message)
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(Status.LOADING, data, null, null)
        }
    }

    override fun toString(): String {
        return "Result(status=$status, data=$data, error=$error, message=$message)"
    }
}

data class OurError(
    val status_code: Int = 0,
    val status_message: String? = null
)