package com.example.practiceset2.util

data class DataState<T>(
    val message: GenericMessageInfo? = null,
    val data: T? = null
) {

    companion object {

        fun <T> error(
            message: GenericMessageInfo,
        ): DataState<T> {
            return DataState(
                message = message,
                data = null,
            )
        }

        fun <T> data(
            message: GenericMessageInfo? = null,
            data: T? = null,
        ): DataState<T> {
            return DataState(
                message = message,
                data = data,
            )
        }
    }
}