package com.example.cafechapin.model

sealed class StoreError(cause: Throwable? = null) : Exception(cause) {

    class NoConnection(cause: Throwable? = null) : StoreError(cause)

    class Server(cause: Throwable? = null) : StoreError(cause)

    class UnexpectedResponse(cause: Throwable? = null) : StoreError(cause)

    class NotFound(cause: Throwable? = null) : StoreError(cause)

    class OutOfStock : StoreError()

    class PermissionDenied(cause: Throwable? = null) : StoreError(cause)

    class Unknown(cause: Throwable? = null) : StoreError(cause)
}
