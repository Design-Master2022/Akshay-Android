package com.newsapp.newsapp.server

/**
 * A sealed class that encapsulates the different states of a data operation.
 *
 * @param T The type of the data being operated upon.
 * @property data The data result of the operation. It can be null in some states.
 * @property message An optional message providing additional information about the operation.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Represents a successful data operation state.
     *
     * @param data The data result of the operation.
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Represents an error data operation state.
     *
     * @param message An optional error message.
     * @param data The data result of the operation. It can be null in error states.
     */
    class Error<T>(message: String?, data: T? = null) : Resource<T>(data, message)

    /**
     * Represents a Resource subclass indicating that the requested data is not cached.
     *
     * @param message Optional error message explaining the reason for data not being cached.
     * @param data The data associated with the resource.
     */
    class DataNotCached<T>(message: String?, data: T? = null) : Resource<T>(data, message)

    /**
     * Represents a loading data operation state.
     */
    class Loading<T> : Resource<T>()
}
