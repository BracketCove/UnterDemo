package com.bracketcove


sealed interface ServiceResult<out T> {
    data class Success<T> (val value: T): ServiceResult<T>
    data class Failure<T>(val exception: Exception): ServiceResult<T>
}
