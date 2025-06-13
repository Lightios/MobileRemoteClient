package pl.michal_cyran.mobileremote.remote.domain

import pl.michal_cyran.mobileremote.core.domain.util.Result
import pl.michal_cyran.mobileremote.core.domain.util.NetworkError

interface RemoteDataSource {
    fun setIpAddress(ipAddress: String)
    fun setPort(port: String)
    fun getIp(): String
    fun getPort(): String

    suspend fun setVolume(value: Int, isMuted: Boolean): Result<Unit, NetworkError>
    suspend fun togglePlay(): Result<Unit, NetworkError>
}