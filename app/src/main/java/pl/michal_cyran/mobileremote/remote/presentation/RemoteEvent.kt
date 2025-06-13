package pl.michal_cyran.mobileremote.remote.presentation

import pl.michal_cyran.mobileremote.core.domain.util.NetworkError

sealed interface RemoteEvent {
    data class Error(val error: NetworkError): RemoteEvent
}