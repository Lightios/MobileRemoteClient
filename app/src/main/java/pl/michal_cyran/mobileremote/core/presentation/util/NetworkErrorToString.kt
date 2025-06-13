package pl.michal_cyran.mobileremote.core.presentation.util

import android.content.Context
import pl.michal_cyran.mobileremote.R
import pl.michal_cyran.mobileremote.core.domain.util.NetworkError

fun NetworkError.toString(context: Context): String {
    val resId = when(this) {
        NetworkError.NO_INTERNET -> R.string.no_internet_connection
        NetworkError.SERIALIZATION -> R.string.serialization_error
        NetworkError.UNKNOWN -> R.string.unknown_error
        NetworkError.REQUEST_TIMEOUT -> R.string.error_request_timeout
        NetworkError.TOO_MANY_REQUESTS -> R.string.too_many_requests
        NetworkError.SERVER_ERROR -> R.string.server_error
    }

    return context.getString(resId)
}